package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订成功同步数据事件
 * @date 2017/2/5 11:41
 */
@EventService(OrderConstant.EventType.REFUND_SUCCESS)
public interface RefundSuccessSyncDataEvent extends MnsSubscribeAction<Integer> {
}
