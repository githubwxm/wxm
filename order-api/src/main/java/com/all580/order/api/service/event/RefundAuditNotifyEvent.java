package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundAuditEventParam;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * Created by wxming on 2017/2/28 0028.   退订审核事件
 */
@EventService(OrderConstant.EventType.ORDER_REFUND_AUDIT)
public interface RefundAuditNotifyEvent  extends MnsSubscribeAction<RefundAuditEventParam> {
}
