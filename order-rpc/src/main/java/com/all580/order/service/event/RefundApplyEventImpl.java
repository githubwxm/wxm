package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundAuditEventParam;
import com.all580.order.api.service.event.RefundApplyEvent;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventAspect;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RefundApplyEventImpl implements RefundApplyEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private MnsEventAspect eventManager;
    @Autowired
    private RefundOrderManager refundOrderManager;

    @Override
    @MnsEvent
    public Result process(String msgId, Integer content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content);
        Assert.notNull(refundOrder, "退订订单不存在");
        log.info(OrderConstant.LogOperateCode.NAME, refundOrderManager.orderLog(null, refundOrder.getOrder_item_id(),
                refundOrder.getApply_user_id(), refundOrder.getApply_user_name(), OrderConstant.LogOperateCode.REFUND_APPLY_SUCCESS,
                refundOrder.getQuantity(), String.format("退订申请:退订订单:%s", refundOrder.getNumber()), String.valueOf(refundOrder.getNumber())));
        // 判断是否需要退订审核
        if (refundOrder.getAudit_ticket() == ProductConstants.RefundAudit.NO
                && refundOrder.getStatus() == OrderConstant.RefundOrderStatus.AUDIT_WAIT) {
            eventManager.addEvent(OrderConstant.EventType.ORDER_REFUND_AUDIT, new RefundAuditEventParam(content, true));
        }
        return new Result(true);
    }
}
