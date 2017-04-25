package com.all580.base.controller.product.hotel;

import com.all580.ep.api.service.EpService;
import com.all580.product.api.hotel.service.HotelBatchService;
import com.all580.product.api.hotel.service.HotelPlanSaleService;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/2/4 0004.
 */
@Controller
@RequestMapping(value = "api/product/hotel/batch")
public class HotelBatchController {

    @Autowired
    private HotelBatchService hotelBatchService;

    @Autowired
    private HotelPlanSaleService hotelPlanSaleService;

    @Autowired
    private EpService epService;

    /**
     * 添加销售计划
     * @param params
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addHotel(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateHotelBatchValidate());
        return hotelBatchService.addHotelBatch(params);
    }

    /**
     * 保留日期
     * @param params
     * @return
     */
    @RequestMapping(value = "retain_day", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateRetainDay(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateHotelBatchRetainValidate());
        return hotelBatchService.updateRetainDay(params);
    }



    @RequestMapping(value = "update_up", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateUp(@RequestBody Map params) {
        return hotelPlanSaleService.updateUp(params);
    }

    @RequestMapping(value = "update_down", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateDown(@RequestBody Map params) {
        return hotelPlanSaleService.updateDown(params);
    }

    @RequestMapping(value = "update_product_down", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateDownBatch(@RequestBody Map params) {
          Integer ep_id = CommonUtil.objectParseInteger( params.get("ep_id"));
        Integer core_ep_id=epService.selectPlatformId(ep_id).get();
        if(ep_id.equals(core_ep_id)){
            return new Result<>(false,"平台商不用下架");
        }
        return hotelPlanSaleService.updateDown(params);
    }


    /**
     * 查询是否有有效销售计划
     * @param batch_id
     * @return
     */
    @RequestMapping(value = "select_batch_count", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectBatchCount(int batch_id){


         return hotelBatchService.selectBatchCount(batch_id);
    }

    /**
     * 删除销售计划
     * @param params
     * @return
     */
    @RequestMapping(value = "delete_hotel_batch", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteHotelBatch(@RequestBody Map params) {
        Integer batch_id =CommonUtil.objectParseInteger(params.get("batch_id"));
        Assert.isNull(batch_id);
        return hotelBatchService.deleteHotelBatch(batch_id);
    }
    /**
     * 查询销售计划
     * @param batch_id
     * @return
     */
    @RequestMapping(value = "select_hotel_batch", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectHotelBatch(int batch_id) {
        return hotelBatchService.selectHotelBatch(batch_id);
    }


    /**
     * 查询销售计划
     * @param batch_id
     * @return
     */
    @RequestMapping(value = "select_hotel_residue_batch", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectHotelResidueBatch(int batch_id) {
        return hotelBatchService.selectHotelBatch(batch_id);
    }




    public Map<String[], ValidRule[]> generateCreateHotelBatchRetainValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "batch_id", //
                "product_sub_id", //
                "list", //

        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "batch_id", //
                "product_sub_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    public Map<String[], ValidRule[]> generateCreateHotelBatchValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "effective_end_date", //
                "effective_start_date", //
                "week", //
                "product_sub_id", //
                "settle_price", //name
                "name", //

        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "product_sub_id", //
                "settle_price", //name
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
