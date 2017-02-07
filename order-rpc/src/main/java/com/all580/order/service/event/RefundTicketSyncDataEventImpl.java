package com.all580.order.service.event;

import com.all580.order.api.model.RefundTicketEventParam;
import com.all580.order.api.service.event.RefundTicketSyncDataEvent;
import com.all580.order.dao.*;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/5 9:54
 */
@Service
public class RefundTicketSyncDataEventImpl extends BasicSyncDataEvent implements RefundTicketSyncDataEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RefundSerialMapper refundSerialMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;

    @Override
    public Result process(String msgId, RefundTicketEventParam content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content.getRefundId());
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");

        SyncAccess syncAccess = getAccessKeys(order);
        if (content.isStatus()) {
            syncAccess.getDataMap()
                    .add("t_refund_order", refundOrder)
                    .add("t_order_item", orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id()))
                    .add("t_refund_serial", refundSerialMapper.selectByRefundOrder(refundOrder.getId()))
                    .add("t_visitor", visitorMapper.selectByOrderItem(refundOrder.getOrder_item_id()))
                    .add("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundOrder.getId()));
        } else {
            // 拒绝
            syncAccess.getDataMap()
                    .add("t_refund_order", refundOrder)
                    .add("t_order_item_detail", orderItemDetailMapper.selectByItemId(refundOrder.getOrder_item_id()));
        }

        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
