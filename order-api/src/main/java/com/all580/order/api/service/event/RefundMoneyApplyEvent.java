package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * 退款申请事件
 * Created by alone on 17-4-21.
 */
@EventService(OrderConstant.EventType.REFUND_MONEY_APPLY)
public interface RefundMoneyApplyEvent extends MnsSubscribeAction<Integer> {
}
