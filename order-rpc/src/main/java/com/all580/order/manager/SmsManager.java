package com.all580.order.manager;

import com.all580.notice.api.conf.SmsType;
import com.all580.notice.api.service.SmsService;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.ShippingMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.RefundOrder;
import com.all580.order.entity.Shipping;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 短信管理器
 * @date 2016/11/8 16:38
 */
@Component
public class SmsManager {
    @Autowired
    private SmsService smsService;

    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 发送核销短信
     * @param orderItem
     * @param quantity
     * @return
     */
    public Result sendConsumeSms(OrderItem orderItem, int quantity) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrderId());
        if (shipping == null) {
            return new Result(false, "订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        if (order == null) {
            return new Result(false, "订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("name", orderItem.getProSubName());
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("num", String.valueOf(orderItem.getQuantity()));
        sendSmsParams.put("xiaofeishuliang", String.valueOf(quantity));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.ORDER_CONSUME, order.getPayeeEpId(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            return new Result(false, "发送核销短信失败");
        }
        return new Result(true);
    }

    /**
     * 发送反核销短信
     * @param orderItem
     * @param consumeQuantity
     * @param reQuantity
     * @return
     */
    public Result sendReConsumeSms(OrderItem orderItem, int consumeQuantity, int reQuantity) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrderId());
        if (shipping == null) {
            return new Result(false, "订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        if (order == null) {
            return new Result(false, "订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("dingdanhao", String.valueOf(orderItem.getNumber()));
        sendSmsParams.put("shuliang1", String.valueOf(consumeQuantity));
        sendSmsParams.put("shuliang2", String.valueOf(reQuantity));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.CONSUME_SUCCESS, order.getPayeeEpId(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            return new Result(false, "发送反核销短信失败");
        }
        return new Result(true);
    }

    /**
     * 发送订单退票失败短信
     * @param orderItem
     * @return
     */
    public Result sendRefundFailSms(OrderItem orderItem) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrderId());
        if (shipping == null) {
            return new Result(false, "订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        if (order == null) {
            return new Result(false, "订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("dingdanhao", String.valueOf(orderItem.getNumber()));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.MONEY_REFUND_FAIL, order.getPayeeEpId(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            return new Result(false, "发送退票失败短信失败");
        }
        return new Result(true);
    }

    /**
     * 发送退订成功短信
     * @param refundOrder
     * @return
     */
    public Result sendRefundSuccessSms(RefundOrder refundOrder) {
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrderItemId());

        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrderId());
        if (shipping == null) {
            return new Result(false, "订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        if (order == null) {
            return new Result(false, "订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("dingdanhao", String.valueOf(orderItem.getNumber()));
        sendSmsParams.put("num", String.valueOf(refundOrder.getQuantity()));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.ORDER_REFUND, order.getPayeeEpId(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            return new Result(false, "发送退订成功短信失败");
        }
        return new Result(true);
    }

    /**
     * 发送退款成功短信
     * @param refundOrder
     * @return
     */
    public Result sendRefundMoneySuccessSms(RefundOrder refundOrder) {
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrderItemId());

        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrderId());
        if (shipping == null) {
            return new Result(false, "订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        if (order == null) {
            return new Result(false, "订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("dingdanhao", String.valueOf(order.getNumber()));
        sendSmsParams.put("money", String.valueOf(refundOrder.getMoney()));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.MONEY_REFUND, order.getPayeeEpId(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            return new Result(false, "发送退款成功短信失败");
        }
        return new Result(true);
    }

    /**
     * 发送预定审核短信
     * @param orderItem
     * @param refundQuantity
     * @return
     */
    public Result sendAuditSms(OrderItem orderItem) {
        // TODO: 2016/11/8  供应商产品管理员获取不到
        return new Result(false);
    }

    /**
     * 发送预定审核失败短信
     * @param orderItem
     * @return
     */
    public Result sendAuditRefuseSms(OrderItem orderItem) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrderId());
        if (shipping == null) {
            return new Result(false, "订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        if (order == null) {
            return new Result(false, "订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("chanpinmingcheng", orderItem.getProSubName());
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("shuliang", String.valueOf(orderItem.getQuantity()));
        // TODO: 2016/11/8 找不到电话号码
        //sendSmsParams.put("dianhuahaoma", null);
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.SUPPLIER_ORDER_REFUSE, order.getPayeeEpId(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            return new Result(false, "发送退订审核失败短信失败");
        }
        return new Result(true);
    }
}
