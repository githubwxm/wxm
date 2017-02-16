package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundMoneyEventParam;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@EventService(OrderConstant.EventType.REFUND_MONEY)
public interface RefundMoneyNotifyEvent extends MnsSubscribeAction<RefundMoneyEventParam> {
}
