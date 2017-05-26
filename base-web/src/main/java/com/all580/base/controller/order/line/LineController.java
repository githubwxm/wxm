package com.all580.base.controller.order.line;

import com.all580.order.api.service.LineOrderService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.applet.Main;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiangzw on 2017/5/24.
 */
@Controller
@RequestMapping("api/order/line")
public class LineController extends BaseController{

    @Autowired
    private LineOrderService lineOrderService;

    @RequestMapping(value = "group/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> listGroup(@RequestBody Map params) {
        return lineOrderService.listGroup(params);
    }

    @RequestMapping(value = "group/detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getLineGroupDetailByNumber(@RequestParam String number) {
        Map<String,Object> params = new HashMap<>();
        params.put("number",number);
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{"number"},new ValidRule[]{new ValidRule.NotNull()});
        ParamsMapValidate.validate(params,rules);
        return lineOrderService.getLineGroupDetailByNumber(number);
    }

    @RequestMapping(value = "visitor/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> listOrderVisitor(@RequestParam String number,
                                      @RequestParam(defaultValue = "0") Integer record_start,
                                      @RequestParam(defaultValue = "20") Integer record_count) {
        Map<String,Object> params = new HashMap<>();
        params.put("number",number);
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{"number"},new ValidRule[]{new ValidRule.NotNull()});
        ParamsMapValidate.validate(params,rules);
        return lineOrderService.listOrderVisitor(number,record_start,record_count);
    }
}
