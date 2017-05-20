package com.all580.order.service.event;

import com.all580.order.api.service.event.OrderCreateSyncDataEvent;
import com.all580.order.dao.*;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItemAccount;
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
 * @Description: 订单创建同步事件执行器
 * @date 2017/1/23 16:51
 */
@Slf4j
@Service
public class OrderCreateSyncDataEventImpl extends BasicSyncDataEvent implements OrderCreateSyncDataEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private VisitorMapper visitorMapper;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        Assert.notNull(order, "订单不存在");

        SyncAccess syncAccess = getAccessKeys(order);

        syncAccess.getDataMap()
                .add("t_order", order)
                .add("t_order_item", orderItemMapper.selectByOrderId(content))
                .add("t_order_item_detail", orderItemDetailMapper.selectByOrderId(content))
                .add("t_shipping", shippingMapper.selectByOrder(content))
                .add("t_visitor", visitorMapper.selectByOrder(content));

        for (Integer coreEpId : syncAccess.getAccessKeyMap().keySet()) {
            List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderAndCore(content, coreEpId);
            if (coreEpId.intValue() == syncAccess.getCoreEpId()) {
                syncAccess.getDataMap().add("t_order_item_account", accounts);
            } else {
                syncAccess.addDataMap(syncAccess.copy(coreEpId)
                        .add("t_order_item_account", accounts));
            }
        }

        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
