package com.all580.order.service.event;

import com.all580.order.api.service.event.SplitCreateOrderSyncDataEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.BookingOrderManager;
import com.framework.common.Result;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.synchronize.SynchronizeDataMap;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 分账成功出票
 * @date 2017/1/24 15:52
 */
@Service
public class SplitCreateOrderSyncDataEventImpl implements SplitCreateOrderSyncDataEvent {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TopicPushManager topicPushManager;
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Value("${mns.topic}")
    private String topicName;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        String accessKey = bookingOrderManager.getAccessKey(order.getPayee_ep_id());
        SynchronizeDataMap dataMap = new SynchronizeDataMap(accessKey)
                .add("t_order", CommonUtil.oneToList(order));

        try {
            dataMap.sendToMns(topicPushManager, topicName).clear();
        } catch (Throwable e) {
            throw new ApiException("同步数据异常", e);
        }
        return new Result(true);
    }
}
