package com.all580.order.service.event;

import com.all580.order.api.model.OrderAuditEventParam;
import com.all580.order.api.service.event.OrderAuditSyncDataEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.framework.common.Result;
import com.framework.common.synchronize.SyncAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/24 14:56
 */
@Service
@Slf4j
public class OrderAuditEventSyncDataImpl extends BasicSyncDataEvent implements OrderAuditSyncDataEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Result process(String msgId, OrderAuditEventParam content, Date createDate) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(content.getItemId());
        Assert.notNull(item);
        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        Assert.notNull(order);

        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_order", order)
                .add("t_order_item", orderItemMapper.selectByOrderId(order.getId()));

        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
