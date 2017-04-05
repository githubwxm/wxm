package com.all580.order.task.timer;

import com.all580.order.api.service.PaymentCallbackService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.ThirdPayService;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单第三方支付状态定时器
 * @date 2016/10/13 16:26
 */
@Component
@Slf4j
public class ThirdPayStatusOrderTimer {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Autowired
    private ThirdPayService thirdPayService;

    @Autowired
    private PaymentCallbackService paymentCallbackService;

    @Autowired
    private RefundOrderManager refundOrderManager;

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Value("${order.pay.timeout}")
    private Integer payTimeOut;

    private AtomicBoolean payingRun = new AtomicBoolean(false);
    private AtomicBoolean refundRun = new AtomicBoolean(false);

    @Scheduled(fixedDelay = 60000 * 5)
    public void payingJob() {
        try {
            if (payingRun.compareAndSet(false, true)) {
                List<Order> orderList = orderMapper.selectPayingOrder(payTimeOut);
                if (orderList != null && !orderList.isEmpty()) {
                    boolean retry = true;
                    for (Order order : orderList) {
                        DistributedReentrantLock lock = distributedLockTemplate.execute(String.valueOf(order.getNumber()), 10);
                        try {
                            Result<Map<String, Object>> result = thirdPayService.getPaidStatus(order.getNumber(), order.getPayee_ep_id(), order.getPayment_type(), order.getThird_serial_no());
                            Map<String, Object> map = result.get();
                            PaymentConstant.ThirdPayStatus payStatus = (PaymentConstant.ThirdPayStatus) map.get("code");
                            switch (payStatus) {
                                case SUCCESS:
                                    paymentCallbackService.payCallback(order.getNumber(), String.valueOf(order.getNumber()), CommonUtil.objectParseString(map.get("transaction_id")));
                                    break;
                                case NOTPAY:
                                case PAYERROR:
                                case NOT_EXIST:
                                case REVOKED:
                                    refundOrderManager.rollback(order, payStatus);
                                    break;
                            }
                        } catch (Exception e) {
                            retry = false;
                            log.error("订单:" + order.getNumber() + "支付中检查异常", e);
                        } finally {
                            lock.unlock();
                        }
                    }
                    if (retry) {
                        payingJob();
                    }
                }
            }
        } catch (Exception e) {
            log.error("支付中订单检查异常", e);
        } finally {
            payingRun.set(false);
        }
    }

    @Scheduled(fixedDelay = 60000 * 5)
    public void refundMoneyJob() {
        try {
            if (refundRun.compareAndSet(false, true)) {
                List<RefundOrder> refundOrders = refundOrderMapper.selectRefundMoneyOrder();
                if (refundOrders != null && !refundOrders.isEmpty()) {
                    boolean retry = true;
                    for (RefundOrder refundOrder : refundOrders) {
                        DistributedReentrantLock lock = distributedLockTemplate.execute(String.valueOf(refundOrder.getNumber()), 10);
                        try {
                            Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
                            Result<PaymentConstant.ThirdPayStatus> result = thirdPayService.refundQuery(String.valueOf(refundOrder.getNumber()), order.getPayee_ep_id(), order.getPayment_type());
                            PaymentConstant.ThirdPayStatus refundStatus = result.get();
                            switch (refundStatus) {
                                case REFUND_SUCCES:
                                    paymentCallbackService.refundCallback(order.getNumber(), String.valueOf(refundOrder.getNumber()), order.getThird_serial_no(), true);
                                    break;
                                case REFUND_FAIL:
                                case REFUND_NOTSURE:
                                    paymentCallbackService.refundCallback(order.getNumber(), String.valueOf(refundOrder.getNumber()), order.getThird_serial_no(), false);
                                    break;
                            }
                        } catch (Exception e) {
                            retry = false;
                            log.error("退订订单:" + refundOrder.getNumber() + "退款中检查异常", e);
                        } finally {
                            lock.unlock();
                        }
                    }
                    if (retry) {
                        refundMoneyJob();
                    }
                }
            }
        } catch (Exception e) {
            log.error("退款中订单检查异常", e);
        } finally {
            refundRun.set(false);
        }
    }
}
