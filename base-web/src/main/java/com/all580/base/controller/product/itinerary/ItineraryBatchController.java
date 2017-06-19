package com.all580.base.controller.product.itinerary;

import com.all580.product.api.hotel.service.ItineraryBatchService;
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

/**
 * Created by wxming on 2017/5/25 0025.
 */
@Controller
@RequestMapping(value = "api/product/itinerary/batch")
public class ItineraryBatchController {
    @Autowired
    ItineraryBatchService itineraryBatchService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addItinerary(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateItineraryBatchValidate());
        return itineraryBatchService.addItineraryBatch(params);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateItineraryBatch(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateItineraryBatchValidate());
        return itineraryBatchService.updateItineraryBatch(params);
    }

    public Map<String[], ValidRule[]> generateCreateItineraryBatchValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
                "product_sub_id", //
                "stock", //
                "settle_price", //
                "effective_end_date", //
                "effective_start_date", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "stock" ,
                "product_sub_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
