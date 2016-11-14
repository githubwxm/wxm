package com.all580.base.controller.role;

import com.all580.role.api.service.IntfService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
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
 * Created by wxming on 2016/11/10 0010.
 */
@Controller
@RequestMapping("api/role/intf")
@Slf4j
public class IntfController extends BaseController{
    @Autowired
    private IntfService intfService;
    /**
     * 添加接口
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(@RequestBody Map<String,Object> params) {
        ParamsMapValidate.validate(params, generateIntfValidate());
        return intfService.insertInft(params);
    }
    /**
     * 删除接口
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Map<String,Object> params) {
        int id= CommonUtil.objectParseInteger(params.get("id"));
        return intfService.deleteInft(id);
    }//
    @RequestMapping(value = "selectFuncId", method = RequestMethod.GET)
    @ResponseBody
    public Result selectFuncId(@RequestParam(value = "func_id", required = true) Integer func_id,Integer record_start,
                               Integer record_count) {
        Map<String,Object> map = new HashMap<>();
        map.put("record_start", record_start);
        map.put("record_count", record_count);
        map.put("func_id", func_id);
        return intfService.selectFuncId(map);
    }//selectList
    @RequestMapping(value = "intfList", method = RequestMethod.GET)
    @ResponseBody
    public Result intfList(Integer record_start,
                           Integer record_count) {
        Map<String,Object> map = new HashMap<>();
        map.put("record_start", record_start);
        map.put("record_count", record_count);
        return intfService.intfList(map);
    }

    /**
     * 校验id
     * @return
     */
    public Map<String[], ValidRule[]> generateIntfValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
                "auth", //
                "func_id",
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "func_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
