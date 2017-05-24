package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.SendTicketEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.SmsManager;
import com.all580.product.api.consts.ProductConstants;
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

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(content);
        Assert.notNull(item);

        // 酒店发送短信
        if (item.getPro_type() == ProductConstants.ProductType.HOTEL) {
            smsManager.sendHotelSendTicket(item);
        }
        // 线路发送短信
        if (item.getPro_type() == ProductConstants.ProductType.ITINERARY) {
            smsManager.sendLineSendTicket(item);
        }
        log.info(OrderConstant.LogOperateCode.NAME, bookingOrderManager.orderLog(null, item.getId(),
                0, "ORDER_EVENT", OrderConstant.LogOperateCode.SENDED,
                item.getQuantity(), "已出票"));
        return new Result(true);
    }
}
