package com.all580.order.service.event;

import com.all580.order.api.model.RefundMoneyEventParam;
import com.all580.order.api.service.event.RefundMoneySyncDataEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
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
 * @date 2017/2/5 11:20
 */
@Service
public class RefundMoneySyncDataEventImpl extends BasicSyncDataEvent implements RefundMoneySyncDataEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result process(String msgId, RefundMoneyEventParam content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(content.getSerialNo()));
        Assert.notNull(refundOrder, "退订订单不存在");
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
