package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.RefundApplyNotifyEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.RefundOrder;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Created by wxming on 2017/2/16 0016.
 */
public class RefundApplyNotifyEventImpl extends BaseNotifyEvent implements RefundApplyNotifyEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result process(String s, Integer integer, Date date) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(integer);
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");
        if(order.getStatus()- OrderConstant.OrderStatus.AUDIT_WAIT==0){
            notifyEvent(refundOrder.getOrder_item_id(), "ORTHER");
        }
        return new Result(true);
    }
}
