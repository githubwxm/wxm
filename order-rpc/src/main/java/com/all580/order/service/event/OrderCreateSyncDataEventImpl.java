package com.all580.order.service.event;

import com.all580.order.api.service.event.OrderCreateSyncDataEvent;
import com.all580.order.dao.*;
import com.all580.order.entity.Order;
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
 * @Description: 订单创建同步事件执行器
 * @date 2017/1/23 16:51
 */
@Slf4j
@Service
public class OrderCreateSyncDataEventImpl implements OrderCreateSyncDataEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Autowired
    private TopicPushManager topicPushManager;
    @Value("${mns.topic}")
    private String topicName;


    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        String accessKey = bookingOrderManager.getAccessKey(order.getPayee_ep_id());
        SynchronizeDataMap dataMap = new SynchronizeDataMap(accessKey)
                .add("t_order", CommonUtil.oneToList(order))
                .add("t_order_item", orderItemMapper.selectByOrderId(content))
                .add("t_order_item_detail", orderItemDetailMapper.selectByOrderId(content))
                .add("t_order_item_account", orderItemAccountMapper.selectByOrder(content))
                .add("t_shipping", CommonUtil.oneToList(shippingMapper.selectByOrder(content)))
                .add("t_visitor", visitorMapper.selectByOrder(content));

        try {
            dataMap.sendToMns(topicPushManager, topicName).clear();
        } catch (Throwable e) {
            throw new ApiException("同步数据异常", e);
        }
        return new Result(true);
    }
}
