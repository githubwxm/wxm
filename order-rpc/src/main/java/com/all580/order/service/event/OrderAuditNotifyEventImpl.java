package com.all580.order.service.event;

import com.all580.order.api.model.OrderAuditEventParam;
import com.all580.order.api.service.event.OrderAuditNotifyEvent;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@Slf4j
@Service
public class OrderAuditNotifyEventImpl  extends BaseNotifyEvent implements OrderAuditNotifyEvent {
    @Override
    public Result process(String s, OrderAuditEventParam orderAuditEventParam, Date date) {
        log.error("查看审核状态"+orderAuditEventParam.toString());
        notifyEvent(  orderAuditEventParam.getItemId(), "ORTHER",null);
        return new Result(true);
    }
}
