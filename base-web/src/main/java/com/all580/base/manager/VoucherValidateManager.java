package com.all580.base.manager;

import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 凭证接口验证管理器
 * @date 2016/10/19 19:54
 */
@Component
public class VoucherValidateManager {
    /**
     * 生成添加凭证验证
     * @return
     */
    public Map<String[], ValidRule[]> addValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", // 凭证名称
                "link" // 凭证接口地址
        }, new ValidRule[]{new ValidRule.NotNull()});

        return rules;
    }
    /**
     * 生成修改凭证验证
     * @return
     */
    public Map<String[], ValidRule[]> updateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id", // 凭证ID
                "name", // 凭证名称
                "link" // 凭证接口地址
        }, new ValidRule[]{new ValidRule.NotNull()});

        return rules;
    }

    /**
     * 关联凭证商户验证
     * @return
     */
    public Map<String[], ValidRule[]> merchantValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id", // 企业ID
                "voucher_id", // 凭证平台ID
                "access_id", // 凭证商户ID
                "access_key" // 凭证商户KEY
        }, new ValidRule[]{new ValidRule.NotNull()});

        rules.put(new String[]{"voucher_id", "ep_id"}, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }
}
