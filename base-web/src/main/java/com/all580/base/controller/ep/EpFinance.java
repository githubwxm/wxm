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
import org.springframework.web.bind.annotation.*;

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
     *
     * @param
     * @return
     */
    @RequestMapping(value = "platform/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> getList(@RequestParam(value = "access_id") String access_id,
                               @RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "link_phone", required = false) String link_phone,
                               @RequestParam(value = "province", required = false) Integer province,
                               @RequestParam(value = "city", required = false) Integer city,
                               @RequestParam(value = "area", required = false) Integer area,
                               @RequestParam(value = "limitStart", required = false) Integer limitStart,
                               @RequestParam(value = "limitEnd", required = false) Integer limitEnd,
                               @RequestParam(value = "credit", required = false) boolean credit,
                               @RequestParam(value = "record_start", required = false) Integer record_start,
                               @RequestParam(value = "record_count", required = false) Integer record_count,
                               @RequestParam(value = "sign") String sign) {
        Map map = new HashMap();
        map.put("name", name);
        map.put("link_phone", link_phone);
        map.put("province", province);
        map.put("city", city);
        map.put("area", area);
        map.put("limitStart", limitStart);
        map.put("limitEnd", limitEnd);
        map.put("credit", credit);
        map.put("record_start", record_start);
        map.put("record_count", record_count);
        return logCreditService.selectList(map);


    }

    /**
     * 查询授信历史列表
     *
     * @return
     */
    @RequestMapping(value = "hostoryCredit", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> hostoryCredit(@RequestParam(value = "access_id") String access_id,
                                     @RequestParam(value = "ep_id") Integer ep_id,
                                     @RequestParam(value = "croe_ep_id") Integer croe_ep_id,
                                     @RequestParam(value = "sign") String sign) {
        Map map = new HashMap();
        map.put("ep_id", ep_id);
        map.put("croe_ep_id", croe_ep_id);
        ParamsMapValidate.validate(map, generateCreateSelectValidate());
        return logCreditService.hostoryCredit(map);


    }

    /**
     * 添加授信
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "set", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> set(@RequestBody Map map) {
        // 验证参数
        try {
            ParamsMapValidate.validate(map, generateCreateCreditValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("修改企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return logCreditService.create(map);
        } catch (ApiException e) {
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
                "croe_ep_id",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id", //
                "croe_ep_id",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
