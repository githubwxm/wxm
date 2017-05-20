package com.all580.order.service.event;

import com.all580.order.api.model.RefundAuditEventParam;
import com.all580.order.api.service.event.RefundAuditSyncDataEvent;
import com.all580.order.dao.*;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.Result;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.synchronize.SyncAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class RefundAuditSyncDataEventImpl extends BasicSyncDataEvent implements RefundAuditSyncDataEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;

    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private TopicPushManager topicPushManager;
    @Value("${mns.topic}")
    private String topicName;

    @Override
    public Result process(String msgId, RefundAuditEventParam content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content.getRefundId());
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");

        SyncAccess syncAccess = getAccessKeys(order);
        if (content.isStatus()) {
            syncAccess.getDataMap()
                    .add("t_refund_order", refundOrder)
                    .add("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundOrder.getId()))
                    .add("t_visitor", visitorMapper.selectByOrderItem(refundOrder.getOrder_item_id()))
                    .add("t_order_item", orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id()));
        } else {
            // 拒绝
            syncAccess.getDataMap()
                    .add("t_refund_order", refundOrder)
                    .add("t_order_item_detail", orderItemDetailMapper.selectByItemId(refundOrder.getOrder_item_id()));
        }

        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
