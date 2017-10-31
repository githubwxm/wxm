package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundMoneyEventParam;
import com.all580.order.api.model.RefundTicketEventParam;
import com.all580.order.api.service.event.RefundTicketNotifyEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/2/23 0023.
 */
@Service
public class RefundTicketNotifyEventImpl extends BaseNotifyEvent implements RefundTicketNotifyEvent {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Override
    public Result process(String s, RefundTicketEventParam content, Date date) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content.getRefundId());
        String serialNo=refundOrder.getOuter_id();
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");
        Map<String,Object> map = new HashMap<>();
        map.put("refund_seq",serialNo);
        if(content.isStatus()){
            map.put("refund_result","APPROVE");
        }else{
            map.put("refund_result","REJECT");
        }
        map.put("refund_quantity",refundOrder.getQuantity());
        map.put("refund_outer_id", refundOrder.getOuter_id());
        map.put("audit_time", refundOrder.getAudit_time());
        map.put("refund_money_time", refundOrder.getRefund_money_time());
        map.put("refund_ticket_time", refundOrder.getRefund_ticket_time());
        map.put("apply_time", refundOrder.getCreate_time());
        notifyEvent(refundOrder.getOrder_item_id(), OrderConstant.OpCode.REFUND,map);
        return new Result(true);
    }
}
