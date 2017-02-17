package com.all580.order.service.event;

import com.all580.order.api.model.ConsumeTicketEventParam;
import com.all580.order.api.service.event.ConsumeTicketNotifyEvent;
import com.all580.order.api.service.event.SendTicketNotifyEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.entity.OrderItem;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@Service
public class ConsumeTicketNotifyEventImpl extends BaseNotifyEvent implements ConsumeTicketNotifyEvent {


    @Override
    public Result process(String s, ConsumeTicketEventParam content, Date date) {
        notifyEvent(content.getItemId(), "CONSUME");
        return new Result(true);
    }
}
