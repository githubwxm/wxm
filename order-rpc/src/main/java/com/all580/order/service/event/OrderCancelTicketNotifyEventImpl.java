package com.all580.order.service.event;

import com.all580.order.api.service.event.CancelTicketNotifyEvent;
import com.all580.order.api.service.event.SendTicketNotifyEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@Service
public class OrderCancelTicketNotifyEventImpl extends BaseNotifyEvent implements CancelTicketNotifyEvent {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Result process(String s, Integer integer, Date date) {
        Order order = orderMapper.selectByPrimaryKey(integer);
        Assert.notNull(order, "订单不存在");
        List<OrderItem> list = orderItemMapper.selectByOrderId(order.getId());
        Assert.notNull(list, "子订单不存在");
        notifyEvent(list.get(0).getId(), "CANCEL");
        return new Result(true);
    }

}
