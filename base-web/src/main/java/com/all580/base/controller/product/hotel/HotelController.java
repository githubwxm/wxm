package com.all580.base.controller.product.hotel;

import com.all580.ep.api.conf.EpConstant;
import com.all580.product.api.hotel.service.HotelService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/1/17 0017.
 */
@Controller
@RequestMapping(value = "api/product/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addHotel(@RequestBody Map params) {
        ParamsMapValidate.validate(params, generateCreateHotelValidate());
        return hotelService.addHotel(params);
    }

    @RequestMapping(value = "select_hotel_name", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectHotelName(HttpServletRequest request, String product_name,String ep_id,  Integer record_start,
                                     Integer record_count) {
        Map<String,Object> map = new HashMap();
        map.put("product_name",product_name);
        map.put("ep_id",ep_id);
        map.put("record_start", record_start);
        map.put("record_count", record_count);
        map.put(EpConstant.EpKey.CORE_EP_ID, request.getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return hotelService.selectHotelName(map);
    }





    public Map<String[], ValidRule[]> generateCreateHotelValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name", //
                "team_num", //
                "province", //
                "city", //
                "area", //
                "address", //
                "level_type", //
                "tel", //
                "invoice", //
                "imgs", //
                "server_type", //
                "facility_type", //
                "depot_type", //
                "card_type", //
                "pay_type", //
                "pet_type", //
                "name", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "team_num" ,// 企业id
                "province" ,// 企业id
                "city" ,// 企业id
                "area" ,// 企业id
                "level_type" ,// 企业id
                "invoice" ,// 企业id
                "pet_type" ,// 企业id
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
