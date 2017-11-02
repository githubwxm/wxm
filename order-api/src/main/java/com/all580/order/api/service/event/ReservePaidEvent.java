package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * Created by wxming on 2017/11/2 0002.
 */
@EventService(OrderConstant.EventType.PAID)
public interface ReservePaidEvent extends MnsSubscribeAction<Integer> {
}
