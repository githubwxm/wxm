package com.all580.voucherplatform.manager;

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

    public Map<String[], ValidRule[]> createRoleValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "supplyId",
                "platformId",
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> createProdTypeValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name"
        }, new ValidRule[]{new ValidRule.NotNull()});

        rules.put(new String[]{
                "id"
        }, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }
}
