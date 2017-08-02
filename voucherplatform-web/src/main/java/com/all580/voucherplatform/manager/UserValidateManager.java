package com.all580.voucherplatform.manager;

import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/12.
 */

@Component
public class UserValidateManager {
    public Map<String[], ValidRule[]> loginValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "userName", // 用户名
                "passWord" // 密码
        }, new ValidRule[]{new ValidRule.NotNull()});
        return rules;
    }

    public Map<String[], ValidRule[]> updateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();

        rules.put(new String[]{
                "oldPassword", // 密码
                "passWord", // 密码
        }, new ValidRule[]{new ValidRule.NotNull()});

        return rules;
    }
}
