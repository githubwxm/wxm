package com.all580.order.service.event;

import com.all580.order.api.service.event.OrderItemWaitAuditEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.SmsManager;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/9 11:19
 */
@Slf4j
@Service
public class OrderItemWaitAuditEventImpl implements OrderItemWaitAuditEvent {
    @Autowired
    private SmsManager smsManager;

    @Autowired
    private OrderItemMapper orderItemMapper;
    /**
     * 执行
     *
     * @param msgId      消息ID
     * @param content    内容
     * @param createDate 创建时间
     * @return
     */
    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(content);
        Assert.notNull(orderItem);
        smsManager.sendAuditSms(orderItem);
        return new Result(true);
    }
}
