package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.RefundMoneyEventParam;
import com.all580.order.api.service.event.RefundMoneyEvent;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.manager.SmsManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventManager;
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
 * @date 2017/2/5 11:10
 */
@Service
public class RefundMoneyEventImpl implements RefundMoneyEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private SmsManager smsManager;

    @Override
    @MnsEvent
    public Result process(String msgId, RefundMoneyEventParam content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(content.getSerialNo()));
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");

        if (content.isSuccess()) {
            // 发送短信 退款
            if (refundOrder.getMoney() > 0) {
                smsManager.sendRefundMoneySuccessSms(refundOrder);
            }

            // 退款分账 记录任务 余额不做后续分账(和支付的时候一起)
            if (order.getPayment_type() != null && order.getPayment_type() != PaymentConstant.PaymentType.BALANCE.intValue()) {
                Map<String, String> moneyJobParams = new HashMap<>();
                moneyJobParams.put("refundId", String.valueOf(refundOrder.getId()));
                refundOrderManager.addJob(OrderConstant.Actions.REFUND_MONEY_SPLIT_ACCOUNT, moneyJobParams, false);
            } else {
                MnsEventManager.addEvent(OrderConstant.EventType.REFUND_SUCCESS, refundOrder.getId());
            }
        }
        return new Result(true);
    }
}
