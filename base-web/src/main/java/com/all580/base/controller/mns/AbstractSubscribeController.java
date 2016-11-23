package com.all580.base.controller.mns;

import com.framework.common.BaseController;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/11/23 11:29
 */
public abstract class AbstractSubscribeController extends BaseController {
    public void validate(Map params) {
        Map<String[], ValidRule[]> validRuleMap = new HashMap<>();
        validRuleMap.put(new String[]{"action", "content", "createTime"}, new ValidRule[]{new ValidRule.NotNull()});
        validRuleMap.put(new String[]{"createTime"}, new ValidRule[]{new ValidRule.Date()});
        ParamsMapValidate.validate(params, validRuleMap);
    }
}
