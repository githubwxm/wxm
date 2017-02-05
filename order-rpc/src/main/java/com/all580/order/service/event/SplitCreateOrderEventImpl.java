package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.SplitCreateOrderEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
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
    private BookingOrderManager bookingOrderManager;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        List<Map<String, String>> jobParams = new ArrayList<>();
        List<Map<String, String>> jobGroupParams = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            Map<String, String> jobParam = new HashMap<>();
            jobParam.put("orderItemId", orderItem.getId().toString());
            // 团队票
            if (orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                    orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM) {
                jobGroupParams.add(jobParam);
                continue;
            }
            jobParams.add(jobParam);
        }
        if (jobParams.size() > 0) {
            bookingOrderManager.addJobs(OrderConstant.Actions.SEND_TICKET, jobParams);
        }
        if (jobGroupParams.size() > 0) {
            bookingOrderManager.addJobs(OrderConstant.Actions.SEND_GROUP_TICKET, jobGroupParams);
        }
        return new Result(true);
    }
}
