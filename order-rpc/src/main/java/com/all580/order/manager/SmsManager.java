package com.all580.order.manager;

import com.all580.notice.api.conf.SmsType;
import com.all580.notice.api.service.SmsService;
import com.all580.order.dao.*;
import com.all580.order.entity.*;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Autowired
    private MaSendResponseMapper maSendResponseMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;

    @Value("${order.pay.timeout}")
    private Integer payTimeOut;
    private Pattern pattern = Pattern.compile("\\$\\{\\w*}");

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
    public void sendRefundFailSms(OrderItem orderItem, RefundOrder refundOrder, String reject) {
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
        sendSmsParams.put("dingdanhao", String.valueOf(order.getNumber()));
        sendSmsParams.put("yuanyin", reject);
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
        sendSmsParams.put("zichanpinming", orderItem.getPro_name() + "-" + orderItem.getPro_sub_name());
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
        sendSmsParams.put("dianhuahaoma", orderItem.getSupplier_phone());
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
        sendSmsParams.put("chanpinmingcheng", orderItem.getPro_name() + "-" + orderItem.getPro_sub_name());
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
        sendHotelSendTicket(orderItem, null);
    }
    /**
     * 发送酒店出票短信
     * @param orderItem
     */
    public void sendHotelSendTicket(OrderItem orderItem, String phone) {
        Assert.notNull(orderItem.getVoucher_template(), "订单没有凭证短信模板,短信发送失败");

        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Map<String, String> sendSmsParams = parseParams(orderItem.getVoucher_msg(), order, orderItem, null, orderItem.getQuantity());
        Result result = smsService.sendByTemplate(orderItem.getSupplier_core_ep_id(), orderItem.getVoucher_template(), sendSmsParams, phone == null ? shipping.getPhone() : phone);
        if (!result.isSuccess()) {
            throw new ApiException("发送酒店出票短信失败:" + result.getError());
        }
    }

    /**
     * 发送线路出票短信
     * @param orderItem
     */
    public void sendLineSendTicket(OrderItem orderItem) {
        sendLineSendTicket(orderItem, null);
    }
    /**
     * 发送线路出票短信
     * @param orderItem
     */
    public void sendLineSendTicket(OrderItem orderItem, String phone) {
        Assert.notNull(orderItem.getVoucher_template(), "订单没有凭证短信模板,短信发送失败");

        Shipping shipping = shippingMapper.selectByOrder(orderItem.getOrder_id());
        if (shipping == null) {
            throw new ApiException("订单联系人不存在");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        Map<String, String> sendSmsParams = parseParams(orderItem.getVoucher_msg(), order, orderItem, null, orderItem.getQuantity());
        Result result = smsService.sendByTemplate(orderItem.getSupplier_core_ep_id(), orderItem.getVoucher_template(), sendSmsParams, phone == null ? shipping.getPhone() : phone);
        if (!result.isSuccess()) {
            throw new ApiException("发送线路出票短信失败:" + result.getError());
        }
    }

    /**
     * 发送凭证短信
     * @param orderItem
     */
    public void sendVoucher(OrderItem orderItem) {
        sendVoucher(orderItem, null);
    }

    /**
     * 发送凭证短信
     * @param orderItem
     */
    public void sendVoucher(OrderItem orderItem, MaSendResponse response) {
        Assert.notNull(orderItem.getVoucher_template(), "订单没有凭证短信模板,短信发送失败");

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        List<Visitor> visitors = visitorMapper.selectByOrderItem(orderItem.getId());
        if (response == null) {
            List<MaSendResponse> maSendResponses = maSendResponseMapper.selectByOrderItemId(orderItem.getId());
            for (MaSendResponse maSendResponse : maSendResponses) {
                Visitor visitor = getVisitor(maSendResponse.getVisitor_id(), visitors);
                sendVoucher(order, orderItem, maSendResponse, visitor);
            }
        } else {
            Visitor visitor = getVisitor(response.getVisitor_id(), visitors);
            sendVoucher(order, orderItem, response, visitor);
        }
    }

    public void sendVoucherMsg(OrderItem orderItem) {
        switch (orderItem.getPro_type()) {
            case ProductConstants.ProductType.HOTEL:
                sendHotelSendTicket(orderItem);
                break;
            case ProductConstants.ProductType.ITINERARY:
                sendLineSendTicket(orderItem);
                break;
            case ProductConstants.ProductType.SCENERY:
                sendVoucher(orderItem);
                break;
        }
    }

    private void sendVoucher(Order order, OrderItem orderItem, MaSendResponse maSendResponse, Visitor visitor) {
        if (visitor == null) {
            log.warn("发送凭证短信:游客:{},手机号码:{}失败,游客不存在", maSendResponse.getVisitor_id(), maSendResponse.getPhone());
            return;
        }

        Map<String, String> sendSmsParams = parseParams(orderItem.getVoucher_msg(), order, orderItem, maSendResponse, visitor.getQuantity());
        Result result = smsService.sendByTemplate(orderItem.getSupplier_core_ep_id(), orderItem.getVoucher_template(), sendSmsParams, visitor.getPhone());
        if (!result.isSuccess()) {
            throw new ApiException(String.format("发送凭证短信:游客:%d,手机号码:%s失败:%s", maSendResponse.getVisitor_id(), maSendResponse.getPhone(), result.getError()));
        }
    }

    private Visitor getVisitor(int id, List<Visitor> visitors) {
        for (Visitor visitor : visitors) {
            if (visitor.getId() == id) {
                return visitor;
            }
        }
        return null;
    }

    public Map<String, String> parseParams(String content, Order order, OrderItem orderItem, MaSendResponse maSendResponse, int quantity) {
        Map<String, String> map = new HashMap<>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String key = matcher.group();
            String val = key.substring(key.indexOf("${") + 2, key.length() - 1);
            switch (key) {
                case "${product}":
                    map.put(val, orderItem.getPro_name() + "-" + orderItem.getPro_sub_name());
                    break;
                case "${quantity}":
                    map.put(val, String.valueOf(quantity));
                    break;
                case "${amount}":
                    map.put(val, String.valueOf(order.getPay_amount()));
                    break;
                case "${booking}":
                    map.put(val, DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
                    break;
                case "${number}":
                    map.put(val, String.valueOf(order.getNumber()));
                    break;
                case "${create}":
                    map.put(val, DateFormatUtils.parseDateToDatetimeString(order.getCreate_time()));
                    break;
                case "${shipping}":
                    Shipping shipping = shippingMapper.selectByOrder(order.getId());
                    map.put(val, shipping.getName());
                    break;
                case "${check_in}":
                    map.put(val, DateFormatUtils.parseDateToDatetimeString(orderItem.getStart()));
                    break;
                case "${check_out}":
                    map.put(val, DateFormatUtils.parseDateToDatetimeString(orderItem.getEnd()));
                    break;
                case "${voucher}":
                    Assert.notNull(maSendResponse, "该产品类型不支持该参数:" + key);
                    map.put(val, maSendResponse.getVoucher_value());
                    break;
                case "${qr}":
                    Assert.notNull(maSendResponse, "该产品类型不支持该参数:" + key);
                    map.put(val, maSendResponse.getImage_url().replace("http://m8e.cn/", ""));
                    break;
                case "${expiry}":
                    Date expiryDate = orderItemDetailMapper.selectByItemId(orderItem.getId()).get(0).getExpiry_date();
                    map.put(val, DateFormatUtils.parseDateToDatetimeString(expiryDate));
                    break;
            }
        }
        return map;
    }
}
