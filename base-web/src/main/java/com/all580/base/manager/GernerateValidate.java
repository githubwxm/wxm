package com.all580.base.manager;

import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiangzw on 2017/6/2.
 */
@Component
public class GernerateValidate {
    private Map<String[], ValidRule[]> rules = new HashMap<>();
    public GernerateValidate(){}

    public GernerateValidate addRules(String[] fields, ValidRule[] validRules){
        rules.put(fields, validRules);
        return this;
    }

    public void validate(Map<String, Object> parasms){
        ParamsMapValidate.validate(parasms, rules);
    }
}
