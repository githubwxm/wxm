package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.SplitCreateOrderEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventAspect;
import com.framework.common.outside.JobAspect;
import com.framework.common.outside.JobTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 分账成功出票
 * @date 2017/1/24 15:52
 */
@Service
public class SplitCreateOrderEventImpl implements SplitCreateOrderEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private JobAspect jobManager;
    @Autowired
    private MnsEventAspect eventManager;

    @Override
    @JobTask
    @MnsEvent
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        List<Map<String, String>> jobParams = new ArrayList<>();
        List<Map<String, String>> jobGroupParams = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getPro_type() != ProductConstants.ProductType.PACKAGE){
                Map<String, String> jobParam = new HashMap<>();
                jobParam.put("orderItemId", orderItem.getId().toString());
                // 团队票
                if (orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                        orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM &&
                        orderItem.getPro_type() == ProductConstants.ProductType.SCENERY) {
                    jobGroupParams.add(jobParam);
                    continue;
                }
                // 酒店 线路
                if (orderItem.getPro_type() == ProductConstants.ProductType.HOTEL ||
                        orderItem.getPro_type() == ProductConstants.ProductType.ITINERARY) {
                    selfSend(orderItem);
                    continue;
                }
                jobParams.add(jobParam);
            }
        }
        if (jobParams.size() > 0) {
            jobManager.addJob(OrderConstant.Actions.SEND_TICKET, jobParams);
        }
        if (jobGroupParams.size() > 0) {
            jobManager.addJob(OrderConstant.Actions.SEND_GROUP_TICKET, jobGroupParams);
        }
        return new Result(true);
    }

    private void selfSend(OrderItem orderItem) {
        orderItem.setStatus(OrderConstant.OrderItemStatus.SEND);
        orderItem.setSend_ma_time(new Date());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
        eventManager.addEvent(OrderConstant.EventType.SEND_TICKET, orderItem.getId());
    }
}
