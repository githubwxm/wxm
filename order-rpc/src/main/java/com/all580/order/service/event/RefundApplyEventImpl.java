package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundAuditEventParam;
import com.all580.order.api.service.event.RefundApplyEvent;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.RefundOrder;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/4 15:35
 */
@Service
public class RefundApplyEventImpl implements RefundApplyEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private MnsEventAspect eventManager;

    @Override
    @MnsEvent
    public Result process(String msgId, Integer content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content);
        Assert.notNull(refundOrder, "退订订单不存在");
        // 判断是否需要退订审核
        if (refundOrder.getAudit_ticket() == ProductConstants.RefundAudit.NO) {
            eventManager.addEvent(OrderConstant.EventType.ORDER_REFUND_AUDIT, new RefundAuditEventParam(content, true));
        }
        return new Result(true);
    }
}
