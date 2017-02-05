package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订申请成功事件
 * @date 2017/2/4 15:25
 */
@EventService(OrderConstant.EventType.ORDER_REFUND_APPLY)
public interface RefundApplyEvent extends MnsSubscribeAction<Integer> {
}
