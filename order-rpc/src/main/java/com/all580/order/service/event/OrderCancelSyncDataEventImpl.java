package com.all580.order.service.event;

import com.all580.order.api.service.event.OrderCancelSyncDataEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.Order;
import com.framework.common.Result;
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
public class OrderCancelSyncDataEventImpl extends BasicSyncDataEvent implements OrderCancelSyncDataEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
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
