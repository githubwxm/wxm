package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.RefundMoneyApplyEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.Result;
import com.framework.common.synchronize.SyncAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Created by alone on 17-4-21.
 */
@Service
@Slf4j
public class RefundMoneyApplyEventImpl extends BasicSyncDataEvent implements RefundMoneyApplyEvent {
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Override
    public Result process(String s, Integer integer, Date date) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(integer);
        log.info(OrderConstant.LogOperateCode.NAME, refundOrderManager.orderLog(null, refundOrder.getOrder_item_id(),
                0, "ORDER_EVENT", OrderConstant.LogOperateCode.REFUND_MONEY_APPLY,
                refundOrder.getQuantity(), String.format("退订退款申请:退订订单:%s", refundOrder.getNumber()), String.valueOf(refundOrder.getNumber())));
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");

        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_refund_order", refundOrder);

        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
