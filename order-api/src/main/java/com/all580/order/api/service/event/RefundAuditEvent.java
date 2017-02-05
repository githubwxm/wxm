package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundAuditEventParam;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订审核事件
 * @date 2017/2/4 15:27
 */
@EventService(OrderConstant.EventType.ORDER_REFUND_AUDIT)
public interface RefundAuditEvent extends MnsSubscribeAction<RefundAuditEventParam> {
}
