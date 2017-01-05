package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.PaymentCallbackService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.BookingOrderManager;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付回调服务
 * @date 2016/10/11 20:01
 */
@Service
@Slf4j
public class PaymentCallbackServiceImpl implements PaymentCallbackService {
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result payCallback(long ordCode, String serialNum, String outTransId) {
        log.debug("支付回调:订单号:{};流水:{};交易号:{}", new Object[]{ordCode, serialNum, outTransId});

        Order order = orderMapper.selectBySN(ordCode);
        if (order == null) {
            log.warn("订单:{}不存在", ordCode);
            return new Result(false, "订单不存在");
        }

        // 支付成功回调 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderId", order.getId().toString());
        jobParams.put("outTransId", outTransId);
        jobParams.put("serialNum", serialNum);
        bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, jobParams);
        return new Result(true);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result refundCallback(Long ordCode, String serialNum, String outTransId, boolean success) {
        log.debug("退款回调-{}:订单号:{};流水:{};交易号:{}", new Object[]{success, ordCode, serialNum, outTransId});
        Order order = orderMapper.selectBySN(ordCode);
        if (order == null && outTransId != null) {
            order = orderMapper.selectByThirdSn(outTransId);
        }
        if (order == null && outTransId == null) {
            order = orderMapper.selectByRefundSn(ordCode);
        }

        if (order == null) {
            log.warn("退款回调:订单不存在");
            return new Result(false, "订单号不存在");
        }

        // 退款成功回调 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderId", order.getId().toString());
        jobParams.put("ordCode", String.valueOf(ordCode));
        jobParams.put("serialNum", serialNum);
        jobParams.put("success", Boolean.toString(success));
        bookingOrderManager.addJob(OrderConstant.Actions.REFUND_MONEY_CALLBACK, jobParams);

        return new Result(true);
    }
}
