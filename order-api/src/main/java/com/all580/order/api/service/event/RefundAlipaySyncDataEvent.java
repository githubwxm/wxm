package com.all580.order.api.service.event;

import com.all580.order.api.OrderConstant;
import com.framework.common.event.EventService;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付宝退款确认同步数据事件
 * @date 2017/2/5 10:34
 */
@EventService(OrderConstant.EventType.REFUND_ALI_PAY_AUDIT)
public interface RefundAliPaySyncDataEvent extends MnsSubscribeAction<Integer> {
}
