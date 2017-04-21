package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.OrderAuditEventParam;
import com.all580.order.api.service.event.OrderAuditEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.manager.SmsManager;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/24 14:56
 */
@Service
@Slf4j
public class OrderAuditEventImpl implements OrderAuditEvent {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private SmsManager smsManager;

    @Override
    public Result process(String msgId, OrderAuditEventParam content, Date createDate) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(content.getItemId());
        if (item == null) {
            throw new ApiException("子订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        log.info(OrderConstant.LogOperateCode.NAME, bookingOrderManager.orderLog(item.getId(), item.getAudit_time(),
                item.getAudit_user_id(), item.getAudit_user_name(),
                content.isStatus() ? OrderConstant.LogOperateCode.AUDIT_PASS_SUCCESS : OrderConstant.LogOperateCode.AUDIT_REJECT_SUCCESS,
                item.getQuantity(), "订单预定审核"));
        if (content.isStatus()) {
            boolean allAudit = bookingOrderManager.isOrderAllAudit(item.getOrder_id());
            if (allAudit) {
                // TODO: 2016/11/16  目前只支持单子订单发送
                smsManager.sendAuditSuccess(item);
            }
            return new Result(true);
        }

        // 不通过取消订单
        refundOrderManager.cancel(order);
        return new Result(true);
    }
}
