package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundAuditEventParam;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订审核同步数据事件
 * @date 2017/2/4 15:27
 */
@EventService(value = OrderConstant.EventType.ORDER_REFUND_AUDIT, order = 2)
public interface RefundAuditSyncDataEvent extends MnsSubscribeAction<RefundAuditEventParam> {
}
