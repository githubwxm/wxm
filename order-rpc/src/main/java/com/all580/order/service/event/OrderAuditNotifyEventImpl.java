package com.all580.order.service.event;

import com.all580.order.api.model.OrderAuditEventParam;
import com.all580.order.api.service.event.CancelTicketNotifyEvent;
import com.all580.order.api.service.event.OrderAuditNotifyEvent;
import com.framework.common.Result;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@Service
public class OrderAuditNotifyEventImpl  extends BaseNotifyEvent implements OrderAuditNotifyEvent {
    @Override
    public Result process(String s, OrderAuditEventParam orderAuditEventParam, Date date) {
        notifyEvent(  orderAuditEventParam.getItemId(), "ORTHER",null);
        return new Result(true);
    }
}
