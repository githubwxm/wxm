package com.all580.order.service.event;

import com.all580.order.api.service.event.SendTicketNotifyEvent;
import com.framework.common.Result;

import java.util.Date;

/**
 * Created by wxming on 2017/2/16 0016.
 */
public class ConsumeTicketNotifyEventImpl extends BaseNotifyEvent implements SendTicketNotifyEvent {


    @Override
    public Result process(String s, Integer integer, Date date) {
        notifyEvent(integer, "CONSUME");
        return new Result(true);
    }
}
