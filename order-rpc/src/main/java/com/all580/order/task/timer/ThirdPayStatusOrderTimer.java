package com.all580.order.task.timer;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.PaymentCallbackService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.service.event.BasicSyncDataEvent;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.ThirdPayService;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private BasicSyncDataEvent basicSyncDataEvent;

    @Scheduled(fixedDelay = 60000 * 5)
    public void payingJob() {
        try {
            List<Order> orderList = orderMapper.selectPayingOrder();
            if (orderList != null && !orderList.isEmpty()) {
                boolean retry = true;
                for (Order order : orderList) {
                    try {
                        Result<PaymentConstant.ThirdPayStatus> result = thirdPayService.getPaidStatus(order.getNumber(), order.getPayee_ep_id(), order.getPayment_type(), order.getThird_serial_no());
                        PaymentConstant.ThirdPayStatus payStatus = result.get();
                        switch (payStatus) {
                            case SUCCESS:
                                paymentCallbackService.payCallback(order.getNumber(), String.valueOf(order.getNumber()), order.getThird_serial_no());
                                break;
                            case NOTPAY:
                            case PAYERROR:
                                rollback(order, payStatus);
                                break;
                        }
                    } catch (Exception e) {
                        retry = false;
                        log.error("订单:"+ order.getNumber() +"支付中检查异常", e);
                    }
                }
                if (retry) {
                    payingJob();
                }
            }
        } catch (Exception e) {
            log.error("支付中订单检查异常", e);
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void rollback(Order order, PaymentConstant.ThirdPayStatus payStatus) {
        order.setStatus(payStatus == PaymentConstant.ThirdPayStatus.NOTPAY ? OrderConstant.OrderStatus.PAY_WAIT : OrderConstant.OrderStatus.PAY_FAIL);
        orderMapper.updateByPrimaryKeySelective(order);
        // 同步数据
        SyncAccess syncAccess = basicSyncDataEvent.getAccessKeys(order);
        syncAccess.getDataMap().add("t_order", order);
        syncAccess.loop();
        basicSyncDataEvent.sync(syncAccess.getDataMaps());
    }

    @Scheduled(fixedDelay = 60000 * 5)
    public void refundMoneyJob() {
        try {
            List<RefundOrder> refundOrders = refundOrderMapper.selectRefundMoneyOrder();
            if (refundOrders != null && !refundOrders.isEmpty()) {
                boolean retry = true;
                for (RefundOrder refundOrder : refundOrders) {
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
                        log.error("退订订单:"+ refundOrder.getNumber() +"退款中检查异常", e);
                    }
                }
                if (retry) {
                    refundMoneyJob();
                }
            }
        } catch (Exception e) {
            log.error("退款中订单检查异常", e);
        }
    }
}
