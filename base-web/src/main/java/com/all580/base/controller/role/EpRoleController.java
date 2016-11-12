package com.all580.base.controller.role;

import com.all580.role.api.service.EpRoleService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/11/12 0012.
 */
@Controller
@RequestMapping("api/ep/role")
@Slf4j
public class EpRoleController {

    @Autowired
    private EpRoleService epRoleService;
    /**
     * 添加树形菜单接口
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<Map<String,Object>>> add(@RequestBody Map<String,Object> params) {
        ParamsMapValidate.validate(params, generateAddEpRoleValidate());
        return epRoleService.addEpRole(params);
    }

    /**
     * 添加树形菜单接口
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<Map<String,Object>>> update(@RequestBody Map<String,Object> params) {
        ParamsMapValidate.validate(params, generateUpdateEpRoleValidate());
        return epRoleService.updateEpRole(params);
    }



    public Map<String[], ValidRule[]> generateAddEpRoleValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
                "user_id", //
                "func_ids", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "user_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    //,status 状态 0 ， oper_id
    public Map<String[], ValidRule[]> generateUpdateEpRoleValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
                "id", //
                "status", //
                "func_ids", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
