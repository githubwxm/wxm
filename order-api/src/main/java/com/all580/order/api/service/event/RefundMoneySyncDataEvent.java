package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundMoneyEventParam;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退款结果同步数据事件
 * @date 2017/2/5 11:10
 */
@EventService(OrderConstant.EventType.REFUND_MONEY)
public interface RefundMoneySyncDataEvent extends MnsSubscribeAction<RefundMoneyEventParam> {
}
