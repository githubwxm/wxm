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
import com.framework.common.event.MnsEventAspect;
import com.framework.common.outside.JobAspect;
import com.framework.common.outside.JobTask;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2017/2/5 11:10
 */
@Service
@Slf4j
public class RefundMoneyEventImpl implements RefundMoneyEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SmsManager smsManager;
    @Autowired
    private MnsEventAspect eventManager;
    @Autowired
    private JobAspect jobManager;
    @Autowired
    private RefundOrderManager refundOrderManager;

    @Override
    @MnsEvent
    @JobTask
    public Result process(String msgId, RefundMoneyEventParam content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(content.getSerialNo()));
        Assert.notNull(refundOrder, "退订订单不存在");
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        Assert.notNull(order, "订单不存在");

        log.info(OrderConstant.LogOperateCode.NAME, refundOrderManager.orderLog(null, refundOrder.getOrder_item_id(),
                0, "ORDER_EVENT", content.isSuccess() ? OrderConstant.LogOperateCode.REFUND_MONEY_SUCCESS : OrderConstant.LogOperateCode.REFUND_MONEY_FAIL,
                0, "订单退款回调", content.getSerialNo()));

        if (content.isSuccess()) {
            // 发送短信 退款
            if (refundOrder.getMoney() > 0) {
                smsManager.sendRefundMoneySuccessSms(refundOrder);
            }

            // 退款分账 记录任务 余额不做后续分账(和支付的时候一起)
            if (order.getPayment_type() != null && order.getPayment_type() != PaymentConstant.PaymentType.BALANCE.intValue()) {
                Map<String, String> moneyJobParams = new HashMap<>();
                moneyJobParams.put("refundId", String.valueOf(refundOrder.getId()));
                jobManager.addJob(OrderConstant.Actions.REFUND_MONEY_SPLIT_ACCOUNT, Collections.singleton(moneyJobParams));
            } else {
                eventManager.addEvent(OrderConstant.EventType.REFUND_SUCCESS, refundOrder.getId());
            }
        }

        return new Result(true);
    }
}
