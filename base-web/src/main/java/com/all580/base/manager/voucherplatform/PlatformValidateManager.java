package com.all580.base.manager.voucherplatform;

import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-26.
 */
@Component
public class PlatformValidateManager {
    public Map<String[], ValidRule[]> createValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name",
                "description",
                "signType"
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"signType"}, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> updateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "name",
                "description",
                "signType"
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"id", "signType"}, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
