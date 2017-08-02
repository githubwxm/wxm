package com.all580.base.controller.role;

import com.all580.role.api.service.FuncGroupLinkService;
import com.all580.role.api.service.FuncGroupService;
import com.all580.role.api.service.PlatFuncService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2017/6/29 0029.
 */
@Controller
@RequestMapping("api/ep/role/group")
@Slf4j
public class FuncGroupController extends BaseController {
    @Autowired
    FuncGroupService funcGroupService;

    @Autowired
    FuncGroupLinkService funcGroupLinkService;

    @Autowired
    PlatFuncService  platFuncService;


    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        ParamsMapValidate.validate(params, generateAddFuncGroupValidate());
        return funcGroupService.addFuncGroup(params);
    }
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        ParamsMapValidate.validate(params, generateUpdateFuncGroupValidate());
        return funcGroupService.updateFuncGroup(params);
    }

    @RequestMapping(value = "delete/id", method = RequestMethod.POST)
    @ResponseBody
    public Result deleteId(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        Integer id = CommonUtil.objectParseInteger(params.get("id"));
        Assert.notNull(id,"删除id不能为空");
        return funcGroupService.deleteFuncGroup(id);
    }
    @RequestMapping(value = "select/id", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String,Object>>> selectFuncGroupId(@RequestParam("id")  int id) {
        return funcGroupService.selectFuncGroupId(id);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String,Object>>> selectFuncGroupList() {
        return funcGroupService.selectFuncGroupList();
    }

    @RequestMapping(value = "func_group_id", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String,Object>>> selectFuncGroupList(@RequestParam(value = "func_group_id", required = true) Integer func_group_id ) {
        return funcGroupLinkService.selectFuncGroupLink(func_group_id);
    }

    @RequestMapping(value = "func/plat", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String,Object>>> selectPlatFromFuncGroupLink(@RequestParam("id")  int id) {
        return funcGroupLinkService.selectPlatFromFuncGroupLink(id);
    }

    @RequestMapping(value = "plat/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String,Object>>> selectPlatFromGroup(@RequestParam("id")  int id) {
        ///ep_func_type 1平台商  2供应商  4销售商  3 平供  5平销 6供销  7平供销
        return funcGroupLinkService.selectPlatFromFuncGroup(1,id);
    }

    @RequestMapping(value = "plat/update", method = RequestMethod.POST)
    @ResponseBody
    public Result platUpdate(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        Integer id = CommonUtil.objectParseInteger(params.get("id"));
        List list = (List)params.get("list");
        return platFuncService.addPlatFunc(id,list);
    }
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result deleteFuncGroupId(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        Integer id = CommonUtil.objectParseInteger(params.get("func_group_id"));
        Assert.notNull(id, "func_group_id  不能为空");
        return funcGroupLinkService.deleteFuncGroupId(id);
    }



    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(@RequestBody Map<String,Object> params) {
        if(!params.get("ep_id").equals("1")){
            throw  new ApiException("没有权限");//  中心平台才有操作
        }
        ParamsMapValidate.validate(params, generateAddFuncGroupLinkValidate());
        return funcGroupLinkService.addFuncGroupLink(params);
    }

    public Map<String[], ValidRule[]> generateAddFuncGroupLinkValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "func_group_id", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "func_group_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> generateAddFuncGroupValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "pid", //
                "title", //
                "choose_type", //
                "extend_type", //
                "show_type", //
                "ep_func_type", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "pid", //
                "choose_type", //
                "extend_type", //
                "show_type", //
                "ep_func_type", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> generateUpdateFuncGroupValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "pid", //
                "id", //
                "title", //
                "choose_type", //
                "extend_type", //
                "show_type", //
                "ep_func_type", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "pid", //
                "id", //
                "choose_type", //
                "extend_type", //
                "show_type", //
                "ep_func_type", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }



}
