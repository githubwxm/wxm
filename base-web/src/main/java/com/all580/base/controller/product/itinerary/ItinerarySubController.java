package com.all580.base.controller.product.itinerary;

/**
 * Created by wxming on 2017/5/25 0025.
 */

import com.all580.product.api.hotel.service.ItinerarySubService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(value = "api/product/itinerary/sub")
public class ItinerarySubController {

    @Autowired
    ItinerarySubService itinerarySubService;
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addItinerary(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateItinerarySubValidate());
        return itinerarySubService.addItinerarylSub(params);
    }

    public Map<String[], ValidRule[]> generateCreateItinerarySubValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
                "product_id", //
                "require_sid", //
                "pay_type", //
                "market_price", //
                "min_sell_price", //
                "max_buy_quantity", //
                "cust_refund_rule", //
                "supplier_audit", //
                "team_prefix", //
                "team_suffix", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "product_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
