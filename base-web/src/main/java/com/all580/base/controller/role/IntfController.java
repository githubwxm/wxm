package com.all580.base.controller.role;

import com.all580.role.api.service.IntfService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
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
    public Result<List<Map<String,Object>>> add(@RequestBody Map<String,Object> params) {

        return intfService.insertInft(params);
    }
    /**
     * 删除接口
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<Map<String,Object>>> delete(@RequestBody Map<String,Object> params) {
        int id= CommonUtil.objectParseInteger(params.get("id"));
        return intfService.deleteInft(id);
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
