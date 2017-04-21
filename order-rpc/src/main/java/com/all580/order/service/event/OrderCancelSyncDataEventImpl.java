package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.OrderCancelSyncDataEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.Order;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

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

        log.info(OrderConstant.LogOperateCode.NAME, refundOrderManager.orderLog(order.getId(), createDate,
                0,  "ORDER_EVENT",
                OrderConstant.LogOperateCode.CANCEL_SUCCESS,
                null, "订单取消"));

        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_order", order)
                .add("t_order_item", orderItemMapper.selectByOrderId(order.getId()));

        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
