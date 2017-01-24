package com.all580.order.api.service.event;

import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付成功事件
 * @date 2017/1/24 14:25
 */
public interface PaidSyncDataEvent extends MnsSubscribeAction<Integer> {
}
