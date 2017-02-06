package com.all580.order.service.event;

import com.all580.order.api.service.event.SendTicketSyncDataEvent;
import com.all580.order.dao.MaSendResponseMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/4 15:47
 */
@Service
public class SendTicketSyncDataEventImpl extends BasicSyncDataEvent implements SendTicketSyncDataEvent {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MaSendResponseMapper maSendResponseMapper;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(content);
        Assert.notNull(item);
        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        Assert.notNull(order);

        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_order_item", CommonUtil.oneToList(item))
                .add("t_ma_send_response", maSendResponseMapper.selectByOrderItemId(item.getId()));

        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
