package com.all580.base.controller.product.hotel;

import com.all580.product.api.hotel.service.HotelSubService;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
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

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateHotelSub(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateHotelSubValidate());
        return hotelSubService.updateHotelSubSummary(params);
    }

    @RequestMapping(value = "select_hotel_sub", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectHotel(Integer id) {

        return hotelSubService.selectHotelSub(id);
    }

    /**
     * 查询子产品库存
     * @param product_sub_id
     * @return
     */
    @RequestMapping(value = "select_hotel_sub_plan", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectBatchPlan(Integer product_sub_id) {

        return hotelSubService.selectBatchPlan(product_sub_id);
    }

    /**
     * 修改单个库存
     * @param params
     * @return
     */
    @RequestMapping(value = "update/single_stock", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateSingleStock(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateStockValidate());
        Integer stock= CommonUtil.objectParseInteger(params.get("stock"));
        Integer id= CommonUtil.objectParseInteger(params.get("id"));
        return hotelSubService.updateSingleStock(id,stock);
    }

    /**
     * 删除单个库存
     * @param params
     * @return
     */
    @RequestMapping(value = "delete/single_stock", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteSingleStock(@RequestBody Map params) {
        params.put("stock",0);
        ParamsMapValidate.validate(params, generateStockValidate());
        Integer id= CommonUtil.objectParseInteger(params.get("id"));
        return hotelSubService.deleteSingleStock(id);
    }
    /**
     * 删除单个库存
     * @param params
     * @return
     */
    @RequestMapping(value = "add/single_stock", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addPlan(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateAddStockValidate());
        return hotelSubService.addPlan(params);
    }
    /**
     * 查询子产品详情
     * @param id
     * @return
     */
    @RequestMapping(value = "select_hotel_sub_summary", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectHotelSubSunmary(Integer id) {

        return hotelSubService.selectHotelSubSunmary(id);
    }


    @RequestMapping("can_sale/list")
    @ResponseBody
    public Result<?> canSaleList(@RequestParam Integer ep_id, @RequestParam Integer product_id, Integer ticket_flag, String in_date, String out_date) {
        Date start_time = DateFormatUtils.converToDate(in_date);
        Date end_time = DateFormatUtils.converToDate(out_date);
        return hotelSubService.selectCanSaleByHotel(ep_id, product_id, ticket_flag, start_time, end_time);
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
               // "ep_ma_id", //
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
    public Map<String[], ValidRule[]> generateStockValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "stock", //
                "id", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "stock" ,// 企业id
                "id" ,// 企业id
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    public Map<String[], ValidRule[]> generateAddStockValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "start_date", //
                "product_sub_id", //
                "stock", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "stock" ,// 企业id
                "product_sub_id" ,// 企业id
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

}
