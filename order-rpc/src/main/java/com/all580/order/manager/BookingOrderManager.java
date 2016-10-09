package com.all580.order.manager;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.VisitorMapper;
import com.all580.order.entity.*;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 预定
 * @date 2016/10/8 14:06
 */
@Component
@Slf4j
public class BookingOrderManager extends BaseOrderManager {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    /**
     * 生成创建订单验证
     * @return
     */
    public Map<String[], ValidRule[]> generateCreateOrderValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "shipping.name", // 订单联系人姓名
                "shipping.mobile", // 订单联系人手机号码
                "items.visitor.name", // 订单游客姓名
                "items.visitor.mobile", // 订单游客手机号码
                "items.visitor.sid", // 订单游客身份证号码
                "items.product_sub_id", // 订单子产品ID
                "items.start", // 计划开始时间
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "user_id", // 订票用户ID
                "user_name", // 订票用户名称
                "sale_amount", // 销售金额
                "from" // 来源 0-平台下单 1-接口下单
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "items.product_sub_id", // 订单子产品ID
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "user_id" // 订票用户ID
        }, new ValidRule[]{new ValidRule.Digits()});

        // 校验身份证
        rules.put(new String[]{
                "items.visitor.sid" // 订单游客身份证号码
        }, new ValidRule[]{new ValidRule.IdCard()});

        // 校验手机号码
        rules.put(new String[]{
                "shipping.mobile", // 订单联系人手机号码
                "items.visitor.mobile" // 订单游客手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        return rules;
    }

    /**
     * 验证游客信息
     * @param visitors 游客信息
     * @param productSubId 子产品ID
     * @param bookingDate 预定时间
     * @param maxCount 最大次数
     * @param maxQuantity 最大张数
     * @return
     */
    public Result validateVisitor(List<Map> visitors, Integer productSubId, Date bookingDate, Integer maxCount, Integer maxQuantity) {
        for (Map visitorMap : visitors) {
            String sid = (String) visitorMap.get("sid");
            if (!canOrderByCount(productSubId, sid, bookingDate, maxCount)) {
                return new Result<>(false, Result.PARAMS_ERROR, "身份证:" + sid + "超出该产品当天最大订单数");
            }
            Integer qty = (Integer) visitorMap.get("quantity");
            if (!canOrderByQuantity(productSubId, sid, bookingDate, maxQuantity, qty)) {
                return new Result<>(false, Result.PARAMS_ERROR,
                        "身份证:" + sid + "超出该产品当天最大购票数,现已定" + qty + "张,最大购票" + maxQuantity + "张");
            }
        }
        return new Result(true);
    }

    /**
     * 判断身份证订单次数是否超过最大次数
     * @param productSubId 子产品ID
     * @param sid 身份证
     * @param date 预定日期
     * @param max 最大次数
     * @return
     */
    private boolean canOrderByCount(Integer productSubId, String sid, Date date, Integer max) {
        if (max == null || max <= 0)
            return true;
        Date start = DateFormatUtils.dayBegin(date);
        Date end = DateFormatUtils.dayEnd(date);
        int count = orderItemMapper.countBySidAndProductForDate(productSubId, sid, start, end);
        return count < max;
    }

    /**
     * 判断身份证订票张数是否超过最大张数
     * @param productSubId 子产品ID
     * @param sid 身份证
     * @param date 预定日期
     * @param max 最大张数
     * @param cur 当前需要订购张数
     * @return
     */
    private boolean canOrderByQuantity(Integer productSubId, String sid, Date date, Integer max, Integer cur) {
        if (max == null || max <= 0)
            return true;
        Date start = DateFormatUtils.dayBegin(date);
        Date end = DateFormatUtils.dayEnd(date);
        int quantity = visitorMapper.quantityBySidAndProductForDate(productSubId, sid, start, end);
        return quantity + cur <= max;
    }

    /**
     * 创建订单生成订单数据
     * @param buyEpId 销售企业ID
     * @param userId 销售用户ID
     * @param userName 销售用户名称
     * @param from 来源
     * @return
     */
    public Order generateOrder(Integer buyEpId, Integer userId, String userName, Integer from) {
        Order order = new Order();
        order.setNumber(UUIDGenerator.generateUUID());
        order.setStatus(OrderConstant.OrderStatus.PAY_WAIT);
        order.setBuyEpId(buyEpId);
        order.setBuyOperatorId(userId);
        order.setBuyOperatorName(userName);
        order.setCreateTime(new Date());
        order.setFromType(from);
        return order;
    }

    /**
     * 创建子订单生成子订单数据
     * @param info 产品信息
     * @param salesInfo 产品销售链
     * @param days 天数
     * @param orderId 订单ID
     * @param quantity 张数
     * @return
     */
    public OrderItem generateItem(ProductSalesInfo info, EpSalesInfo salesInfo, int days, int orderId, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setNumber(UUIDGenerator.generateUUID());
        orderItem.setSaleAmount(salesInfo.getPrice() * quantity * days); // 进货价
        orderItem.setDays(days);
        orderItem.setGroupId(0); // 散客为0
        orderItem.setOrderId(orderId);
        orderItem.setProName(info.getProductName());
        orderItem.setProSubName(info.getProductSubName());
        orderItem.setProSubId(info.getProductSubId());
        orderItem.setQuantity(quantity);
        orderItem.setPaymentFlag(info.getPayType());
        orderItem.setStatus(OrderConstant.OrderItemStatus.AUDIT_WAIT);
        return orderItem;
    }

    /**
     * 创建子订单详情
     * @param info 产品信息
     * @param itemId 子订单ID
     * @param day 当天
     * @param quantity 张数
     * @return
     */
    public OrderItemDetail generateDetail(ProductSalesInfo info, int itemId, Date day, int quantity) {
        OrderItemDetail orderItemDetail = new OrderItemDetail();
        orderItemDetail.setDay(day);
        orderItemDetail.setQuantity(quantity);
        orderItemDetail.setCustRefundRule(info.getCustRefundRule()); // 销售方退货规则
        orderItemDetail.setSalerRefundRule(info.getSalerRefundRule()); // 供应方退货规则
        orderItemDetail.setOrderItemId(itemId);
        orderItemDetail.setRefundQuantity(0);
        orderItemDetail.setUsedQuantity(0);
        return orderItemDetail;
    }

    /**
     * 创建子订单游客信息
     * @param v 游客参数
     * @param itemId 子订单ID
     * @return
     */
    public Visitor generateVisitor(Map v, int itemId) {
        Visitor visitor = new Visitor();
        visitor.setRefId(itemId);
        visitor.setName((String) v.get("name"));
        visitor.setPhone((String) v.get("phone"));
        visitor.setSid((String) v.get("sid"));
        visitor.setQuantity((Integer) v.get("quantity"));
        return visitor;
    }

    /**
     * 创建订单联系人
     * @param shippingMap 联系人参数
     * @param orderId 订单ID
     * @return
     */
    public Shipping generateShipping(Map shippingMap, int orderId) {
        Shipping shipping = new Shipping();
        shipping.setId(orderId);
        shipping.setName((String) shippingMap.get("name"));
        shipping.setPhone((String) shippingMap.get("phone"));
        shipping.setSid((String) shippingMap.get("sid"));
        return shipping;
    }
}
