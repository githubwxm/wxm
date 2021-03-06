package com.all580.base.manager;

import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.apache.commons.beanutils.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiangzw on 2017/6/2.
 */
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

    public void validate(Object simpleObject){
        try {
            Map<String, Object> params = BeanUtils.describe(simpleObject);
            ParamsMapValidate.validate(params, rules);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
