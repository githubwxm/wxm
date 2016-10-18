package com.all580.base.manager;

import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/11 0011.
 *
 */
@Component
public class PlatfromValidateManager {


    public Map<String[], ValidRule[]> generateCreateEpValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", // 企业中文名
                "code", // '企业组织机构代码',
                "logo_pic", // '企业组织机构代码',
                "license", // 营业证编号
                "linkman", // 企业联系人姓名
                "link_phone", // 企业联系人电话
                "province", // 企业省
                "city", // 市
                "area", // 区
                "address", // 详细地址
        }, new ValidRule[]{new ValidRule.NotNull()});

//        // 校验整数
        rules.put(new String[]{
                "province", // 企业省
                "city", // 市
                "area", // 区
        }, new ValidRule[]{new ValidRule.Digits()});
        rules.put(new String[]{
                "link_phone", // 订单联系人手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});
        return rules;
    }
    public Map<String[], ValidRule[]> generateCreateStatusValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id" // 企业id
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> generateCreateDownUpValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id" // 企业id
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
