package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付成功事件
 * @date 2017/1/24 14:25
 */
@EventService(OrderConstant.EventType.PAID)
public interface PaidEvent extends MnsSubscribeAction<Integer> {
}
