package com.all580.order.api.service.event;

import com.all580.order.api.model.RefundMoneyEventParam;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退款结果事件
 * @date 2017/2/5 11:08
 */
public interface RefundMoneyEvent extends MnsSubscribeAction<RefundMoneyEventParam> {
}
