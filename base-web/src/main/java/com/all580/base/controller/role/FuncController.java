package com.all580.base.controller.role;

import com.all580.role.api.service.FuncService;
import com.all580.role.api.service.IntfService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
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
 * Created by wxming on 2016/11/10 0010.
 */
@Controller
@RequestMapping("api/role/func")
@Slf4j
public class FuncController extends BaseController {
    @Autowired
    private FuncService funcService;

    @Autowired
    private IntfService intfService;

    /**
     * 查询树形菜单接口
     * @return
     */
    @RequestMapping(value = "getAll", method = RequestMethod.GET)
    @ResponseBody
    public Result <List<Map<String,Object>>> getAll() {
        return funcService.getAll();
    }
    /**
     * 添加树形菜单接口
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(@RequestBody Map<String,Object> params) {
        //params.put("status",1);//添加默认状态
        ParamsMapValidate.validate(params, generateFuncValidate());
        return funcService.insertSelective(params);
    }
    /**
     * 修改树形菜单接口
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result  update(@RequestBody Map<String,Object> params) {
        ParamsMapValidate.validate(params, generateFuncValidate());
        return funcService.updateByPrimaryKeySelective(params);
    }

    /**
     * 删除树形菜单接口
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result  delete(@RequestBody Map<String,Object> params) {
        int id= CommonUtil.objectParseInteger(params.get("id"));
        return funcService.deleteByPrimaryKey(id);
    }

//    /**
//     * 查询菜单下接口
//     * @return
//     */
//    @RequestMapping(value = "selectFuncId", method = RequestMethod.GET)
//    @ResponseBody
//    public Result  selectFuncId(int id) {
//        return intfService.selectFuncId(id);
//    }


    /**
     * 校验id
     * @return
     */
    public Map<String[], ValidRule[]> generateFuncValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "pid", //
                "name", //
                "seq", //
                "type", //
                "status",
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "pid", //
                "seq", //
                "type", //
                "status",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

}
