package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.PaidEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.BookingOrderManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;
import com.framework.common.outside.JobAspect;
import com.framework.common.outside.JobTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/24 15:41
 */
@Service
@Slf4j
public class PaidEventImpl implements PaidEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private JobAspect jobManager;
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Override
    @JobTask
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        log.info(OrderConstant.LogOperateCode.NAME, bookingOrderManager.orderLog(order.getId(), null,
                0, "ORDER_EVENT", OrderConstant.LogOperateCode.PAID_SUCCESS,
                0, "订单支付成功", order.getLocal_payment_serial_no()));

        // 添加分账任务 余额不做后续分账(和支付的时候一起)
        if (order.getPayment_type() != null && order.getPayment_type() != PaymentConstant.PaymentType.BALANCE.intValue()) {
            Map<String, String> jobParam = new HashMap<>();
            jobParam.put("orderId", String.valueOf(order.getId()));
            jobManager.addJob(OrderConstant.Actions.PAYMENT_SPLIT_ACCOUNT, Collections.singleton(jobParam));
        }
        return new Result(true);
    }
}
