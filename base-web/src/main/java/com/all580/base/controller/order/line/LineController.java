package com.all580.base.controller.order.line;

import com.all580.base.manager.GernerateValidate;
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

    @RequestMapping(value = "group/list", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> listGroup(@RequestBody Map params) {
        GernerateValidate gernerateValidate = new GernerateValidate();
        gernerateValidate.addRules(new String[]{"booking_start_date","booking_end_date"},
                                    new ValidRule[]{new ValidRule.Date("yyyy-mm-dd")})
                        .validate(params);

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
    public Result<?> getLineGroupDetailByNumber(@RequestParam String number, @RequestParam String ep_id) {
        Map<String,Object> params = new HashMap<>();
        params.put("number",number);
        GernerateValidate gernerateValidate = new GernerateValidate();
        gernerateValidate.addRules(new String[]{"number"}, new ValidRule[]{new ValidRule.NotNull()}).validate(params);

        //String epId = String.valueOf(this.getRequest().getParameter("ep_id"));
        //System.out.println("ep_id----->" + epId);
        return lineOrderService.getLineGroupDetailByNumber(number, ep_id);
    }

    @RequestMapping(value = "visitor/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> listOrderVisitor(@RequestParam String number,
                                      @RequestParam(defaultValue = "0") Integer record_start,
                                      @RequestParam(defaultValue = "20") Integer record_count) {
        Map<String,Object> params = new HashMap<>();
        params.put("number",number);
        GernerateValidate gernerateValidate = new GernerateValidate();
        gernerateValidate.addRules(new String[]{"number"},new ValidRule[]{new ValidRule.NotNull()}).validate(params);

        String epId = String.valueOf(this.getRequest().getParameter(EpConstant.EpKey.EP_ID));
        return lineOrderService.listOrderVisitor(number,epId,record_start,record_count);
    }

    @RequestMapping(value = "guide/set", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> setLineGroupGuide(@RequestBody Map params) {
        GernerateValidate gernerateValidate = new GernerateValidate();
        gernerateValidate.addRules(new String[]{"number","guide_name","guide_phone","guide_card","guide_sid"},new ValidRule[]{new ValidRule.NotNull()})
                        .addRules(new String[]{"guide_phone"}, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)})
                        .addRules(new String[]{"guide_sid"}, new ValidRule[]{new ValidRule.IdCard()})
                        .validate(params);

        return lineOrderService.setLineGroupGuide(params);
    }

    @RequestMapping(value = "group_status/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateLineGroupStatus(@RequestBody Map params) {
        GernerateValidate gernerateValidate = new GernerateValidate();
        gernerateValidate.addRules(new String[]{"number","status"}, new ValidRule[]{new ValidRule.NotNull()}).validate(params);

        return lineOrderService.updateLineGroupStatus(params);
    }
}
