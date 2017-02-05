package com.all580.base.controller.product.hotel;

import com.all580.product.api.hotel.service.HotelSubService;
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
 * Created by wxming on 2017/1/18 0018.
 */
@Controller
@RequestMapping(value = "api/product/hotel/sub")
public class HotelSubController {
    @Autowired
    private HotelSubService hotelSubService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addHotel(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateHotelSubValidate());
        return hotelSubService.addHotelSub(params);
    }



    public Map<String[], ValidRule[]> generateCreateHotelSubValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "product_id", //
                "name", //
                "market_price", //
                "min_sell_price", //
                "ticket_flag", //
                "pay_type", //
                "ma_product_id", //
                "ep_ma_id", //
                "window", //
                "ischeck_in_latest", //
                "return_money", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "product_id" ,// 企业id
                "market_price" ,// 企业id
                "min_sell_price" ,// 企业id
                "ticket_flag" ,// 企业id
                "pay_type" ,// 企业id
                "ma_product_id" ,// 企业id
                "ep_ma_id" ,// 企业id
                "window", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
