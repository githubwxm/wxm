package com.all580.base.controller.product.hotel;

import com.all580.product.api.hotel.service.HotelSubService;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @RequestMapping(value = "select_hotel_sub", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectHotel(Integer id) {

        return hotelSubService.selectHotelSub(id);
    }

    @RequestMapping("can_sale/list")
    @ResponseBody
    public Result<?> canSaleList(@RequestParam Integer ep_id, @RequestParam Integer product_id, String inDate, String outDate) {
        Date start_time = DateFormatUtils.converToDate(inDate);
        Date end_time = DateFormatUtils.converToDate(outDate);
        return hotelSubService.selectCanSaleByHotel(ep_id, product_id, start_time, end_time);
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
               // "ma_product_id", //
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
               // "ma_product_id" ,// 企业id
                "ep_ma_id" ,// 企业id
                "window", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
