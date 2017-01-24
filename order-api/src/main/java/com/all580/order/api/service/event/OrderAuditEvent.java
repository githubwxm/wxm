package com.all580.order.api.service.event;

import com.all580.order.api.model.OrderAuditEventParam;
import com.framework.common.mns.MnsSubscribeAction;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单审核事件
 * @date 2017/1/24 14:51
 */
public interface OrderAuditEvent extends MnsSubscribeAction<OrderAuditEventParam> {
}
