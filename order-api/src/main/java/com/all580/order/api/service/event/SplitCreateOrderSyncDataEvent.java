package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 创建订单分账事件
 * @date 2017/1/24 15:51
 */
@EventService(OrderConstant.EventType.SPLIT_CREATE_ACCOUNT)
public interface SplitCreateOrderSyncDataEvent extends MnsSubscribeAction<Integer> {
}
