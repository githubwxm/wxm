package com.all580.base.controller.order.line;

import com.all580.ep.api.conf.EpConstant;
import com.all580.order.api.service.LineOrderService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.applet.Main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzw on 2017/5/24.
 */
@Controller
@RequestMapping("api/order/line")
@Slf4j
public class LineController extends BaseController{

    @Autowired
    private LineOrderService lineOrderService;

    @RequestMapping(value = "group/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> listGroup(@RequestBody Map params) {
        params.put(EpConstant.EpKey.EP_ID, this.getRequest().getAttribute(EpConstant.EpKey.EP_ID));
        System.out.println("params--->"+params);
        if(params.get("record_start") == null){
            params.put("record_start",0);
        }
        if(params.get("record_count") == null){
            params.put("record_count",20);
        }
        return lineOrderService.listGroup(params);
    }

    @RequestMapping(value = "group/detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getLineGroupDetailByNumber(@RequestParam String number) {
        Map<String,Object> params = new HashMap<>();
        params.put("number",number);
        this.gernerateValidate(params, new String[]{"number"},new ValidRule[]{new ValidRule.NotNull()});

        String epId = String.valueOf(this.getRequest().getAttribute(EpConstant.EpKey.EP_ID));
        return lineOrderService.getLineGroupDetailByNumber(number, epId);
    }

    @RequestMapping(value = "visitor/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> listOrderVisitor(@RequestParam String number,
                                      @RequestParam(defaultValue = "0") Integer record_start,
                                      @RequestParam(defaultValue = "20") Integer record_count) {
        Map<String,Object> params = new HashMap<>();
        params.put("number",number);
        this.gernerateValidate(params, new String[]{"number"},new ValidRule[]{new ValidRule.NotNull()});

        String epId = String.valueOf(this.getRequest().getAttribute(EpConstant.EpKey.EP_ID));
        return lineOrderService.listOrderVisitor(number,epId,record_start,record_count);
    }

    private void gernerateValidate(Map<String,Object> params, String[] fields,ValidRule[] validRules){
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(fields, validRules);
        ParamsMapValidate.validate(params, rules);
    }
}
