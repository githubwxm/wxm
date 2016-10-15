package com.all580.base.manager;

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
                //"sale_amount", // 销售金额
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
                "visitors.quantity", // 退票数量
                "quantity", // 退票数量
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        rules.put(new String[]{
                "days.day" // 日期
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Date()});

        rules.put(new String[]{
                "visitors.sid" // 身份证
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.IdCard()});

        rules.put(new String[]{"cause"}, new ValidRule[]{new ValidRule.NotNull()});
        return rules;
    }

    /**
     * 订单退订审核验证
     * @return
     */
    public Map<String[], ValidRule[]> refundAuditValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "refund_sn" // 订单编号(流水)
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
}
