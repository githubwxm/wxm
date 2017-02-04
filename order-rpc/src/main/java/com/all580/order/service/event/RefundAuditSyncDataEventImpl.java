package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundAuditEventParam;
import com.all580.order.api.service.event.RefundAuditEvent;
import com.all580.order.api.service.event.RefundAuditSyncDataEvent;
import com.all580.order.dao.*;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.Result;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.synchronize.SynchronizeDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/4 16:29
 */
@Service
public class RefundAuditSyncDataEventImpl implements RefundAuditSyncDataEvent {
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

        if (content.isStatus()) {
            String accessKey = refundOrderManager.getAccessKey(order.getPayee_ep_id());
            SynchronizeDataMap dataMap = new SynchronizeDataMap(accessKey)
                    .add("t_refund_order", refundOrder)
                    .add("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundOrder.getId()))
                    .add("t_visitor", visitorMapper.selectByOrderItem(refundOrder.getOrder_item_id()))
                    .add("t_order_item", orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id()));

            try {
                dataMap.sendToMns(topicPushManager, topicName).clear();
            } catch (Throwable e) {
                throw new ApiException("同步数据异常", e);
            }
            return new Result(true);
        }

        // 拒绝
        String accessKey = refundOrderManager.getAccessKey(order.getPayee_ep_id());
        SynchronizeDataMap dataMap = new SynchronizeDataMap(accessKey)
                .add("t_refund_order", refundOrder)
                .add("t_order_item_detail", orderItemDetailMapper.selectByItemId(refundOrder.getOrder_item_id()));

        try {
            dataMap.sendToMns(topicPushManager, topicName).clear();
        } catch (Throwable e) {
            throw new ApiException("同步数据异常", e);
        }
        return new Result(true);
    }

    @Override
    public String key() {
        return OrderConstant.EventType.ORDER_REFUND_AUDIT;
    }

    public int order() {
        return 2;
    }
}
