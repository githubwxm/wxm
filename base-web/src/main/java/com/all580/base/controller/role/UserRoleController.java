package com.all580.base.controller.role;

import com.all580.role.api.service.UserRoleService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/7/5 0005.
 */
@Controller
@RequestMapping("api/user/role")
@Slf4j
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result create(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        ParamsMapValidate.validate(params, generateAddUserRoleValidate());
        return userRoleService.addUserRole(params);
    }
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateUserRole(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        ParamsMapValidate.validate(params, generateUpdateUserRoleValidate());
        return userRoleService.updateUserRole(params);
    }
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public Result selectUserRoleList() {
        return userRoleService.selectUserRoleList();
    }

    @RequestMapping(value = "type/list", method = RequestMethod.GET)
    @ResponseBody
    public Result selectUserRoleTypeList(@RequestParam("ep_type") Integer ep_type) {
        return userRoleService.selectUserRoleTypeList(ep_type);
    }

    @RequestMapping(value = "id", method = RequestMethod.GET)
    @ResponseBody
    public Result selectUserId(@RequestParam("id")  int id) {
        return userRoleService.selectUserId(id);
    }



    @RequestMapping(value = "user/func", method = RequestMethod.GET)
    @ResponseBody
    public Result selectUserRoleIdFunc(@RequestParam("user_role_id")  int user_role_id) {
        return userRoleService.selectUserRoleIdFunc(user_role_id);
    }

    @RequestMapping(value = "add/user/func", method = RequestMethod.POST)
    @ResponseBody
    public Result addUserRoleFunc(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        ParamsMapValidate.validate(params, generateAddUserRoleFuncValidate());
        return userRoleService.addUserRoleFunc(params);
    }
    @RequestMapping(value = "update/user/func", method = RequestMethod.POST)
    @ResponseBody
    public Result updateUserRoleFunc(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        ParamsMapValidate.validate(params, generateAddUserRoleFuncValidate());
        return userRoleService.updateUserRoleFunc(params);
    }

    public Map<String[], ValidRule[]> generateAddUserRoleValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        return rules;
    }
    public Map<String[], ValidRule[]> generateUpdateUserRoleValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
                "id", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> generateAddUserRoleFuncValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "user_role_id", //
                "func_ids", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "user_role_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }


}
