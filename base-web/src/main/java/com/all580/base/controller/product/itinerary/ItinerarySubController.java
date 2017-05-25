package com.all580.base.controller.product.itinerary;

/**
 * Created by wxming on 2017/5/25 0025.
 */

import com.all580.product.api.hotel.service.ItinerarySubService;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


@Controller
@RequestMapping(value = "api/product/itinerary/sub")
public class ItinerarySubController {

    @Autowired
    ItinerarySubService itinerarySubService;
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addItinerary(@RequestBody Map params) {
        //ParamsMapValidate.validate(params, generateCreateItineraryValidate());
        return itinerarySubService.addItinerarylSub(params);
    }
}
