package com.all580.base.manager.voucherplatform;

import com.framework.common.validate.ValidRule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/18.
 */
public class QrRuleManage {
    public Map<String[], ValidRule[]> createValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "userName", // 用户名
                "passWord" // 密码
        }, new ValidRule[]{new ValidRule.NotNull()});
        return rules;
    }
}
