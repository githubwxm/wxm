package com.all580.order.service;

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
import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> cancel(Map params) {
        return refundOrderManager.cancel(Long.valueOf(params.get("order_sn").toString()));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> apply(Map params) throws Exception {
        String orderItemSn = params.get("order_item_sn").toString();
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(orderItemSn));
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemSn, lockTimeOut);

        // 锁成功
        try {
            if (Arrays.binarySearch(new int[]{
                    OrderConstant.OrderItemStatus.SEND,
                    OrderConstant.OrderItemStatus.NON_SEND,
                    OrderConstant.OrderItemStatus.TICKET_FAIL
            }, orderItem.getStatus()) < 0 ||
                    order.getStatus() != OrderConstant.OrderStatus.PAID) {
                throw new ApiException("订单不在可退订状态");
            }

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
            // 判断余票 并修改明细退票数量
            int tmpQuantity = refundOrderManager.canRefundForDays(daysList, detailList);
            if (tmpQuantity != quantity) {
                throw new ApiException("退票总数与每天退票数不符");
            }

            Date refundDate = new Date();
            // 计算退款金额
            int money = refundOrderManager.calcRefundMoney(daysList, detailList, orderItem.getId(), order.getBuyEpId(),
                    refundOrderManager.getCoreEpId(refundOrderManager.getCoreEpId(order.getBuyEpId())), refundDate);
            if (money < 0) {
                throw new ApiException("销售价小于退货手续费");
            }
            // 创建退订订单
            RefundOrder refundOrder = refundOrderManager.generateRefundOrder(orderItem.getId(), daysList, quantity, money, cause);

            // 退订分账
            refundOrderManager.preRefundAccount(daysList, orderItem.getId(), refundOrder.getId(), detailList, refundDate);

            // 同步数据
            refundOrderManager.syncRefundOrderApplyData(refundOrder.getId());
            return new Result<>(true);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> cancelNoSplit(Map params) {
        String orderSn = params.get("order_sn").toString();
        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderSn, lockTimeOut);

        // 锁成功
        try {
            Order order = orderMapper.selectBySN(Long.valueOf(orderSn));
            if (order == null) {
                throw new ApiException("订单不存在");
            }
            if (order.getStatus() != OrderConstant.OrderStatus.PAID_HANDLING) {
                throw new ApiException("订单不在已支付处理中状态");
            }
            // 退款,取消在退款回调中执行
            refundOrderManager.refundMoney(order, order.getPayAmount(), null);
            return new Result<>(true);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> audit(Map params) throws Exception {
        // 分布式锁
        String refundSn = params.get("refund_sn").toString();
        DistributedReentrantLock lock = distributedLockTemplate.execute(refundSn, lockTimeOut);

        // 锁成功
        try {
            RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(refundSn));
            if (refundOrder == null) {
                throw new ApiException("退订订单不存在");
            }
            if (refundOrder.getStatus() != OrderConstant.RefundOrderStatus.AUDIT_WAIT) {
                throw new ApiException("退订订单不在待审核状态");
            }
            refundOrder.setAuditTime(new Date());
            refundOrder.setAuditUserId(CommonUtil.objectParseInteger(params.get("operator_id")));
            refundOrder.setAuditUserName(CommonUtil.objectParseString(params.get("operator_name")));
            OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrderItemId());
            if (orderItem == null) {
                throw new ApiException("订单不存在");
            }
            Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
            if (order == null) {
                throw new ApiException("订单不存在");
            }
            boolean status = Boolean.parseBoolean(params.get("status").toString());
            // 通过
            if (status) {
                if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND) {
                    // 调用退票
                    refundOrderManager.refundTicket(refundOrder);
                    refundOrderMapper.updateByPrimaryKeySelective(refundOrder);
                } else {
                    // 没有出票直接退款
                    refundOrder.setStatus(OrderConstant.RefundOrderStatus.REFUND_MONEY); // 退款中
                    refundOrderMapper.updateByPrimaryKeySelective(refundOrder);
                    List daysList = JsonUtils.json2List(refundOrder.getData());
                    // 每日订单详情
                    List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
                    int quantity = refundOrderManager.nonSendTicketRefund(daysList, detailList);
                    orderItem.setRefundQuantity(orderItem.getRefundQuantity() + quantity);
                    orderItemMapper.updateByPrimaryKeySelective(orderItem);
                    // 支付宝需要手动退款
                    if (order.getPaymentType() != PaymentConstant.PaymentType.ALI_PAY.intValue()) {
                        // 退款
                        refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()));
                    }
                }

                // 同步数据
                refundOrderManager.syncRefundOrderAuditAcceptData(refundOrder.getId());
                return new Result<>(true);
            }
            refundOrderManager.refundFail(refundOrder);
            // 同步数据
            refundOrderManager.syncRefundOrderAuditRefuse(refundOrder.getId());
            return new Result<>(true);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> refundAliPayMoney(Map params) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(params.get("refund_sn").toString()));
        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        if (refundOrder.getStatus() != OrderConstant.RefundOrderStatus.AUDIT_WAIT) {
            throw new ApiException("退订订单不在待审核状态");
        }
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrderItemId());
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        // 退款
        refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()));
        return new Result<>(true);
    }
}
