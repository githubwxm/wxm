package com.all580.base.controller.product.hotel;

import com.all580.product.api.hotel.service.HotelPlanSaleService;
import com.all580.product.api.hotel.service.HotelService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/2/9 0009.
 */
@Controller
@RequestMapping(value = "api/product/hotel")
public class HotelPlanSaleController {

    @Autowired
    private HotelPlanSaleService hotelPlanSaleService;


    /**
     * 销售计划批次上架
     * @param params
     * @return
     */
    @RequestMapping(value = "sale/up", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateUp(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateSaleUpdateUpValidate());
        return hotelPlanSaleService.updateUp(params);
    }

    /**
     * 销售计划批次下架
     * @param params
     * @return
     */
    @RequestMapping(value = "sale/down", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateDown(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateSaleUpdateUpValidate());
        return hotelPlanSaleService.updateDown(params);
    }

    /**
     * 指定一个或多个企业分销    商家的话  mpa list 里是一个上级供应商 （下游）
     * @param params
     * @return
     */
    @RequestMapping(value = "sale/batch/up", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> productOnSaleBatch(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateSaleUpdateUpValidate());
        return hotelPlanSaleService.productOnSaleBatch(params);
    }

    @RequestMapping(value = "sale/select/not_sale", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectNotSale(@RequestParam("ep_id") Integer ep_id,
                                   @RequestParam("batch_id") Integer batch_id) {
        return hotelPlanSaleService.selectNotSale(ep_id,batch_id);
    }



    public Map<String[], ValidRule[]> generateSaleUpdateUpValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "batch_id", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "batch_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
