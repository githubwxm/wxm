package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundTicketEventParam;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退票结果同步数据事件
 * @date 2017/2/5 9:51
 */
@EventService(OrderConstant.EventType.REFUND_TICKET)
public interface RefundTicketSyncDataEvent extends MnsSubscribeAction<RefundTicketEventParam> {
}
