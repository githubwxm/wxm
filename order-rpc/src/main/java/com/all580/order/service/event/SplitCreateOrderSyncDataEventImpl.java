package com.all580.order.service.event;

import com.all580.order.api.service.event.SplitCreateOrderSyncDataEvent;
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
 * @Description: 分账成功出票
 * @date 2017/1/24 15:52
 */
@Service
public class SplitCreateOrderSyncDataEventImpl extends BasicSyncDataEvent implements SplitCreateOrderSyncDataEvent {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        Assert.notNull(order);
        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_order", order);

        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
