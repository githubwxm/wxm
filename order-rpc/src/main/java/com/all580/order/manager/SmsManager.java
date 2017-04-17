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
import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 短信管理器
 * @date 2016/11/8 16:38
 */
@Component
@Slf4j
public class SmsManager {
    @Autowired
    private SmsService smsService;

    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Value("${order.pay.timeout}")
    private Integer payTimeOut;

    /**
     * 发送核销短信
     * @param orderItem
     * @param quantity
     * @return
     */
    public void sendConsumeSms(OrderItem orderItem, int quantity) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("name", orderItem.getPro_sub_name());
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("num", String.valueOf(orderItem.getQuantity()));
        sendSmsParams.put("xiaofeishuliang", String.valueOf(quantity));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.ORDER_CONSUME, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送核销短信失败:" + result.getError());
        }
    }

    /**
     * 发送反核销短信
     * @param orderItem
     * @param consumeQuantity
     * @param reQuantity
     * @return
     */
    public void sendReConsumeSms(OrderItem orderItem, int consumeQuantity, int reQuantity) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("dingdanhao", String.valueOf(orderItem.getNumber()));
        sendSmsParams.put("shuliang1", String.valueOf(consumeQuantity));
        sendSmsParams.put("shuliang2", String.valueOf(reQuantity));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.CONSUME_SUCCESS, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送反核销短信失败:" + result.getError());
        }
    }

    /**
     * 发送订单退票失败短信
     * @param orderItem
     * @return
     */
    public void sendRefundFailSms(OrderItem orderItem, RefundOrder refundOrder) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("productname", orderItem.getPro_sub_name());
        sendSmsParams.put("countnum", String.valueOf(orderItem.getQuantity() * orderItem.getDays()));
        sendSmsParams.put("dingdanhao", String.valueOf(order.getNumber()));
        sendSmsParams.put("num", String.valueOf(refundOrder.getQuantity()));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.MONEY_REFUND_FAIL, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送退票失败短信失败:" + result.getError());
        }
    }

    /**
     * 发送退订成功短信
     * @param refundOrder
     * @return
     */
    public void sendRefundSuccessSms(RefundOrder refundOrder) {
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());

        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("productname", orderItem.getPro_sub_name());
        sendSmsParams.put("countnum", String.valueOf(orderItem.getQuantity() * orderItem.getDays()));
        sendSmsParams.put("dingdanhao", String.valueOf(order.getNumber()));
        sendSmsParams.put("num", String.valueOf(refundOrder.getQuantity()));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.ORDER_REFUND, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送退订成功短信失败:" + result.getError());
        }
    }

    /**
     * 发送退款成功短信
     * @param refundOrder
     * @return
     */
    public void sendRefundMoneySuccessSms(RefundOrder refundOrder) {
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());

        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        // 余额不发短信
        if (order.getPayment_type() == PaymentConstant.PaymentType.BALANCE.intValue()) {
            return;
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("dingdanhao", String.valueOf(order.getNumber()));
        sendSmsParams.put("money", String.valueOf(refundOrder.getMoney() / 100.0));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.MONEY_REFUND, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送退款成功短信失败:" + result.getError());
        }
    }

    /**
     * 发送预定审核短信
     * @param orderItem
     * @return
     */
    public void sendAuditSms(OrderItem orderItem) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        if (orderItem.getSupplier_phone() == null) {
            log.warn("子订单:{}没有供应商短信通知号码,不发送短信", orderItem.getNumber());
            return;
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("dingdanhao", orderItem.getNumber().toString());
        sendSmsParams.put("zichanpinming", orderItem.getPro_sub_name());
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("xingmin", shipping.getName());
        sendSmsParams.put("shuliang", String.valueOf(orderItem.getQuantity()));
        sendSmsParams.put("dianhua", shipping.getPhone());
        Result result = smsService.send(orderItem.getSupplier_phone(), SmsType.Order.SUPPLIER_ORDER_WARN, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送预定审核短信失败:" + result.getError());
        }
    }

    /**
     * 发送预定审核失败短信
     * @param orderItem
     * @return
     */
    public void sendAuditRefuseSms(OrderItem orderItem) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("chanpinmingcheng", orderItem.getPro_sub_name());
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("shuliang", String.valueOf(orderItem.getQuantity()));
        // TODO: 2016/11/8 找不到电话号码
        sendSmsParams.put("dianhuahaoma", "123456789");
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.SUPPLIER_ORDER_REFUSE, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送预定审核失败短信失败:" + result.getError());
        }
    }

    /**
     * 发送审核通过待支付短信(多个子订单不适用)
     * @param orderItem
     * @return
     */
    public void sendAuditSuccess(OrderItem orderItem) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Date date = DateUtils.addMinutes(order.getAudit_time(), payTimeOut);
        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("chanpinmingcheng", orderItem.getPro_sub_name());
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("shuliang", String.valueOf(orderItem.getQuantity()));
        sendSmsParams.put("buydate", DateFormatUtils.parseDateToDatetimeString(date));
        sendSmsParams.put("money", String.valueOf(order.getPay_amount() / 100.0));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.SUPPLIER_PAY, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送审核通过待支付短信失败:" + result.getError());
        }
    }

    /**
     * 发送预定支付成功短信
     * @param orderItem
     */
    public void sendPaymentSuccess(OrderItem orderItem) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("chanpinmingcheng", orderItem.getPro_sub_name());
        sendSmsParams.put("date", DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
        sendSmsParams.put("num", String.valueOf(orderItem.getQuantity()));
        Result result = smsService.send(shipping.getPhone(), SmsType.Prod.PRODUCT_ORDER_OTA, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送预定支付成功短信失败:" + result.getError());
        }
    }

    /**
     * 发送酒店出票短信
     * @param orderItem
     */
    public void sendHotelSendTicket(OrderItem orderItem) {
        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Map<String, String> sendSmsParams = new HashMap<>();
        sendSmsParams.put("productname", orderItem.getPro_sub_name());
        sendSmsParams.put("number", String.valueOf(order.getNumber()));
        sendSmsParams.put("indate", DateFormatUtils.converToStringDate(orderItem.getStart()));
        sendSmsParams.put("outdate", DateFormatUtils.converToStringDate(orderItem.getEnd()));
        sendSmsParams.put("count", String.valueOf(orderItem.getQuantity()));
        Result result = smsService.send(shipping.getPhone(), SmsType.Order.HOTEL_TICKET, order.getPayee_ep_id(), sendSmsParams);//发送短信
        if (!result.isSuccess()) {
            throw new ApiException("发送酒店出票短信失败:" + result.getError());
        }
    }
}
