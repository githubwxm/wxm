package com.all580.order.api.service.event;

import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 创建订单分账事件
 * @date 2017/1/24 15:51
 */
public interface SplitCreateOrderSyncDataEvent extends MnsSubscribeAction<Integer> {
}
