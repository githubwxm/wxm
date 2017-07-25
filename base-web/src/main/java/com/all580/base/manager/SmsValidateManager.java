package com.all580.base.manager;

import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description:
 * @date 17-7-6 下午3:21
 */
@Component
public class SmsValidateManager {
    /**
     * 创建主产品验证
     * @return
     */
    public Map<String[], ValidRule[]> addValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id",
                "url",
                "app_id",
                "app_pwd",
                "ep_sign"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id"
        }, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }

    public Map<String[], ValidRule[]> updateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "ep_id",
                "app_id",
                "app_pwd",
                "ep_sign"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id"
        }, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }

    public Map<String[], ValidRule[]> addTemplateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "content",
                "ep_id",
                "sms_type",
                "channel_type",
                "out_sms_tpl_id"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id",
                "sms_type",
                "channel_type"
        }, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }

    public Map<String[], ValidRule[]> updateTemplateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "out_sms_tpl_id"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id"
        }, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }
}
