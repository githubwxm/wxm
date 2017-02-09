package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 子订单待审核事件
 * @date 2017/2/9 11:18
 */
@EventService(OrderConstant.EventType.ORDER_WAIT_AUDIT)
public interface OrderItemWaitAuditEvent extends MnsSubscribeAction<Integer> {
}
