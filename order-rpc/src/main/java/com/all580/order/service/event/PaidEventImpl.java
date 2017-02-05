package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.PaidEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.SmsManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/24 15:41
 */
@Service
public class PaidEventImpl implements PaidEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private SmsManager smsManager;
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        if (orderItems.size() == 1) {
            // 发送短信
            // TODO: 2016/11/16  目前只支持单子订单发送
            smsManager.sendPaymentSuccess(orderItems.get(0));
        }

        // 添加分账任务 余额不做后续分账(和支付的时候一起)
        if (order.getPayment_type() != null && order.getPayment_type() != PaymentConstant.PaymentType.BALANCE.intValue()) {
            Map<String, String> jobParam = new HashMap<>();
            jobParam.put("orderId", String.valueOf(order.getId()));
            bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_SPLIT_ACCOUNT, jobParam);
        }
        return new Result(true);
    }
}
