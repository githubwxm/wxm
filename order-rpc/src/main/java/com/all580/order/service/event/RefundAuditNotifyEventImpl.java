package com.all580.order.service.event;


import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundAuditEventParam;
import com.all580.order.api.service.event.RefundAuditNotifyEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/2/28 0028.
 */
@Slf4j
@Service
public class RefundAuditNotifyEventImpl  extends BaseNotifyEvent implements RefundAuditNotifyEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Autowired
    private OrderMapper orderMapper;
    @Override
    public Result process(String s, RefundAuditEventParam content, Date date) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content.getRefundId());
        Assert.notNull(refundOrder, "退订订单不存在");
        String serialNo=refundOrder.getOuter_id();
        if (!content.isStatus()) {
            Assert.notNull(refundOrder, "退订订单不存在");
            Map<String,Object> map = new HashMap<>();
            map.put("refund_seq",serialNo);
            map.put("refund_result","REJECT");
            map.put("refund_quantity",refundOrder.getQuantity());
            map.put("refund_outer_id", refundOrder.getOuter_id());
            map.put("audit_time", refundOrder.getAudit_time());
            map.put("refund_money_time", refundOrder.getRefund_money_time());
            map.put("refund_ticket_time", refundOrder.getRefund_ticket_time());
            map.put("apply_time", refundOrder.getCreate_time());
            log.info("退订审核不通过 Order_item_id: {} " ,refundOrder.getOrder_item_id());
            notifyEvent(refundOrder.getOrder_item_id(), OrderConstant.OpCode.REFUND,map);
        }
        return new Result(true);
    }
}
