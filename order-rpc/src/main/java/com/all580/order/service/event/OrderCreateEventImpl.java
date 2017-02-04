package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.OrderCreateEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.BookingOrderManager;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单创建事件执行器
 * @date 2017/1/23 16:51
 */
@Slf4j
@Service
public class OrderCreateEventImpl implements OrderCreateEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        Order order = orderMapper.selectByPrimaryKey(content);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        // 到付产品 || 0元产品
        if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT && order.getPay_amount() <= 0) {
            bookingOrderManager.addPaymentCallback(order);
        }
        return new Result(true);
    }

    @Override
    public String key() {
        return OrderConstant.EventType.ORDER_CREATE;
    }
}
