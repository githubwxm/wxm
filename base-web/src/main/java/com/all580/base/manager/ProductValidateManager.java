package com.all580.base.manager;

import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProductValidateManager {

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
                "from", // 来源 0-平台下单 1-接口下单
                "remark" // 备注
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
}
