package com.all580.order.service.event;

import com.all580.order.api.model.RefundMoneyEventParam;
import com.all580.order.api.service.event.RefundMoneyNotifyEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Created by wxming on 2017/2/16 0016.
 */
public class RefundMoneyNotifyEventImpl extends BaseNotifyEvent implements RefundMoneyNotifyEvent {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Override
    public Result process(String s, RefundMoneyEventParam content, Date date) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(content.getSerialNo()));
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");
        notifyEvent(refundOrder.getOrder_item_id(), "REFUND");
        return new Result(true);
    }


}
