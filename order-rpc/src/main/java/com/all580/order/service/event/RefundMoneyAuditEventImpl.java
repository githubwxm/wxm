package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.RefundMoneyAuditEvent;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by alone on 17-4-21.
 */
@Service
@Slf4j
public class RefundMoneyAuditEventImpl implements RefundMoneyAuditEvent {
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Override
    public Result process(String s, Integer integer, Date date) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(integer);
        log.info(OrderConstant.LogOperateCode.NAME, refundOrderManager.orderLog(refundOrder.getOrder_item_id(),
                refundOrder.getAudit_money_user_id(), refundOrder.getAudit_money_user_name(), OrderConstant.LogOperateCode.REFUND_MONEY_AUDIT_SUCCESS,
                refundOrder.getQuantity(), String.format("退订退款申请审核:退订订单:%s", refundOrder.getNumber())));
        return new Result(true);
    }
}
