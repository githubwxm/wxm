package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.OrderCancelSyncDataEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.Result;
import com.framework.common.synchronize.SyncAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/24 16:28
 */
@Service
@Slf4j
public class OrderCancelSyncDataEventImpl extends BasicSyncDataEvent implements OrderCancelSyncDataEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RefundOrderManager refundOrderManager;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        Assert.notNull(order);

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem orderItem : orderItems) {
            log.info(OrderConstant.LogOperateCode.NAME, refundOrderManager.orderLog(null, orderItem.getId(),
                    0,  "ORDER_EVENT",
                    OrderConstant.LogOperateCode.CANCEL_SUCCESS,
                    null, "订单取消", null));
        }

        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_order", order)
                .add("t_order_item", orderItems);

        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
