package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundTicketEventParam;
import com.all580.order.api.service.event.RefundTicketEvent;
import com.all580.order.api.service.event.RefundTicketSyncDataEvent;
import com.all580.order.dao.*;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.manager.SmsManager;
import com.framework.common.Result;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.synchronize.SynchronizeDataMap;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/5 9:54
 */
@Service
public class RefundTicketSyncDataEventImpl implements RefundTicketSyncDataEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RefundSerialMapper refundSerialMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;

    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private TopicPushManager topicPushManager;
    @Value("${mns.topic}")
    private String topicName;

    @Override
    public Result process(String msgId, RefundTicketEventParam content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content.getRefundId());
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");

        String accessKey = refundOrderManager.getAccessKey(order.getPayee_ep_id());
        SynchronizeDataMap dataMap = new SynchronizeDataMap(accessKey);
        if (content.isStatus()) {
            dataMap.add("t_refund_order", refundOrder)
                    .add("t_order_item", orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id()))
                    .add("t_refund_serial", refundSerialMapper.selectByRefundOrder(refundOrder.getId()))
                    .add("t_visitor", visitorMapper.selectByOrderItem(refundOrder.getOrder_item_id()))
                    .add("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundOrder.getId()));

        } else {
            dataMap.add("t_refund_order", refundOrder)
                    .add("t_order_item_detail", orderItemDetailMapper.selectByItemId(refundOrder.getOrder_item_id()));
        }
        try {
            dataMap.sendToMns(topicPushManager, topicName).clear();
        } catch (Throwable e) {
            throw new ApiException("同步数据异常", e);
        }
        return new Result(true);
    }

    @Override
    public String key() {
        return OrderConstant.EventType.REFUND_TICKET;
    }
}
