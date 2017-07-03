package com.all580.base.manager.voucherplatform;

import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-26.
 */
@Component
public class SupplyValidateManager {
    public Map<String[], ValidRule[]> createValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", // 用户名
                "phone", // 密码
                "address",
                "region",
                "description",
                "signTye"
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"signTye"}, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> updateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "name",
                "phone",
                "address",
                "region",
                "description",
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"id"}, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> updateConfValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "conf",
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"id"}, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> updateTicketSysValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "ticketsysId",
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"id", "ticketsysId"}, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> setProdValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "code",
                "name",
        }, new ValidRule[]{new ValidRule.NotNull()});
        return rules;
    }
}
