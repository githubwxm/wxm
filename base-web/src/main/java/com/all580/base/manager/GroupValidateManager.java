package com.all580.base.manager;

import com.all580.order.api.OrderConstant;
import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 团队信息验证管理器
 * @date 2016/12/1 17:35
 */
@Component
public class GroupValidateManager {

    /**
     * 添加团队验证
     * @return
     */
    public Map<String[], ValidRule[]> addGroupValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "number", // 团号
                "start_date", // 出游日期
                "travel_name", // 旅行社名称
                "province", // 省
                "city", // 市
                "area", // 区
                "guide_name", // 导游姓名
                "guide_phone", // 导游手机号
                "guide_sid", // 导游身份证
                "ep_id", // 企业ID
                "operator_id", // 用户ID
                "operator_name" // 用户名称
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "number", // 团号
                "province", // 省
                "city", // 市
                "area", // 区
                "ep_id", // 企业ID
                "operator_id" // 用户ID
        }, new ValidRule[]{new ValidRule.Digits()});

        // 校验身份证
        rules.put(new String[]{
                "guide_sid" // 导游身份证
        }, new ValidRule[]{new ValidRule.IdCard()});

        // 校验手机号码
        rules.put(new String[]{
                "guide_phone" // 导游手机号
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        // 校验日期
        rules.put(new String[]{
                "start_date" // 出游日期
        }, new ValidRule[]{new ValidRule.Date("yyyy-MM-dd")});

        return rules;
    }

    /**
     * 修改团队验证
     * @return
     */
    public Map<String[], ValidRule[]> updateGroupValidate() {
        Map<String[], ValidRule[]> rules = addGroupValidate();
        rules.put(new String[]{"group_id"}, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});
        return rules;
    }

    /**
     * 删除团队验证
     * @return
     */
    public Map<String[], ValidRule[]> deleteGroupValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{"group_id"}, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});
        return rules;
    }

    /**
     * 添加导游验证
     * @return
     */
    public Map<String[], ValidRule[]> addGuideValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", // 姓名
                "phone", // 手机号
                "sid", // 身份证
                "ep_id", // 企业ID
                "operator_id", // 用户ID
                "operator_name" // 用户名称
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id", // 企业ID
                "operator_id" // 用户ID
        }, new ValidRule[]{new ValidRule.Digits()});

        // 校验身份证
        rules.put(new String[]{
                "sid" // 导游身份证
        }, new ValidRule[]{new ValidRule.IdCard()});

        // 校验手机号码
        rules.put(new String[]{
                "phone" // 导游手机号
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        return rules;
    }

    /**
     * 修改导游验证
     * @return
     */
    public Map<String[], ValidRule[]> updateGuideValidate() {
        Map<String[], ValidRule[]> rules = addGuideValidate();
        rules.put(new String[]{"guide_id"}, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});
        return rules;
    }

    /**
     * 删除导游验证
     * @return
     */
    public Map<String[], ValidRule[]> deleteGuideValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{"guide_id"}, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});
        return rules;
    }

    /**
     * 新增团队成员验证
     * @return
     */
    public Map<String[], ValidRule[]> addGroupMemberValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "group_id", // 团队ID
                "member.name", // 姓名
                "member.card_type", // 证件类型
                "member.card", // 证件号码
                "member.phone", // 手机号
                "member.adult", // 是否成人
                "ep_id", // 企业ID
                "operator_id", // 用户ID
                "operator_name" // 用户名称
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "group_id", // 团队ID
                "ep_id", // 企业ID
                "operator_id" // 用户ID
        }, new ValidRule[]{new ValidRule.Digits()});
        rules.put(new String[]{
                "member.card_type" // 证件类型
        }, new ValidRule[]{new ValidRule.Digits(new Long[]{Long.valueOf(OrderConstant.CardType.ID), Long.valueOf(OrderConstant.CardType.OTHER)})});

        // 校验手机号码
        rules.put(new String[]{
                "member.phone" // 导游手机号
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        return rules;
    }

    /**
     * 删除团队成员验证
     * @return
     */
    public Map<String[], ValidRule[]> deleteGroupMemberValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{"group_id", "member_id"}, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});
        return rules;
    }
}
