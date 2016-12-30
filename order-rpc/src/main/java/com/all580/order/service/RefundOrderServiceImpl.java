package com.all580.order.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.RefundOrderService;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.manager.SmsManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订服务
 * @date 2016/10/11 10:49
 */
@Service
@Slf4j
public class RefundOrderServiceImpl implements RefundOrderService {
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private SmsManager smsManager;

    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> cancel(Map params) {
        if (!params.containsKey(EpConstant.EpKey.EP_ID)) {
            throw new ApiException("非法请求:企业ID为空");
        }
        Order order = orderMapper.selectBySN(Long.valueOf(params.get("order_sn").toString()));
        if (!String.valueOf(params.get(EpConstant.EpKey.EP_ID)).equals(String.valueOf(order.getBuy_ep_id()))
                && !String.valueOf(params.get(EpConstant.EpKey.CORE_EP_ID)).equals(String.valueOf(order.getPayee_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能取消该订单");
        }
        return refundOrderManager.cancel(order);
    }

    @Override
    public Result<?> apply(Map params) throws Exception {
        String orderItemSn = params.get("order_item_sn").toString();
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(orderItemSn));
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        // 供应侧/销售侧
        Integer applyFrom = CommonUtil.objectParseInteger(params.get("apply_from"));

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemSn, lockTimeOut);

        // 锁成功
        try {
            return apply(params, orderItem, order, applyFrom);
        } finally {
            lock.unlock();
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> apply(Map params, OrderItem orderItem, Order order, Integer applyFrom) throws Exception {
        if (ArrayUtils.indexOf(new int[]{
                OrderConstant.OrderItemStatus.SEND,
                OrderConstant.OrderItemStatus.NON_SEND,
                OrderConstant.OrderItemStatus.TICKET_FAIL
        }, orderItem.getStatus()) < 0 ||
                order.getStatus() != OrderConstant.OrderStatus.PAID) {
            throw new ApiException("订单不在可退订状态");
        }
        if (!params.containsKey(EpConstant.EpKey.EP_ID)) {
            throw new ApiException("非法请求:企业ID为空");
        }

        int epId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID));
        // 检查权限
        refundOrderManager.checkApplyRefund(orderItem, order, applyFrom, epId);

        // 每日订单详情
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
        // 每日退票详情
        List daysList = (List) params.get("days");
        // 总退票数量
        Integer quantity = CommonUtil.objectParseInteger(params.get("quantity"));
        String cause = CommonUtil.objectParseString(params.get("cause"));

        if (daysList == null) {
            throw new ApiException("缺少退票详情");
        }

        Date refundDate = new Date();
        // 计算退款金额
        int money = 0;
        // 手续费
        int fee = 0;
        // 到付不计算
        if (orderItem.getPayment_flag() != ProductConstants.PayType.PAYS) {
            int[] calcResult = refundOrderManager.calcRefundMoney(applyFrom, daysList, detailList, orderItem.getId(), order.getBuy_ep_id(),
                    refundOrderManager.getCoreEpId(refundOrderManager.getCoreEpId(order.getBuy_ep_id())), refundDate);
            money = calcResult[0];
            fee = calcResult[1];
        }
        if (money < 0) {
            throw new ApiException("销售价小于退货手续费");
        }

        // 获取退订审核
        int[] auditSupplierConfig = getAuditConfig(orderItem.getPro_sub_id(), orderItem.getSupplier_core_ep_id());
        int auditTicket = auditSupplierConfig[0];
        // 获取退款审核
        int auditMoney = 0;
        if (orderItem.getSupplier_core_ep_id() == order.getPayee_ep_id().intValue()) {
            auditMoney = auditSupplierConfig[1];
        } else {
            auditMoney =  getAuditConfig(orderItem.getPro_sub_id(), order.getPayee_ep_id())[1];
        }
        // 创建退订订单
        RefundOrder refundOrder = refundOrderManager.generateRefundOrder(orderItem, daysList, quantity, money, fee, cause, auditTicket, auditMoney, order.getPayee_ep_id());

        // 判断余票 并修改明细退票数量 创建游客退票信息
        int tmpQuantity = refundOrderManager.canRefundForDays(daysList, detailList, refundOrder.getOrder_item_id(), refundOrder.getId());
        if (tmpQuantity != quantity) {
            throw new ApiException("退票总数与每天退票数不符");
        }

        // 退订分账 到付退订不分帐
        if (orderItem.getPayment_flag() != ProductConstants.PayType.PAYS) {
            refundOrderManager.preRefundAccount(daysList, applyFrom, orderItem, refundOrder.getId(), detailList, refundDate, order);
        }

        // 判断是否需要退订审核
        if (refundOrder.getAudit_ticket() == ProductConstants.RefundAudit.NO) {
            refundOrderManager.auditSuccess(orderItem, refundOrder, order);
        }
        // 同步数据
        Map syncData = refundOrderManager.syncRefundOrderApplyData(refundOrder.getId());
        return new Result<>(true).putExt(Result.SYNC_DATA, syncData);
    }

