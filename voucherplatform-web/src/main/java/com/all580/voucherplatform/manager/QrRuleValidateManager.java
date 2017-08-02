package com.all580.voucherplatform.manager;

import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/18.
 */

@Component
public class QrRuleValidateManager {
    public Map<String[], ValidRule[]> createValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", // 用户名
                "len", // 密码
                "errorRate",
                "size",
                "foreColor",
                "supply_id"
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"len", "size", "supplyProd_id"}, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> updateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "name",
                "len",
                "errorRate",
                "size",
                "foreColor"
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"id", "len", "size"}, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }


}
