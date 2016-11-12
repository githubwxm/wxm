package com.all580.base.controller.role;

import com.all580.role.api.service.EpRoleService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
     * 添加角色
     *
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(@RequestBody Map<String, Object> params) {
        ParamsMapValidate.validate(params, generateAddEpRoleValidate());
        return epRoleService.addEpRole(params);
    }



    /**
     * 修改角色
     *
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map<String, Object> params) {
        ParamsMapValidate.validate(params, generateUpdateEpRoleValidate());
        return epRoleService.updateEpRole(params);
    }

    /**
     * 添加菜单
     *
     * @return
     */
    @RequestMapping(value = "add/func", method = RequestMethod.POST)
    @ResponseBody
    public Result addFunc(@RequestBody Map<String, Object> params) {
        //ep_role_id
        ParamsMapValidate.validate(params, generateAddEpRoleFuncValidate());
        return epRoleService.addEpRoleFunc(params);
    }
    /**
     * 修改角色菜单
     *
     * @return
     */
    @RequestMapping(value = "update/func", method = RequestMethod.POST)
    @ResponseBody
    public Result updateFunc(@RequestBody Map<String, Object> params) {
        ParamsMapValidate.validate(params, generateUpdateEpRoleFuncValidate());
        return epRoleService.updateEpRoleFunc(params);
    }//selectepRoleId
    /**
     * 查询角色菜单
     *
     * @return
     */
    @RequestMapping(value = "select/func", method = RequestMethod.GET)
    @ResponseBody
    public Result selectepRoleId(@RequestParam(value = "prodName", required = true) Integer ep_role_id) {
        return epRoleService.selectepRoleId(ep_role_id);
    }//




    public Map<String[], ValidRule[]> generateAddEpRoleValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
                "user_id", //
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
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    /**
     * @return
     */
    public Map<String[], ValidRule[]> generateAddEpRoleFuncValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_role_id", //
                "oper_id", //
                "func_ids", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "ep_role_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> generateUpdateEpRoleFuncValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "oper_id",
                "ep_role_id", //
                "func_ids", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "ep_role_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
