package com.all580.base.manager;

import com.all580.order.api.OrderConstant;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单参数验证管理器
 * @date 2016/10/11 11:04
 */
@Component
public class OrderValidateManager {
    /**
     * 生成创建订单验证
     * @return
     */
    public Map<String[], ValidRule[]> createValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "shipping.name", // 订单联系人姓名
                "shipping.phone", // 订单联系人手机号码
                "items.visitor.name", // 订单游客姓名
                "items.visitor.phone", // 订单游客手机号码
                //"items.visitor.sid", // 订单游客身份证号码
                "items.product_sub_code", // 订单子产品CODE
                "items.start", // 计划开始时间
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "operator_id", // 订票用户ID
                //"sale_amount", // 销售金额
                "from" // 来源 0-平台下单 1-接口下单
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "items.product_sub_code", // 订单子产品CODE
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "operator_id" // 订票用户ID
        }, new ValidRule[]{new ValidRule.Digits()});

        rules.put(new String[]{
                "items.days", // 天数：景点固定1
                "items.quantity" // 订票数量
        }, new ValidRule[]{new ValidRule.Digits(1L, 10000L)});

        // 校验身份证
        rules.put(new String[]{
                "items.visitor.sid" // 订单游客身份证号码
        }, new ValidRule[]{new ValidRule.IdCard()});

        // 校验手机号码
        rules.put(new String[]{
                "shipping.phone", // 订单联系人手机号码
                "items.visitor.phone" // 订单游客手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        // 校验日期
        rules.put(new String[]{
                "items.start" // 计划开始时间
        }, new ValidRule[]{new ValidRule.Date()});

        rules.put(new String[]{"items.send_msg"}, new ValidRule[]{new ValidRule.Boolean()});

        return rules;
    }

    /**
     * 生成创建订单验证
     * @return
     */
    public Map<String[], ValidRule[]> createHotelValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "shipping.name", // 订单联系人姓名
                "shipping.phone", // 订单联系人手机号码
                "items.product_sub_code", // 订单子产品CODE
                "items.start", // 计划开始时间
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "operator_id", // 订票用户ID
                "from" // 来源 0-平台下单 1-接口下单
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "items.product_sub_code", // 订单子产品CODE
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "operator_id" // 订票用户ID
        }, new ValidRule[]{new ValidRule.Digits()});

        rules.put(new String[]{
                "items.days", // 天数：景点固定1
                "items.quantity" // 订票数量
        }, new ValidRule[]{new ValidRule.Digits(1L, 10000L)});

        // 校验手机号码
        rules.put(new String[]{
                "shipping.phone" // 订单联系人手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        // 校验日期
        rules.put(new String[]{
                "items.start" // 计划开始时间
        }, new ValidRule[]{new ValidRule.Date()});

        return rules;
    }

    /**
     * 生成酒店团队创建订单验证
     * @return
     */
    public Map<String[], ValidRule[]> createHotelGroupValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "items.product_sub_code", // 订单子产品CODE
                "items.start", // 计划开始时间
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "items.group_id", // 团队ID
                "ep_id", // 订票企业ID
                "operator_id", // 订票用户ID
                "from" // 来源 0-平台下单 1-接口下单
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "items.product_sub_code", // 订单子产品CODE
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "items.group_id", // 团队ID
                "ep_id", // 订票企业ID
                "operator_id" // 订票用户ID
        }, new ValidRule[]{new ValidRule.Digits()});

        rules.put(new String[]{
                "items.days", // 天数：景点固定1
                "items.quantity" // 订票数量
        }, new ValidRule[]{new ValidRule.Digits(1L, 10000L)});

        // 校验手机号码
        rules.put(new String[]{
                "items.phone" // 订单联系人手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        // 校验日期
        rules.put(new String[]{
                "items.start" // 计划开始时间
        }, new ValidRule[]{new ValidRule.Date()});

        return rules;
    }

    /**
     * 生成创建线路订单验证
     * @return
     */
    public Map<String[], ValidRule[]> createLineValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "shipping.name", // 订单联系人姓名
                "shipping.phone", // 订单联系人手机号码
                "items.visitor.name", // 订单游客姓名
                "items.visitor.phone", // 订单游客手机号码
                "items.product_sub_code", // 订单子产品CODE
                "items.start", // 计划开始时间
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "from" // 来源 0-平台下单 1-接口下单
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "items.product_sub_code", // 订单子产品CODE
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "operator_id" // 订票用户ID
        }, new ValidRule[]{new ValidRule.Digits()});

        rules.put(new String[]{
                "items.days", // 天数：景点固定1
                "items.quantity" // 订票数量
        }, new ValidRule[]{new ValidRule.Digits(1L, 10000L)});

        // 校验身份证
        rules.put(new String[]{
                "items.visitor.sid" // 订单游客身份证号码
        }, new ValidRule[]{new ValidRule.IdCard()});

        // 校验性别
        rules.put(new String[]{
                "items.visitor.sex"
        }, new ValidRule[]{new ValidRule.Digits(new Long[]{
                (long) OrderConstant.SexType.NONE,
                (long) OrderConstant.SexType.MAN,
                (long) OrderConstant.SexType.FEMALE
        })});

        // 校验手机号码
        rules.put(new String[]{
                "shipping.phone", // 订单联系人手机号码
                "items.visitor.phone" // 订单游客手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        // 校验日期
        rules.put(new String[]{
                "items.start" // 计划开始时间
        }, new ValidRule[]{new ValidRule.Date()});

        return rules;
    }

    /**
     * 生成创建团队订单验证
     * @return
     */
    public Map<String[], ValidRule[]> createGroupValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "items.group_id", // 团ID
                "items.product_sub_code", // 订单子产品ID
                "items.start", // 计划开始时间
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "operator_id", // 订票用户ID
                //"sale_amount", // 销售金额
                "from" // 来源 0-平台下单 1-接口下单
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "items.product_sub_code", // 订单子产品ID
                "items.days", // 天数：景点固定1
                "items.quantity", // 订票数量
                "ep_id", // 订票企业ID
                "operator_id" // 订票用户ID
        }, new ValidRule[]{new ValidRule.Digits()});

        // 校验日期
        rules.put(new String[]{
                "items.start" // 计划开始时间
        }, new ValidRule[]{new ValidRule.Date()});

        return rules;
    }

    /**
     * 生成订单审核验证
     * @return
     */
    public Map<String[], ValidRule[]> auditValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "status" // 通过/不通过
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Boolean()});

        rules.put(new String[]{
                "order_item_id" // 子订单ID
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        return rules;
    }

    /**
     * 生成订单支付验证
     * @return
     */
    public Map<String[], ValidRule[]> paymentValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "order_sn", // 订单编号(流水)
                "pay_type" // 支付类型
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        return rules;
    }

    /**
     * 订单退订申请验证
     * @return
     */
    public Map<String[], ValidRule[]> refundApplyValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "order_item_sn", // 订单编号(流水)
                "days.quantity", // 退票数量
                "days.visitors.quantity", // 退票数量
                "days.visitors.id", // 游客ID
                "quantity", // 退票数量
                "apply_from" // 来源 供应侧/销售侧
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        rules.put(new String[]{
                "days.quantity", // 退票数量
                "days.visitors.quantity", // 退票数量
                "quantity" // 退票数量
        }, new ValidRule[]{new ValidRule.Digits(1L, 100000L)});

        rules.put(new String[]{"apply_from"}, new ValidRule[]{
                new ValidRule.Digits(new Long[]{
                        Long.valueOf(ProductConstants.RefundEqType.SELLER), Long.valueOf(ProductConstants.RefundEqType.PROVIDER)
                })});

        rules.put(new String[]{
                "days.day" // 日期
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Date()});

        rules.put(new String[]{"cause"}, new ValidRule[]{new ValidRule.NotNull()});
        return rules;
    }

    /**
     * 团队订单退订申请验证
     * @return
     */
    public Map<String[], ValidRule[]> refundApplyForGroupValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "order_item_sn", // 订单编号(流水)
                "apply_from" // 来源 供应侧/销售侧
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        rules.put(new String[]{"apply_from"}, new ValidRule[]{
                new ValidRule.Digits(new Long[]{
                        Long.valueOf(ProductConstants.RefundEqType.SELLER), Long.valueOf(ProductConstants.RefundEqType.PROVIDER)
                })});

        rules.put(new String[]{"cause"}, new ValidRule[]{new ValidRule.NotNull()});
        return rules;
    }

    /**
     * 酒店订单退订申请验证
     * @return
     */
    public Map<String[], ValidRule[]> refundApplyForHotelValidate() {
        return refundApplyForGroupValidate();
    }

    /**
     * OTA订单退订申请验证
     * @return
     */
    public Map<String[], ValidRule[]> refundApplyForOtaValidate() {
        return refundApplyForGroupValidate();
    }

    /**
     * 订单退订审核验证
     * @return
     */
    public Map<String[], ValidRule[]> refundAuditValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "refund_sn", // 订单编号(流水)
                "operator_id" // 操作人ID
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        rules.put(new String[]{
                "status" // 通过/不通过
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Boolean()});

        rules.put(new String[]{
                "reason" // 原因
        }, new ValidRule[]{new ValidRule.NotNull()});

        return rules;
    }

    /**
     * 订单退订退款审核验证
     * @return
     */
    public Map<String[], ValidRule[]> refundMoneyAuditValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "refund_sn", // 订单编号(流水)
                "operator_id" // 操作人ID
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        return rules;
    }

    /**
     * 订单取消验证
     * @return
     */
    public Map<String[], ValidRule[]> cancelValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "order_sn" // 订单编号(流水)
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        return rules;
    }
    /**
     * 订单支付宝退款验证
     * @return
     */
    public Map<String[], ValidRule[]> refundAlipayValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "refund_sn" // 退订订单编号(流水)
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        return rules;
    }

    /**
     * 订单重新发票验证
     * @return
     */
    public Map<String[], ValidRule[]> resendTicketValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "order_item_sn", // 子订单编号(流水)
                "visitor_id" // 游客ID
        }, new ValidRule[]{new ValidRule.Digits()});

        rules.put(new String[]{
                "phone" // 游客手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});
        rules.put(new String[]{
                "all" // 是否全部重新发票
        }, new ValidRule[]{new ValidRule.Boolean()});
        rules.put(new String[]{
                "order_item_sn" // 子订单编号(流水)
        }, new ValidRule[]{new ValidRule.NotNull()});

        return rules;
    }

    /**
     * 订单重新发票验证
     * @return
     */
    public Map<String[], ValidRule[]> resendTicketForGroupValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "order_item_sn" // 子订单编号(流水)
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        return rules;
    }

    /**
     * 修改团队票据
     * @return
     */
    public Map<String[], ValidRule[]> modifyTicketForGroupValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "order_item_sn" // 子订单编号(流水)
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        rules.put(new String[]{
                "guide_phone",
                "visitors.phone"
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        rules.put(new String[]{
                "guide_sid"
        }, new ValidRule[]{new ValidRule.IdCard()});
        rules.put(new String[]{
                "visitors.card_type" // 证件类型
        }, new ValidRule[]{new ValidRule.Digits(new Long[]{Long.valueOf(OrderConstant.CardType.ID), Long.valueOf(OrderConstant.CardType.OTHER)})});
        return rules;
    }

    /**
     * 平台商订单列表验证
     * @return
     */
    public Map<String[], ValidRule[]> platformOrderListValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "start_time",
                "end_time"
        }, new ValidRule[]{new ValidRule.Date()});

        rules.put(new String[]{
                "phone" // 游客手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        return rules;
    }

    /**
     * 酒店核销验证
     * @return
     */
    public Map<String[], ValidRule[]> consumeHotelValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "order_item_sn", // 订单编号(流水)
                "days.quantity" // 核销数量
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        rules.put(new String[]{
                "days.quantity" // 核销数量
        }, new ValidRule[]{new ValidRule.Digits(0L, 100000L)});

        rules.put(new String[]{
                "days.day" // 日期
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Date()});

        return rules;
    }

    /**
     * 酒店核销验证
     * @return
     */
    public Map<String[], ValidRule[]> consumeLineValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "order_item_sn" // 订单编号(流水)
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        return rules;
    }
}
