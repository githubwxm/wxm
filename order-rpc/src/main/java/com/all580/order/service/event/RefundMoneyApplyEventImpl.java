package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.RefundMoneyApplyEvent;
import com.all580.order.dao.OrderItemMapper;
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
public class RefundMoneyApplyEventImpl implements RefundMoneyApplyEvent {
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Override
    public Result process(String s, Integer integer, Date date) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(integer);
        log.info(OrderConstant.LogOperateCode.NAME, refundOrderManager.orderLog(refundOrder.getOrder_item_id(), date,
                0, "ORDER_EVENT", OrderConstant.LogOperateCode.REFUND_MONEY_APPLY,
                refundOrder.getQuantity(), String.format("退订退款申请:退订订单:%s", refundOrder.getNumber())));
        return new Result(true);
    }
}
