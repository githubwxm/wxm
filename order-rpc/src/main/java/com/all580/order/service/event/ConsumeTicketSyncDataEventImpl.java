package com.all580.order.service.event;

import com.all580.order.api.model.ConsumeTicketEventParam;
import com.all580.order.api.service.event.ConsumeTicketSyncDataEvent;
import com.all580.order.dao.*;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderClearanceSerial;
import com.all580.order.entity.OrderItem;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.synchronize.SyncAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/9 11:49
 */
@Service
public class ConsumeTicketSyncDataEventImpl extends BasicSyncDataEvent implements ConsumeTicketSyncDataEvent {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
    @Autowired
    private OrderClearanceDetailMapper orderClearanceDetailMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private GroupConsumeMapper groupConsumeMapper;

    /**
     * 执行
     *
     * @param msgId      消息ID
     * @param content    内容
     * @param createDate 创建时间
     * @return
     */
    @Override
    public Result process(String msgId, ConsumeTicketEventParam content, Date createDate) {
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(content.getItemId());
        Assert.notNull(orderItem);
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        Assert.notNull(order);
        OrderClearanceSerial serial = orderClearanceSerialMapper.selectByPrimaryKey(content.getSerialId());
        Assert.notNull(serial);

        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_order_item_detail", orderItemDetailMapper.selectByItemId(orderItem.getId()))
                .add("t_order_item", orderItem)
                .add("t_order_clearance_serial", serial)
                .add("t_order_clearance_detail", orderClearanceDetailMapper.selectBySn(serial.getSerial_no()))
                .add("t_visitor", visitorMapper.selectByOrderItem(orderItem.getId()));

        if (orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM) {
            syncAccess.getDataMap().add("t_group_consume", groupConsumeMapper.selectBySn(serial.getSerial_no()));
        }
        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
