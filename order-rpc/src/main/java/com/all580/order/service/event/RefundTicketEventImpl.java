package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundTicketEventParam;
import com.all580.order.api.service.event.RefundTicketEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.manager.SmsManager;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/5 9:54
 */
@Service
public class RefundTicketEventImpl implements RefundTicketEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private SmsManager smsManager;

    @Override
    public Result process(String msgId, RefundTicketEventParam content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content.getRefundId());
        Assert.notNull(refundOrder, "退订订单不存在");
        if (content.isStatus()) {
            // 还库存 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("refundId", String.valueOf(refundOrder.getId()));
            refundOrderManager.addJob(OrderConstant.Actions.REFUND_STOCK, jobParams);
        } else {
            // 发送短信
            OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
            smsManager.sendRefundFailSms(orderItem);
        }
        return new Result(true);
    }
}
