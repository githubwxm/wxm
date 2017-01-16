package com.all580.order.manager;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundGroupTicketInfo;
import com.all580.order.api.model.RefundTicketInfo;
import com.all580.order.dao.*;
import com.all580.order.dto.AccountDataDto;
import com.all580.order.dto.RefundDay;
import com.all580.order.entity.*;
import com.all580.order.util.AccountUtil;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import com.github.ltsopensource.core.domain.Action;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 锁事务分离的处理类
 * @date 2017/1/3 16:59
 */
@Component
@Slf4j
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class LockTransactionManager {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private RefundSerialMapper refundSerialMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private SmsManager smsManager;
    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private EpService epService;
    @Autowired
    private ThirdPayService thirdPayService;

    /**
     * 支付回调
     * @param orderId
     * @param outTransId
     * @param serialNum
     */
    public void paymentCallback(int orderId, String outTransId, String serialNum) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("支付成功任务,订单不存在");
            throw new ApiException("订单不存在");
        }

        if (order.getStatus() != OrderConstant.OrderStatus.PAYING) {
            log.warn("支付成功任务,订单:{}状态:{}不是支付中", order.getNumber(), order.getStatus());
            throw new ApiException("订单不在支付中状态");
        }
        order.setThird_serial_no(outTransId);
        order.setStatus(OrderConstant.OrderStatus.PAID_HANDLING); // 已支付,处理中
        // 到付 和 余额支付直接支付成功 其它的在分账的时候设置
        if (order.getPayment_type() == null && order.getPay_amount() == 0 ||
                (order.getPayment_type() != null && order.getPayment_type() == PaymentConstant.PaymentType.BALANCE.intValue())) {
            order.setStatus(OrderConstant.OrderStatus.PAID); // 已支付
        }
        orderMapper.updateByPrimaryKeySelective(order);

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        List<ProductSearchParams> lockParams = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            lockParams.add(bookingOrderManager.parseParams(orderItem));
        }

        // 加已售
        com.framework.common.Result result = productSalesPlanRPCService.addSoldProductStocks(lockParams);
        if (!result.isSuccess()) {
            log.warn("支付成功加已售失败");
            throw new ApiException(result.getError());
        }

        // 子订单状态为未出票
        orderItemMapper.setStatusByOrderId(orderId, OrderConstant.OrderItemStatus.NON_SEND);

        // 同步数据
        bookingOrderManager.syncPaymentSuccessData(orderId);

        // 发送短信
        // TODO: 2016/11/16  目前只支持单子订单发送
        smsManager.sendPaymentSuccess(orderItems.get(0));

        // 添加分账任务 余额不做后续分账(和支付的时候一起)
        if (order.getPayment_type() != null && order.getPayment_type() != PaymentConstant.PaymentType.BALANCE.intValue()) {
            Map<String, String> jobParam = new HashMap<>();
            jobParam.put("orderId", String.valueOf(orderId));
            bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_SPLIT_ACCOUNT, jobParam);
        }
        if (order.getStatus() == OrderConstant.OrderStatus.PAID) {
            // 出票
            // 记录任务
            sendTicket(orderItems);
        }
    }

    /**
     * 退款回调
     * @param orderId
     * @param ordCode
     * @param serialNum
     * @param success
     * @throws Exception
     */
    public void refundMoneyCallback(int orderId, Long ordCode, String serialNum, boolean success) throws Exception {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("退款成功回调,订单不存在");
            throw new Exception("订单不存在");
        }

        if (order.getStatus() == OrderConstant.OrderStatus.PAID_HANDLING) {
            if (!success) {
                addRefundMoneyJob(ordCode, serialNum);
                return;
            }
            // 已支付,处理中(分账失败)退订 直接取消
            // 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", order.getId().toString());
            bookingOrderManager.addJob(OrderConstant.Actions.CANCEL_CALLBACK, jobParams);
            return;
        }

        refundOrderManager.refundMoneyAfter(Long.valueOf(serialNum), success);
        if (!success) {
            addRefundMoneyJob(ordCode, serialNum);
        }
    }

    /**
     * 支付分账
     * @param orderId
     * @return
     */
    public com.github.ltsopensource.tasktracker.Result paymentSplitAccount(int orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("支付分账,订单不存在");
            throw new ApiException("订单不存在");
        }

        if (order.getStatus() != OrderConstant.OrderStatus.PAID_HANDLING) {
            log.warn("支付分账,订单:{}状态:{}不是分账中", order.getNumber(), order.getStatus());
            throw new ApiException("订单不在分账中状态");
        }

        // 更新状态已支付
        order.setStatus(OrderConstant.OrderStatus.PAID);
        orderMapper.updateByPrimaryKeySelective(order);
        // 分账
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        List<BalanceChangeInfo> balanceChangeInfoList = bookingOrderManager.packagingPaySplitAccount(order, orderItems);
        Result<BalanceChangeRsp> result = bookingOrderManager.changeBalances(PaymentConstant.BalanceChangeType.PAY_SPLIT, String.valueOf(order.getNumber()), balanceChangeInfoList);
        if (!result.isSuccess()) {
            log.warn("支付分账失败:{}", JsonUtils.toJson(result.get()));
            throw new ApiException(result.getError());
        }

        // 出票
        // 记录任务
        sendTicket(orderItems);

        // 同步数据
        bookingOrderManager.syncPaymentSplitAccountData(orderId);
        return new com.github.ltsopensource.tasktracker.Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 申请退订
     * @param params
     * @param orderItem
     * @param order
     * @param applyFrom
     * @return
     * @throws Exception
     */
    public Result<?> applyRefund(Map params, OrderItem orderItem, Order order, Integer applyFrom) throws Exception {
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
        Collection<RefundDay> refundDays = AccountUtil.decompileRefundDay((List) params.get("days"));
        // 总退票数量
        Integer quantity = CommonUtil.objectParseInteger(params.get("quantity"));
        String cause = CommonUtil.objectParseString(params.get("cause"));

        if (refundDays == null) {
            throw new ApiException("缺少退票详情");
        }

        Date refundDate = new Date();
        int[] calcResult = refundOrderManager.calcRefundMoneyAndFee(orderItem, order, applyFrom, refundDays, detailList, refundDate);
        // 计算退款金额
        int money = calcResult[0];
        // 手续费
        int fee = calcResult[1];

        // 获取退订审核
        int[] auditSupplierConfig = refundOrderManager.getAuditConfig(orderItem.getPro_sub_id(), orderItem.getSupplier_core_ep_id());
        int auditTicket = auditSupplierConfig[0];
        // 获取退款审核
        int auditMoney = 0;
        if (orderItem.getSupplier_core_ep_id() == order.getPayee_ep_id().intValue()) {
            auditMoney = auditSupplierConfig[1];
        } else {
            auditMoney = refundOrderManager.getAuditConfig(orderItem.getPro_sub_id(), order.getPayee_ep_id())[1];
        }
        // 创建退订订单
        RefundOrder refundOrder = refundOrderManager.generateRefundOrder(orderItem, refundDays, quantity, money, fee, cause, auditTicket, auditMoney, order.getPayee_ep_id());

        // 判断余票 并修改明细退票数量 创建游客退票信息
        int tmpQuantity = refundOrderManager.canRefundForDays(refundDays, detailList, refundOrder.getOrder_item_id(), refundOrder.getId());
        if (tmpQuantity != quantity) {
            throw new ApiException("退票总数与每天退票数不符");
        }

        // 退订分账 到付退订不分帐
        if (orderItem.getPayment_flag() != ProductConstants.PayType.PAYS) {
            refundOrderManager.preRefundSplitAccount(applyFrom, refundOrder.getId(), order, refundDate, refundOrder.getOrder_item_id(), detailList, refundDays);
        }

        // 判断是否需要退订审核
        if (refundOrder.getAudit_ticket() == ProductConstants.RefundAudit.NO) {
            refundOrderManager.auditSuccess(orderItem, refundOrder, order);
        }
        // 同步数据
        Map syncData = refundOrderManager.syncRefundOrderApplyData(refundOrder.getId());
        return new Result<>(true).putExt(Result.SYNC_DATA, syncData);
    }

    /**
     * 申请退订(团队)
     * @param params
     * @param orderItem
     * @param order
     * @param applyFrom
     * @return
     * @throws Exception
     */
    public Result<?> applyRefundForGroup(Map params, OrderItem orderItem, Order order, Integer applyFrom) throws Exception {
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

        // 使用后退订
        if (orderItem.getUsed_quantity() != null && orderItem.getUsed_quantity() > 0) {
            Date expiryDate = detailList.get(detailList.size() - 1).getExpiry_date();
            if (refundDate.before(expiryDate)) {
                throw new ApiException("已使用后的必须过期才能退订");
            }
        }

        String cause = CommonUtil.objectParseString(params.get("cause"));

        // 伪装每天退票信息
        Collection<RefundDay> refundDays = AccountUtil.parseRefundDayForDetail(detailList);
        // 计算一共退多少张
        int totalRefundQuantity = 0;
        for (RefundDay refundDay : refundDays) {
            totalRefundQuantity += refundDay.getQuantity();
        }

        int[] calcResult = refundOrderManager.calcRefundMoneyAndFee(orderItem, order, applyFrom, refundDays, detailList, refundDate);
        // 计算退款金额
        int money = calcResult[0];
        // 手续费
        int fee = calcResult[1];

        // 获取退订审核
        int[] auditSupplierConfig = refundOrderManager.getAuditConfig(orderItem.getPro_sub_id(), orderItem.getSupplier_core_ep_id());
        int auditTicket = auditSupplierConfig[0];
        // 获取退款审核
        int auditMoney = 0;
        if (orderItem.getSupplier_core_ep_id() == order.getPayee_ep_id().intValue()) {
            auditMoney = auditSupplierConfig[1];
        } else {
            auditMoney = refundOrderManager.getAuditConfig(orderItem.getPro_sub_id(), order.getPayee_ep_id())[1];
        }
        // 创建退订订单
        RefundOrder refundOrder = refundOrderManager.generateRefundOrder(orderItem, refundDays, totalRefundQuantity, money, fee, cause, orderItem.getGroup_id(), auditTicket, auditMoney, order.getPayee_ep_id());

        // 修改明细退票数量
        orderItemDetailMapper.refundRemain(orderItem.getId());

        // 退订分账
        refundOrderManager.preRefundSplitAccount(applyFrom, refundOrder.getId(), order, refundDate, refundOrder.getOrder_item_id(), detailList, refundDays);

        // 判断是否需要退订审核
        if (refundOrder.getAudit_ticket() == ProductConstants.RefundAudit.NO) {
            refundOrderManager.auditSuccess(orderItem, refundOrder, order);
        }
        // 同步数据
        Map syncData = refundOrderManager.syncRefundOrderApplyData(refundOrder.getId());
        return new Result<>(true).putExt(Result.SYNC_DATA, syncData);
    }

    /**
     * 取消已支付未分账的订单
     * @param orderSn
     * @return
     */
    public Result<?> cancelNoSplit(String orderSn) {
        Order order = orderMapper.selectBySN(Long.valueOf(orderSn));
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (order.getStatus() != OrderConstant.OrderStatus.PAID_HANDLING) {
            throw new ApiException("订单不在已支付处理中状态");
        }
        // 退款,取消在退款回调中执行
        refundOrderManager.refundMoney(order, order.getPay_amount(), null, -1);
        return new Result<>(true);
    }

    /**
     * 退订审核
     * @param params
     * @param refundSn
     * @return
     * @throws Exception
     */
    public Result<?> auditRefund(Map params, String refundSn) throws Exception {
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

    /**
     * 预定审核
     * @param params
     * @param orderItemSn
     * @return
     */
    public Result<?> auditBooking(Map params, long orderItemSn) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderItemSn);
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        if (orderItem.getStatus() != OrderConstant.OrderItemStatus.AUDIT_WAIT) {
            throw new ApiException("订单不在待审状态");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT) {
            throw new ApiException("订单不在待审状态");
        }
        if (!params.containsKey(EpConstant.EpKey.EP_ID)) {
            throw new ApiException("非法请求:企业ID为空");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.EP_ID)).equals(String.valueOf(orderItem.getSupplier_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能审核该订单");
        }

        orderItem.setAudit_user_id(CommonUtil.objectParseInteger(params.get("operator_id")));
        orderItem.setAudit_user_name(CommonUtil.objectParseString(params.get("operator_name")));
        orderItem.setAudit_time(new Date());
        boolean status = Boolean.parseBoolean(params.get("status").toString());
        // 通过
        if (status) {
            orderItem.setStatus(OrderConstant.OrderItemStatus.AUDIT_SUCCESS);
            orderItemMapper.updateByPrimaryKeySelective(orderItem);
            boolean allAudit = bookingOrderManager.isOrderAllAudit(orderItem.getOrder_id(), orderItem.getId());
            if (allAudit) {
                order.setStatus(OrderConstant.OrderStatus.PAY_WAIT);
                order.setAudit_time(new Date());
                // 判断是否需要支付
                if (order.getPay_amount() <= 0) { // 不需要支付
                    order.setStatus(OrderConstant.OrderStatus.PAID_HANDLING); // 已支付,处理中
                    // 支付成功回调 记录任务
                    Map<String, String> jobParams = new HashMap<>();
                    jobParams.put("orderId", order.getId().toString());
                    bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, jobParams);
                }
                orderMapper.updateByPrimaryKeySelective(order);
                // TODO: 2016/11/16  目前只支持单子订单发送
                smsManager.sendAuditSuccess(orderItem);
            }
            // 同步数据
            Map syncData = bookingOrderManager.syncOrderAuditAcceptData(order.getId(), orderItem.getId());
            Result<?> result = new Result<>(true);
            result.putExt(Result.SYNC_DATA, syncData);
            return result;
        }
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        // 不通过
        // 取消订单 会自动同步数据
        return refundOrderManager.cancel(order.getNumber());
    }

    /**
     * 支付
     * @param params
     * @param sn
     * @param payType
     * @return
     */
    public Result<?> payment(Map params, Long sn, Integer payType) {
        Order order = orderMapper.selectBySN(sn);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (order.getStatus() != OrderConstant.OrderStatus.PAY_WAIT &&
                order.getStatus() != OrderConstant.OrderStatus.PAY_FAIL &&
                order.getStatus() != OrderConstant.OrderStatus.PAYING) {
            throw new ApiException("订单不在待支付状态");
        }
        if (order.getPay_amount() <= 0) {
            throw new ApiException("该订单不需要支付");
        }
        if (!params.containsKey(EpConstant.EpKey.EP_ID)) {
            throw new ApiException("非法请求:企业ID为空");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.EP_ID)).equals(String.valueOf(order.getBuy_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能支付该订单");
        }

        order.setStatus(OrderConstant.OrderStatus.PAYING);
        order.setLocal_payment_serial_no(String.valueOf(UUIDGenerator.generateUUID()));
        order.setPayment_type(payType);
        order.setPay_time(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        // 调用支付RPC
        // 余额支付
        if (payType == PaymentConstant.PaymentType.BALANCE.intValue()) {
            // 余额支付需要扣除销售商的钱
            List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderAnEp(order.getId(), order.getBuy_ep_id(), order.getPayee_ep_id());
            for (OrderItemAccount account : accounts) {
                // 把data JSON 反编译为JAVA类型
                Collection<AccountDataDto> dataDtoList = AccountUtil.decompileAccountData(account.getData());
                // 获取总出货价
                int totalOutPrice = AccountUtil.getTotalOutPrice(dataDtoList);
                account.setMoney(account.getMoney() + (-totalOutPrice));
                account.setProfit(account.getMoney());
                orderItemAccountMapper.updateByPrimaryKeySelective(account);
            }

            // 获取余额变动信息
            List<BalanceChangeInfo> balanceChangeInfoList = bookingOrderManager.packagingPaySplitAccount(order);

            // 支付
            Result result = bookingOrderManager.changeBalances(
                    PaymentConstant.BalanceChangeType.BALANCE_PAY,
                    order.getNumber().toString(), balanceChangeInfoList);
            if (!result.isSuccess()) {
                log.warn("余额支付失败:{}", JsonUtils.toJson(result));
                throw new ApiException(result.getError());
            }
            // 同步数据
            Map syncData = bookingOrderManager.syncOrderPaymentData(order.getId());
            return new Result<>(true).putExt(Result.SYNC_DATA, syncData);
        }
        // 第三方支付
        // 获取商品名称
        List<String> names = orderItemMapper.getProductNamesByOrderId(order.getId());
        List<Long> ids = orderItemMapper.getProductIdsByOrderId(order.getId());
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("prodName", StringUtils.join(names, ","));
        payParams.put("totalFee", order.getPay_amount());
        payParams.put("serialNum", order.getNumber().toString());
        payParams.put("prodId", StringUtils.join(ids, ","));

        Result result = thirdPayService.reqPay(order.getNumber(),
                bookingOrderManager.getCoreEpId(epService.selectPlatformId(order.getBuy_ep_id())),
                payType, payParams);
        if (!result.isSuccess()) {
            log.warn("第三方支付异常:{}", JsonUtils.toJson(result));
            throw new ApiException(result.getError());
        }
        // 同步数据
        Map syncData = bookingOrderManager.syncOrderPaymentData(order.getId());
        return result.putExt(Result.SYNC_DATA, syncData);
    }

    /**
     * 退票
     * @param info
     * @param procTime
     * @param orderItem
     * @param refundOrder
     * @return
     */
    public Result refundTicket(RefundTicketInfo info, Date procTime, OrderItem orderItem, RefundOrder refundOrder) {
        RefundSerial refundSerial = refundSerialMapper.selectByLocalSn(Long.valueOf(info.getRefId()));
        if (refundSerial == null) {
            return new Result(false, "退票流水错误");
        }

        if (refundSerial.getRefund_time() != null) {
            return new Result(false, "退票流水:" + info.getRefId() + "重复操作");
        }

        // 退票失败
        if (!info.isSuccess()) {
            return refundFail(refundOrder, orderItem);
        }

        refundOrder.setRefund_ticket_time(procTime);

        //refundSerial.setRemoteSerialNo(refundSn);
        refundSerial.setRefund_time(procTime);

        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        visitor.setReturn_quantity(visitor.getReturn_quantity() + refundSerial.getQuantity());
        visitorMapper.updateByPrimaryKeySelective(visitor);

        RefundVisitor refundVisitor = refundVisitorMapper.selectByRefundIdAndVisitorId(refundOrder.getId(), info.getVisitorSeqId());
        refundVisitor.setReturn_quantity(refundVisitor.getReturn_quantity() + refundSerial.getQuantity());
        refundVisitor.setPre_quantity(0);
        refundVisitorMapper.updateByPrimaryKeySelective(refundVisitor);
        orderItem.setRefund_quantity(orderItem.getRefund_quantity() + refundSerial.getQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
        refundSerialMapper.updateByPrimaryKeySelective(refundSerial);

        // 退款
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), refundOrder);

        // 还库存 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderItemId", String.valueOf(refundOrder.getOrder_item_id()));
        jobParams.put("check", "false");
        bookingOrderManager.addJob(OrderConstant.Actions.REFUND_STOCK, jobParams);

        // 同步数据
        refundOrderManager.syncRefundTicketData(refundOrder.getId());
        return new Result(true);
    }

    /**
     * 退票(团队)
     * @param info
     * @param procTime
     * @param orderItem
     * @param refundOrder
     * @return
     */
    public Result refundGroupTicket(RefundGroupTicketInfo info, Date procTime, OrderItem orderItem, RefundOrder refundOrder) {
        RefundSerial refundSerial = refundSerialMapper.selectByLocalSn(Long.valueOf(info.getRefId()));
        if (refundSerial == null) {
            return new Result(false, "退票流水错误");
        }

        if (refundSerial.getRefund_time() != null) {
            return new Result(false, "退票流水:" + info.getRefId() + "重复操作");
        }
        // 退票失败
        if (!info.isSuccess()) {
            return refundFail(refundOrder, orderItem);
        }

        refundOrder.setRefund_ticket_time(procTime);

        //refundSerial.setRemoteSerialNo(refundSn);
        refundSerial.setRefund_time(procTime);

        orderItem.setRefund_quantity(orderItem.getRefund_quantity() + refundSerial.getQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
        refundSerialMapper.updateByPrimaryKeySelective(refundSerial);

        // 退款
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), refundOrder);

        // 还库存 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderItemId", String.valueOf(refundOrder.getOrder_item_id()));
        jobParams.put("check", "false");
        bookingOrderManager.addJob(OrderConstant.Actions.REFUND_STOCK, jobParams);

        // 同步数据
        refundOrderManager.syncRefundTicketData(refundOrder.getId());
        return new Result(true);
    }

    private void addRefundMoneyJob(Long ordCode, String serialNum) {
        log.info("退款失败 加入任务处理...");
        // 退款失败回调 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("ordCode", String.valueOf(ordCode));
        jobParams.put("serialNum", serialNum);
        bookingOrderManager.addJob(OrderConstant.Actions.REFUND_MONEY, jobParams, true);
    }

    private Result refundFail(RefundOrder refundOrder, OrderItem orderItem) {
        try {
            refundOrderManager.refundFail(refundOrder);
        } catch (Exception e) {
            throw new ApiException("退票失败还原状态异常", e);
        }

        // 发送短信
        smsManager.sendRefundFailSms(orderItem);

        refundOrderManager.syncRefundOrderAuditRefuse(refundOrder.getId());
        return new Result(true);
    }

    private void sendTicket(List<OrderItem> orderItems) {
        List<Map<String, String>> jobParams = new ArrayList<>();
        List<Map<String, String>> jobGroupParams = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            Map<String, String> jobParam = new HashMap<>();
            jobParam.put("orderItemId", orderItem.getId().toString());
            // 团队票
            if (orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                    orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM) {
                jobGroupParams.add(jobParam);
                continue;
            }
            jobParams.add(jobParam);
        }
        if (jobParams.size() > 0) {
            bookingOrderManager.addJobs(OrderConstant.Actions.SEND_TICKET, jobParams);
        }
        if (jobGroupParams.size() > 0) {
            bookingOrderManager.addJobs(OrderConstant.Actions.SEND_GROUP_TICKET, jobGroupParams);
        }
    }
}
