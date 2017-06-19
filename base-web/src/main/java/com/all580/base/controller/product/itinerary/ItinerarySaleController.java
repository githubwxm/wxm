package com.all580.base.controller.product.itinerary;

import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.hotel.service.ItineraryPlanSaleService;
import com.all580.product.api.model.CanSaleOrderState;
import com.all580.product.api.model.ProductAndSubInfo;
import com.all580.product.api.service.ProductRPCService;
import com.framework.common.Result;
import com.framework.common.vo.Paginator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/5/26 0026.
 */
@Controller
@RequestMapping(value = "api/itinerary/sale")
public class ItinerarySaleController {
    @Resource
    ProductRPCService productService;

    @Resource
    ItineraryPlanSaleService itineraryPlanSaleService;
    /**
     * 产品分销列表
     * @param epId
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public Result<Paginator<ProductAndSubInfo>> searchCanSaleListHotel(@RequestParam("ep_id") Integer epId,
                                                                        String product_name,
                                                                      Integer is_supplier,
                                                                       Integer order_str,
                                                                       @RequestParam("record_start")  Integer record_start,
                                                                       @RequestParam("record_count") Integer record_count
            , Integer is_platfrom,Integer type ) {

        String orderStr = null;
        if(type==null){
            type= ProductConstants.ProductType.ITINERARY;
        }
        if (order_str != null) {
            switch (CanSaleOrderState.getCanSaleOrderSate(order_str)) {
                case CREATE_TIME_ASC: orderStr = CanSaleOrderState.CREATE_TIME_ASC.getValue(); break;
                case CREATE_TIME_DESC: orderStr = CanSaleOrderState.CREATE_TIME_DESC.getValue(); break;
                case PRODUCT_NAME_ASC: orderStr = CanSaleOrderState.PRODUCT_NAME_ASC.getValue(); break;
                case PRODUCT_NAME_DESC: orderStr = CanSaleOrderState.PRODUCT_NAME_DESC.getValue(); break;
            }
        }
        Result result = productService.searchSubProductListByProductNameHotel(is_platfrom,epId, product_name, is_supplier,
                orderStr, record_start, record_count,type);
        return result;
    }

    /***
     * 查询子产品  日期价格
     * @param ep_id
     * @param product_sub_id
     * @param start
     * @return
     */
    @RequestMapping(value = "product/plan", method = RequestMethod.GET)
    @ResponseBody
    public Result<?>selectItinerary(Integer ep_id,@RequestParam("product_sub_id")Integer product_sub_id,
                                    @RequestParam("start")String start,@RequestParam("end")String end,String from){
        Map map = new HashMap<>();
        map.put("from",from);
        map.put("ep_id",ep_id);
        map.put("product_sub_id",product_sub_id);
        map.put("start",start);
        map.put("end",end);
        return itineraryPlanSaleService.selectProductPlan(map);
    }
}
