package com.all580.order.api.service.event;

import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单创建同步数据事件《为了解决 dubbo 服务暴露问题,所以这里创建了一个空的接口》
 * @date 2017/1/23 16:49
 */
public interface OrderCreateSyncDataEvent extends MnsSubscribeAction<Integer> {

}
