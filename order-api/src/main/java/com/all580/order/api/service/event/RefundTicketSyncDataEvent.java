package com.all580.order.api.service.event;

import com.all580.order.api.model.RefundTicketEventParam;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退票结果同步数据事件
 * @date 2017/2/5 9:51
 */
public interface RefundTicketSyncDataEvent extends MnsSubscribeAction<RefundTicketEventParam> {
}