    @Override
    public Result<?> applyForGroup(Map params) throws Exception {
        String orderItemSn = params.get("order_item_sn").toString();
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(orderItemSn));
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        if (!(orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM)) {
            throw new ApiException("该订单不是团队订单");
        }

        // 供应侧/销售侧
        Integer applyFrom = CommonUtil.objectParseInteger(params.get("apply_from"));

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemSn, lockTimeOut);

        // 锁成功
        try {
            return appForGroup(params, orderItem, order, applyFrom);
        } finally {
            lock.unlock();
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> appForGroup(Map params, OrderItem orderItem, Order order, Integer applyFrom) throws Exception {
        if (ArrayUtils.indexOf(new int[]{
                OrderConstant.OrderItemStatus.SEND,
                OrderConstant.OrderItemStatus.NON_SEND,
                OrderConstant.OrderItemStatus.TICKET_FAIL
        }, orderItem.getStatus()) < 0 ||
                order.getStatus() != OrderConstant.OrderStatus.PAID) {
            throw new ApiException("订单不在可退订状态");
        }
        if (!params.containsKey(EpConstant.EpKey.EP_ID)) {
            throw new ApiException("非法请求:企业ID为空");
        }

        int epId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID));
        // 检查权限
        refundOrderManager.checkApplyRefund(orderItem, order, applyFrom, epId);

        Date refundDate = new Date();

        // 每日订单详情
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());

        // 退钱的票数
        int realRefundQuantity = 0;
        // 退票数量
        int refundQuantity = 0;
        // 已使用数量
        Integer usedQuantity = orderItem.getUsed_quantity();
        // 总数量
        Integer quantity = orderItem.getQuantity() * orderItem.getDays();
        // 使用方式退订
        if (usedQuantity != null && usedQuantity > 0) {
            Date expiryDate = detailList.get(detailList.size() - 1).getExpiry_date();
            if (refundDate.before(expiryDate)) {
                throw new ApiException("已使用后的必须过期才能退订");
            }
            refundQuantity = quantity - usedQuantity;
            if (orderItem.getLow_quantity() != null) {
                realRefundQuantity = usedQuantity < orderItem.getLow_quantity() ?
                        quantity - orderItem.getLow_quantity() : refundQuantity;
            }
        } else {
            // 未使用方式退订(全退)
            realRefundQuantity = quantity;
            refundQuantity = realRefundQuantity;
        }

        String cause = CommonUtil.objectParseString(params.get("cause"));

        // 计算退款金额
        int money = 0;
        // 手续费
        int fee = 0;
        // 到付不计算
        if (orderItem.getPayment_flag() != ProductConstants.PayType.PAYS) {
            int[] calcResult = refundOrderManager.calcRefundMoneyForGroup(applyFrom, orderItem.getId(), realRefundQuantity, order.getBuy_ep_id(),
                    refundOrderManager.getCoreEpId(refundOrderManager.getCoreEpId(order.getBuy_ep_id())), refundDate, detailList);
            money = calcResult[0];
            fee = calcResult[1];
        }
        if (money < 0) {
            throw new ApiException("销售价小于退货手续费");
        }

        List<Map<String, Object>> daysList = new ArrayList<>();
        for (OrderItemDetail detail : detailList) {
            Map<String, Object> dayMap = new HashMap<>();
            dayMap.put("day", detail.getDay());
            dayMap.put("quantity", detail.getQuantity() - detail.getUsed_quantity());
            dayMap.put("visitors", Collections.emptyList());
            dayMap.put("fee", fee);
            daysList.add(dayMap);
        }

        // 获取退订审核
        int[] auditSupplierConfig = getAuditConfig(orderItem.getPro_sub_id(), orderItem.getSupplier_core_ep_id());
        int auditTicket = auditSupplierConfig[0];
        // 获取退款审核
        int auditMoney = 0;
        if (orderItem.getSupplier_core_ep_id() == order.getPayee_ep_id().intValue()) {
            auditMoney = auditSupplierConfig[1];
        } else {
            auditMoney =  getAuditConfig(orderItem.getPro_sub_id(), order.getPayee_ep_id())[1];
        }
        // 创建退订订单
        RefundOrder refundOrder = refundOrderManager.generateRefundOrder(orderItem, daysList, refundQuantity, money, fee, cause, orderItem.getGroup_id(), auditTicket, auditMoney, order.getPayee_ep_id());

        // 修改明细退票数量
        orderItemDetailMapper.refundRemain(orderItem.getId());

        // 退订分账
        refundOrderManager.preRefundAccountForGroup(applyFrom, orderItem, refundOrder.getId(), detailList, refundDate, order);

        // 判断是否需要退订审核
        if (refundOrder.getAudit_ticket() == ProductConstants.RefundAudit.NO) {
            refundOrderManager.auditSuccess(orderItem, refundOrder, order);
        }
        // 同步数据
        Map syncData = refundOrderManager.syncRefundOrderApplyData(refundOrder.getId());
        return new Result<>(true).putExt(Result.SYNC_DATA, syncData);
    }

    @Override
    public Result<?> cancelNoSplit(Map params) {
        String orderSn = params.get("order_sn").toString();
        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderSn, lockTimeOut);

        // 锁成功
        try {
            return cancelNoSplit(orderSn);
        } finally {
            lock.unlock();
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> cancelNoSplit(String orderSn) {
        Order order = orderMapper.selectBySN(Long.valueOf(orderSn));
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (order.getStatus() != OrderConstant.OrderStatus.PAID_HANDLING) {
            throw new ApiException("订单不在已支付处理中状态");
        }
        // 退款,取消在退款回调中执行
        refundOrderManager.refundMoney(order, order.getPay_amount(), null);
        return new Result<>(true);
    }

    @Override
    public Result<?> audit(Map params) throws Exception {
        // 分布式锁
        String refundSn = params.get("refund_sn").toString();
        DistributedReentrantLock lock = distributedLockTemplate.execute(refundSn, lockTimeOut);

        // 锁成功
        try {
            return audit(params, refundSn);
        } finally {
            lock.unlock();
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> audit(Map params, String refundSn) throws Exception {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(refundSn));
        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        if (refundOrder.getStatus() != OrderConstant.RefundOrderStatus.AUDIT_WAIT) {
            throw new ApiException("退订订单不在待审核状态");
        }
        refundOrder.setAudit_time(new Date());
        refundOrder.setAudit_user_id(CommonUtil.objectParseInteger(params.get("operator_id")));
        refundOrder.setAudit_user_name(CommonUtil.objectParseString(params.get("operator_name")));
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.CORE_EP_ID)).equals(String.valueOf(orderItem.getSupplier_core_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能审核该退订订单");
        }
        boolean status = Boolean.parseBoolean(params.get("status").toString());
        // 通过
        if (status) {
            return refundOrderManager.auditSuccess(orderItem, refundOrder, order);
        }
        refundOrderManager.refundFail(refundOrder);
        // 发送短信
        smsManager.sendAuditRefuseSms(orderItem);

        // 同步数据
        Map syncData = refundOrderManager.syncRefundOrderAuditRefuse(refundOrder.getId());
        return new Result<>(true).putExt(Result.SYNC_DATA, syncData);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> refundAliPayMoney(Map params) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(params.get("refund_sn").toString()));
        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        if (refundOrder.getStatus() != OrderConstant.RefundOrderStatus.REFUND_MONEY) {
            throw new ApiException("退订订单不在退款中状态");
        }
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
        if (orderItem == null) {
            throw new ApiException("子订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.CORE_EP_ID)).equals(String.valueOf(order.getPayee_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能退款该退订订单");
        }
        // 退款
        Result result = refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()));
        if (result == null) {
            throw new ApiException("调用退款返回null");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> refundMoneyAudit(Map params) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(params.get("refund_sn").toString()));
        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        if (refundOrder.getStatus() != OrderConstant.RefundOrderStatus.REFUND_MONEY_AUDITING) {
            throw new ApiException("退订订单不在退款待审核状态");
        }
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
        if (orderItem == null) {
            throw new ApiException("子订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.CORE_EP_ID)).equals(String.valueOf(order.getPayee_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能审核退款该退订订单");
        }
        refundOrder.setAudit_money_time(new Date());
        refundOrder.setAudit_money_user_id(CommonUtil.objectParseInteger(params.get("operator_id")));
        refundOrder.setAudit_money_user_name(CommonUtil.objectParseString(params.get("operator_name")));
        // 退款
        Result result = refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), refundOrder);
        if (result == null) {
            throw new ApiException("调用退款返回null");
        }
        Map syncData = refundOrderManager.syncRefundOrderMoney(refundOrder.getId());
        return result.putExt(Result.SYNC_DATA, syncData);
    }

    private int[] getAuditConfig(int productSubId, int coreEpId) {
        int auditTicket = ProductConstants.RefundAudit.YES;
        int auditMoney = ProductConstants.RefundMoneyAudit.YES;
        Result<Map> platFormConfig = productSalesPlanRPCService.getPlatformConfig(productSubId, coreEpId);
        if (platFormConfig != null && platFormConfig.get() != null && platFormConfig.isSuccess()) {
            Map map = platFormConfig.get();
            Integer tmpTicket = CommonUtil.objectParseInteger(map.get("audit_ticket"));
            Integer tmpMoney = CommonUtil.objectParseInteger(map.get("audit_money"));
            auditTicket = tmpTicket == null ? auditTicket : tmpTicket;
            auditMoney = tmpMoney == null ? auditMoney : tmpMoney;
        }
        return new int[]{auditTicket, auditMoney};
    }
}
