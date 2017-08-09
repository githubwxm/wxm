package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * Created by wxming on 2017/8/9 0009.   支付成功推送
 */
@EventService(OrderConstant.EventType.PAID)
public interface PaidNotifyEvent  extends MnsSubscribeAction<Integer> {
}
