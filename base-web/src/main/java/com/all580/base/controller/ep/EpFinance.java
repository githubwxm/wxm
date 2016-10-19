package com.all580.base.controller.ep;


import com.all580.ep.api.service.LogCreditService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.exception.ParamsMapValidationException;
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
import java.util.Map;

/**
 * Created by wxming on 2016/10/19 0019.
 */
@Controller
@RequestMapping("api/finance/credit")
@Slf4j
public class EpFinance extends BaseController {
    @Autowired
    private LogCreditService logCreditService;

    /**
     * 查询授信列表
     * @param map
     * @return
     */
    @RequestMapping(value = "platform/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map>  getList(@RequestBody Map map) {
        // 验证参数
        try {
            return logCreditService.selectList(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }
    /**
     * 查询授信历史列表
     * @param map
     * @return
     */
    @RequestMapping(value = "hostoryCredit", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map>  hostoryCredit(@RequestBody Map map) {
        // 验证参数
        try {
            ParamsMapValidate.validate(map, generateCreateSelectValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("修改企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return logCreditService.hostoryCredit(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }
    /**
     * 添加授信
     * @param map
     * @return
     */
    @RequestMapping(value = "set", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer>  set(@RequestBody Map map) {
        // 验证参数
        try {
            ParamsMapValidate.validate(map, generateCreateCreditValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("修改企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return logCreditService.create(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }//set


    public Map<String[], ValidRule[]> generateCreateCreditValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id", //
                "credit_after", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id", //
                "credit_after", //授信额度
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    public Map<String[], ValidRule[]> generateCreateSelectValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
