package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.PaymentCallbackService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.BookingOrderManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付回调服务
 * @date 2016/10/11 20:01
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class PaymentCallbackServiceImpl implements PaymentCallbackService {
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Override
    public Result payCallback(long ordCode, String serialNum) {
        Order order = orderMapper.selectBySN(ordCode);
        if (order == null) {
            return new Result(false, "订单不存在");
        }
        order.setStatus(OrderConstant.OrderStatus.PAID_HANDLING); // 已支付,处理中
        orderMapper.updateByPrimaryKey(order);

        // 支付成功后加平台商余额(平帐)
        BalanceChangeInfo info = new BalanceChangeInfo();
        info.setEpId(bookingOrderManager.getCoreEpId(bookingOrderManager.getCoreEpId(order.getBuyEpId())));
        info.setCoreEpId(info.getEpId());
        info.setBalance(order.getPayAmount());
        bookingOrderManager.changeBalances(PaymentConstant.BalanceChangeType.THIRD_PAY_FOR_ORDER, serialNum, info);

        // 支付成功回调 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderId", order.getId().toString());
        bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, jobParams);
        return new Result(true);
    }

    @Override
    public Result refundCallback(long ordCode, String serialNum, boolean success) {
        Order order = orderMapper.selectBySN(ordCode);
        if (order == null) {
            return new Result(false, "订单不存在");
        }
        if (order.getStatus() != OrderConstant.OrderStatus.PAID_HANDLING) {
            RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(serialNum));
            if (refundOrder == null) {
                return new Result(false, "退订订单不存在");
            }
            refundOrder.setRefundMoneyTime(new Date());
            refundOrder.setStatus(success ? OrderConstant.RefundOrderStatus.REFUND_SUCCESS : OrderConstant.RefundOrderStatus.REFUND_MONEY_FAIL);
            refundOrderMapper.updateByPrimaryKey(refundOrder);
        }

        if (!success) {
            log.info("退款失败 加入任务处理...");
            // 退款失败回调 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("ordCode", String.valueOf(ordCode));
            jobParams.put("serialNum", serialNum);
            bookingOrderManager.addJob(OrderConstant.Actions.REFUND_MONEY, jobParams);
            return new Result(true);
        }
        // 已支付,处理中 分账失败 直接取消
        if (order.getStatus() == OrderConstant.OrderStatus.PAID_HANDLING) {
            // 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", order.getId().toString());
            bookingOrderManager.addJob(OrderConstant.Actions.CANCEL_CALLBACK, jobParams);
            return new Result(true);
        }
        return new Result(true);
    }
}
