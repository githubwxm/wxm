package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/24 16:27
 */
@EventService(OrderConstant.EventType.ORDER_CANCEL)
public interface OrderCancelSyncDataEvent extends MnsSubscribeAction<Integer> {
}
