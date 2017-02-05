package com.all580.order.service.event;

import com.all580.order.api.model.RefundAuditEventParam;
import com.all580.order.api.service.event.RefundAuditEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.manager.SmsManager;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/4 16:29
 */
@Service
public class RefundAuditEventImpl implements RefundAuditEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private SmsManager smsManager;

    @Override
    public Result process(String msgId, RefundAuditEventParam content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content.getRefundId());
        Assert.notNull(refundOrder, "退订订单不存在");

        if (content.isStatus()) {
            // 通过
            OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
            Assert.notNull(orderItem, "子订单不存在");
            Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
            Assert.notNull(order, "订单不存在");
            refundOrderManager.auditSuccess(orderItem, refundOrder, order);
            return new Result(true);
        }

        // 拒绝
        refundOrderManager.refundFail(refundOrder);
        // 发送短信
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
        Assert.notNull(orderItem, "子订单不存在");
        smsManager.sendAuditRefuseSms(orderItem);
        return new Result(true);
    }
}
