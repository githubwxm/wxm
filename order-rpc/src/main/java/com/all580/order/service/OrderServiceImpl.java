package com.all580.order.service;

import com.all580.order.api.service.OrderService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.Shipping;
import com.framework.common.Result;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务实现
 * @date 2016/9/28 9:23
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result<?> create(Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, generateCreateOrderValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("创建订单参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }

        Integer buyEpId = (Integer) params.get("ep_id");

        // TODO 判断销售商状态是否为已冻结

        // 创建订单
        Order order = insertOrder(BigDecimal.ZERO, BigDecimal.ZERO, buyEpId, (Integer) params.get("user_id"), (String) params.get("user_name"), (Integer) params.get("from"));

        // 获取产品订单
        List<Map> items = (List<Map>) params.get("items");
        for (Map item : items) {
            Integer productSubId = (Integer) item.get("product_sub_id");
            Integer quantity = (Integer) item.get("quantity");
            Integer days = (Integer) item.get("days");
            Date bookingDate = null; //预定日期

            // TODO 调用商品接口获取商品信息

            // TODO 判断供应商状态是否为已冻结

            Map visitors = (Map) item.get("visitor");
            // TODO 判断游客信息

            // TODO 锁定库存

            // 创建子订单
            OrderItem orderItem = new OrderItem();
            orderItem.setNumber(0L);
            orderItem.setSaleAmount(BigDecimal.ZERO); // 进货价
            orderItem.setDays(days);
            orderItem.setGroupId(0); // 散客为0
            orderItem.setOrderId(order.getId());
            orderItem.setProName("");
            orderItem.setProSubId(productSubId);
            orderItem.setQuantity(quantity);
            orderItem.setStatus(1);

            // 创建子订单详情
            for (Integer i = 0; i < days; i++) {
                OrderItemDetail orderItemDetail = new OrderItemDetail();
                orderItemDetail.setDay(DateUtils.addDays(bookingDate, i));
                orderItemDetail.setQuantity(quantity);
                orderItemDetail.setCustRefundRule(1); // 销售方退货规则
                orderItemDetail.setSalerRefundRule(1); // 供应方退货规则
                orderItemDetail.setOrderItemId(orderItem.getId());
                orderItemDetail.setRefundQuantity(0);
                orderItemDetail.setUsedQuantity(0);
            }
        }

        // 创建订单联系人


        Map shipping = (Map) params.get("shipping");
        String name = (String) shipping.get("name");

        orderMapper.selectByPrimaryKey(0);
        return null;
    }

    @Override
    public Result<?> audit(Map params) {
        return null;
    }

    @Override
    public Result<?> payment(Map params) {
        return null;
    }

    @Override
    public Result<?> resendTicket(Map params) {
        return null;
    }

    @Override
    public Result<?> cancel(Map params) {
        return null;
    }

    @Override
    public Result<?> refundApply(Map params) {
        return null;
    }

    @Override
    public Result<?> refundAudit(Map params) {
        return null;
    }

    /**
     * 生成创建订单验证
     * @return
     */
    private Map<String[], ValidRule[]> generateCreateOrderValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "shipping.name", // 订单联系人姓名
                "shipping.mobile", // 订单联系人手机号码
                "items.visitor.name", // 订单游客姓名
                "items.visitor.mobile", // 订单游客手机号码
                "items.visitor.sid", // 订单游客身份证号码
                "items.product_sub_id", // 订单子产品ID
                "items.plan_id", // 计划ID
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
                "items.plan_id", // 计划ID
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
     * 创建订单生成订单数据
     * @param payAmount 代收方式: 销售门店价; 反之: 进货价
     * @param saleAmount 进货价
     * @param buyEpId 销售企业ID
     * @param userId 销售用户ID
     * @param userName 销售用户名称
     * @param from 来源
     * @return
     */
    private Order insertOrder(BigDecimal payAmount, BigDecimal saleAmount,
                              Integer buyEpId, Integer userId, String userName, Integer from) {
        Order order = new Order();
        order.setNumber(0L);
        order.setPayAmount(payAmount); // 支付金额:产品信息获取
        order.setStatus(1); // 待审核
        order.setBuyEpId(buyEpId);
        order.setBuyOperatorId(userId);
        order.setBuyOperatorName(userName);
        order.setCreateTime(new Date());
        order.setSaleAmount(saleAmount);
        order.setFromType(from);
        orderMapper.insert(order);
        return order;
    }
}
