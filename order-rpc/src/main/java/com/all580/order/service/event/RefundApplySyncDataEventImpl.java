package com.all580.order.service.event;

import com.all580.order.api.service.event.RefundApplySyncDataEvent;
import com.all580.order.dao.*;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundAccount;
import com.all580.order.entity.RefundOrder;
import com.framework.common.Result;
import com.framework.common.synchronize.SyncAccess;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/4 15:35
 */
@Service
public class RefundApplySyncDataEventImpl extends BasicSyncDataEvent implements RefundApplySyncDataEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;
    @Autowired
    private RefundAccountMapper refundAccountMapper;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content);
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");

        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_refund_order", CommonUtil.oneToList(refundOrder))
                .add("t_order_item_detail", orderItemDetailMapper.selectByItemId(refundOrder.getOrder_item_id()))
                .add("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundOrder.getId()));

        for (Integer coreEpId : syncAccess.getAccessKeyMap().keySet()) {
            List<RefundAccount> accounts = refundAccountMapper.selectByRefundIdAndCore(refundOrder.getId(), coreEpId);
            if (coreEpId.intValue() == syncAccess.getCoreEpId()) {
                syncAccess.getDataMap().add("t_refund_account", accounts);
            } else {
                syncAccess.addDataMap(syncAccess.copy(coreEpId)
                        .add("t_refund_account", accounts));
            }
        }

        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
