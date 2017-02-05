package com.all580.order.service.event;

import com.all580.order.api.model.RefundMoneyEventParam;
import com.all580.order.api.service.event.RefundMoneySyncDataEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.Result;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.synchronize.SynchronizeDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/5 11:20
 */
@Service
public class RefundMoneySyncDataEventImpl implements RefundMoneySyncDataEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private TopicPushManager topicPushManager;
    @Value("${mns.topic}")
    private String topicName;

    @Override
    public Result process(String msgId, RefundMoneyEventParam content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(content.getSerialNo()));
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");

        String accessKey = refundOrderManager.getAccessKey(order.getPayee_ep_id());
        SynchronizeDataMap dataMap = new SynchronizeDataMap(accessKey)
                .add("t_refund_order", refundOrder);

        try {
            dataMap.sendToMns(topicPushManager, topicName).clear();
        } catch (Throwable e) {
            throw new ApiException("同步数据异常", e);
        }
        return new Result(true);
    }
}