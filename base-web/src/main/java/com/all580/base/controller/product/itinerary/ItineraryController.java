package com.all580.base.controller.product.itinerary;

import com.all580.ep.api.conf.EpConstant;
import com.all580.product.api.hotel.service.ItineraryService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/5/18 0018.
 */
@Controller
@RequestMapping(value = "api/product/itinerary")
public class ItineraryController {
    @Autowired
    private ItineraryService itineraryService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addItinerary(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateItineraryValidate());
        return itineraryService.addItinerary(params);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateItinerary(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateItineraryValidate());
        return itineraryService.updateItinerary(params);
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteItinerary(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateItineraryValidate());
        return itineraryService.deleteItinerary(params);
    }

    @RequestMapping(value = "select/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectItineraryList(HttpServletRequest request, String name, String ep_id,Integer product_id
                                      ,Integer id   ,Integer record_start,
                                     Integer record_count) {
        Map<String,Object> map = new HashMap();
        map.put("ep_id",ep_id);
        map.put("product_id",product_id);
        map.put("id",id);
        map.put("name",name);
        map.put("record_start", record_start);
        map.put("record_count", record_count);
        map.put(EpConstant.EpKey.CORE_EP_ID, request.getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return itineraryService.selectItineraryList(map);
    }
    @RequestMapping(value = "select/id", method = RequestMethod.GET)
    @ResponseBody
    public Result<?>selectItinerary(Integer id){
        return itineraryService.selectItinerary(id);
    }

    @RequestMapping(value = "can_sale/list")
    @ResponseBody
    public Result<?> canSaleList( String from,@RequestParam Integer ep_id,Integer end_city,Integer end_province,
                                  String label,String product_name,String start_date,String end_date,
                                  String product_sub_name ,Integer record_start,
                                  Integer record_count
                                 ) {
        Map map = new HashMap();
        map.put("ep_id",ep_id);
        map.put("end_city",end_city);
        map.put("end_province",end_province);
        map.put("label",label);
        map.put("product_name",product_name);
        map.put("start_date",start_date);
        map.put("end_date",end_date);
        map.put("product_sub_name",product_sub_name);
        map.put("from",from);
        map.put("record_start",record_start);
        map.put("record_count",record_count);
        return itineraryService.selectCanSaleList(map);
    }

    public Map<String[], ValidRule[]> generateCreateItineraryValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
                "subhead", //
                "start_province", //
                "start_city", //
                "end_province", //
                "end_city", //
                "label", //
                "vehicle", //
                "imgs", //
                "days", //
                "set_area", //
                "detail", //
                "booking_notes", //
                "visa_notes", //
                "cost_contain", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "visa_type" ,
                "start_province", //
                "start_city", //
                "end_province", //
                "end_city", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
