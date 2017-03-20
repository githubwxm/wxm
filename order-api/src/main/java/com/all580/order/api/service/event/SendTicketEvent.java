package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 出票成功事件
 * @date 2017/2/4 15:46
 */
@EventService(OrderConstant.EventType.SEND_TICKET)
public interface SendTicketEvent extends MnsSubscribeAction<Integer> {
}
