package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.SendTicketEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.SmsManager;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/4 15:47
 */
@Service
@Slf4j
public class SendTicketEventImpl implements SendTicketEvent {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private SmsManager smsManager;
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(content);
        Assert.notNull(item);

        log.info(OrderConstant.LogOperateCode.NAME, bookingOrderManager.orderLog(null, item.getId(),
                0, "ORDER_EVENT", OrderConstant.LogOperateCode.SENDED,
                item.getQuantity() * item.getDays(), "已出票", null));

        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        if (order.getSource() == OrderConstant.OrderSourceType.SOURCE_TYPE_SYS){
            //处理套票
            bookingOrderManager.checkTicketOrderItemChainForPackage(item);
        }

        return new Result(true);
    }
}
