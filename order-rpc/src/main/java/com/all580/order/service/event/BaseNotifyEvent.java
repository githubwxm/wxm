package com.all580.order.service.event;

import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.mns.TopicPushManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@Component
public class BaseNotifyEvent {
    @Autowired
    private TopicPushManager topicPushManager;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Value("${mns.topic}")
    private String topicName;

    protected void notifyEvent(Integer itemId,String opCode){
        OrderItem item = orderItemMapper.selectByPrimaryKey(itemId);
        Assert.notNull(item);
        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        Assert.notNull(order);
        Map<String,Object> map = new HashMap<>();
        map.put("op_code",opCode);
        map.put("order_id",order.getId());
        map.put("order_item_id",itemId);
        map.put("ticket_status",item.getStatus());
        map.put("order_status",order.getStatus());
        map.put("ep_id",order.getBuy_ep_id());
        map.put("usd_qty",item.getUsed_quantity());
        map.put("rfd_qty",item.getRefund_quantity());
        map.put("quantity",item.getQuantity());
        map.put("exp_qty",0);
        String MaxDate= orderItemDetailMapper.selectExpiryMaxDate(itemId);
        Date date =  DateFormatUtils.converToDateTime(MaxDate);
        Date currentDate = new Date();
         if(currentDate.after(date)){
             map.put("exp_qty",item.getQuantity()-item.getRefund_quantity()-item.getUsed_quantity());
          }
        String str = JsonUtils.toJson(map);
        topicPushManager.push(topicName, null, str);
    }
}
