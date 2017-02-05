package com.all580.order.service.event;

import com.all580.order.api.model.OrderAuditEventParam;
import com.all580.order.api.service.event.OrderAuditSyncDataEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.framework.common.Result;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.synchronize.SynchronizeDataMap;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/24 14:56
 */
@Service
@Slf4j
public class OrderAuditEventSyncDataImpl implements OrderAuditSyncDataEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private TopicPushManager topicPushManager;
    @Value("${mns.topic}")
    private String topicName;

    @Override
    public Result process(String msgId, OrderAuditEventParam content, Date createDate) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(content.getItemId());
        if (item == null) {
            throw new ApiException("子订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        String accessKey = bookingOrderManager.getAccessKey(order.getPayee_ep_id());
        SynchronizeDataMap dataMap = new SynchronizeDataMap(accessKey)
                .add("t_order", CommonUtil.oneToList(order))
                .add("t_order_item", orderItemMapper.selectByOrderId(order.getId()));

        try {
            dataMap.sendToMns(topicPushManager, topicName).clear();
        } catch (Throwable e) {
            throw new ApiException("同步数据异常", e);
        }
        return new Result(true);
    }
}
