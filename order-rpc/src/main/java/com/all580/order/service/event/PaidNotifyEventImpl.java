package com.all580.order.service.event;

import com.all580.order.api.service.event.PaidNotifyEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * Created by wxming on 2017/8/9 0009.
 */
@Slf4j
@Service
public class PaidNotifyEventImpl extends  BaseNotifyEvent implements PaidNotifyEvent {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        Assert.notNull(order, "订单不存在");
        Integer payment_type=order.getPayment_type();
        //微信支付宝  发送支付成功短信
        if(payment_type != null && payment_type-PaymentConstant.PaymentType.ALI_PAY==0||payment_type-PaymentConstant.PaymentType.WX_PAY==0){
            List<OrderItem> list = orderItemMapper.selectByOrderId(order.getId());
            Assert.notNull(list, "子订单不存在");
            notifyEvent(list.get(0).getId(), "PAID",null);
        }
        return new Result(true);
    }
}
