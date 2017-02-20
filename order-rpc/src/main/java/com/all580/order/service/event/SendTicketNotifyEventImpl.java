package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.SendTicketNotifyEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.RefundOrder;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.mns.TopicPushManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@Service
public class SendTicketNotifyEventImpl extends BaseNotifyEvent implements SendTicketNotifyEvent {


    @Override
    public Result process(String s, Integer integer, Date date) {
        notifyEvent(integer, "SENT",null);
        return new Result(true);
    }
}
