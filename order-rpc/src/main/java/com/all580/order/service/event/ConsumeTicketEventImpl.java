package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.ConsumeTicketEventParam;
import com.all580.order.api.service.event.ConsumeTicketEvent;
import com.all580.order.dao.OrderClearanceSerialMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.entity.OrderClearanceSerial;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.framework.common.Result;
import com.framework.common.outside.JobAspect;
import com.framework.common.outside.JobTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/9 11:49
 */
@Service
public class ConsumeTicketEventImpl implements ConsumeTicketEvent {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;

    @Autowired
    private JobAspect jobManager;
    /**
     * 执行
     *
     * @param msgId      消息ID
     * @param content    内容
     * @param createDate 创建时间
     * @return
     */
    @Override
    @JobTask
    public Result process(String msgId, ConsumeTicketEventParam content, Date createDate) {
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(content.getItemId());
        Assert.notNull(orderItem);
        OrderClearanceSerial serial = orderClearanceSerialMapper.selectByPrimaryKey(content.getSerialId());
        Assert.notNull(serial);

        if (serial.getSplit() == null || !serial.getSplit()) {
            // 分账
            // 核销成功 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("sn", serial.getSerial_no());
            jobManager.addJob(OrderConstant.Actions.CONSUME_SPLIT_ACCOUNT, Collections.singleton(jobParams));

            bookingOrderManager.dealPackageOrderItemConSume(orderItem);
        }
        return new Result(true);
    }
}
