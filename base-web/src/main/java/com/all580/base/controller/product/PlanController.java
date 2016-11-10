package com.all580.base.controller.product;

import com.all580.ep.api.service.EpService;
import com.all580.product.api.model.AddProductPlanParams;
import com.all580.product.api.model.CanSaleSubProductInfo;
import com.all580.product.api.model.OnSalesParams;
import com.all580.product.api.model.ProductPlanInfo;
import com.all580.product.api.service.ProductRPCService;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.StringUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.Paginator;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售计划接口网关
 */
@Controller
@RequestMapping(value = "api/plan")
public class PlanController extends BaseController {

    @Resource
    ProductSalesPlanRPCService productSalesPlanService;

    @Resource
    ProductRPCService productService;

    @Resource
    EpService epService;

    /**
     * 增加销售计划
     * @param params
     * @return
     */
    @RequestMapping(value = "config", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addSalesPlan(@RequestBody Map params) {
        return productSalesPlanService.addProductPlanBatch(initAddProductPlanParams(params));
    }

    private AddProductPlanParams initAddProductPlanParams(Map params) {
        AddProductPlanParams addProductPlanParams = new AddProductPlanParams();
        addProductPlanParams.setProductSubId(CommonUtil.objectParseInteger(params.get("productSubId")));
        addProductPlanParams.setEndDate(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("endDate"))));
        addProductPlanParams.setEpId(CommonUtil.objectParseInteger(params.get("ep_id")));
        addProductPlanParams.setStartDate(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("startDate"))));
        if (params.get("stock") != null && !params.get("stock").toString().equals("")) {
            addProductPlanParams.setStock(CommonUtil.objectParseInteger(params.get("stock")));
        }
        addProductPlanParams.setTimes((List)params.get("times"));
        addProductPlanParams.setWeek((List)params.get("week"));
        return addProductPlanParams;
    }

    /**
     * 修改销售计划
     * @param params
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateSalesPlan(@RequestBody Map params) {
        return null;
    }

    /**
     * 查询可售子产品列表
     * @param epId
     * @param productName
     * @param productSubName
     * @param province
     * @param city
     * @param area
     * @param ticketFlag
     * @param payType
     * @param start
     * @param count
     * @return
     */
    @RequestMapping(value = "can_sale/sub/list")
    @ResponseBody
    public Result<Paginator<Map<String, ?>>> canSaleSubProductList(
            @RequestParam("ep_id") Integer epId,
            @RequestParam("productName") String productName,
            @RequestParam("productSubName") String productSubName,
            @RequestParam("province") Integer province,
            @RequestParam("city") Integer city,
            @RequestParam("area") Integer area,
            @RequestParam("ticketFlag") Integer ticketFlag,
            @RequestParam("payType") Integer payType,
            @RequestParam("ticketDict") Integer ticketDict,
            @RequestParam("record_start") Integer start,
            @RequestParam("record_count") Integer count) {
        return productSalesPlanService.selectCanSaleSubProduct(epId,productName,productSubName,province,city,area,ticketFlag,payType,ticketDict,start,count);
    }

    @RequestMapping(value = "sale/group", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubDistributionGroup(@RequestBody Map params) {
        for (OnSalesParams param : initGroupOnsalesParams(params)) {
            productService.productOnSale(param);
        }
        return new Result(true);
    }

    @RequestMapping(value = "sale/ep/creator", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubDistributionCreatorEp(@RequestBody Map params) {
        List<Map> list =updateEpOnsalesParams(params);
        return productService.productOnSaleBatch(list);
    }

    @RequestMapping(value = "sale/ep", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubDistributionEp(@RequestBody Map params) {

        return  productService.productOnSaleBatch(initEpOnsalesParams(params));
    }

    private List<Map> updateEpOnsalesParams(Map params){
        Integer ep_id= epService.selectCreatorEpId(CommonUtil.objectParseInteger(params.get("ep_id"))).get();//获取上级id
        if(ep_id==null){
            return null;
        }
        Map<String,Object> mapId  = new HashMap();
        mapId.put("id", ep_id);
        Result<Map<String,Object>> ep=  epService.selectId(mapId);//获取企业信息
        List<Map> list = new ArrayList<Map>();
        String name = CommonUtil.objectParseString(ep.get().get("name"));
         Map map = new HashMap();
        map.put("name",name);
        map.put("saleEpId",params.get("ep_id"));
        map.put("ep_id",ep_id);
        map.put("priceType",params.get("priceType"));
        map.put("pricePercent",params.get("pricePercent"));
        map.put("pricePixed",params.get("pricePixed"));
        map.put("productSubId",params.get("productSubId"));
        list.add(map);
        return list;
    }
    private List<Map> initEpOnsalesParams (Map params) {
        List<Map> onSalesEps = (List<Map>) params.get("saledArray");
        for (Map ep : onSalesEps) {
            ep.put("productSubId", params.get("productSubId"));
            ep.put("saleEpId", params.get("ep_id"));
        }
        return onSalesEps;
    }

    private List<OnSalesParams> initGroupOnsalesParams (Map params) {
        List<OnSalesParams> onSalesParamses = new ArrayList<>();
        List<Map> saledArray = (List<Map>) params.get("saledArray");
        for (Map group : saledArray) {
            OnSalesParams onSalesParams = new OnSalesParams();
            onSalesParams.setProductSubId(CommonUtil.objectParseInteger(params.get("productSubId")));
            onSalesParams.setPricePixed(CommonUtil.objectParseInteger(group.get("pricePixed")));
            onSalesParams.setPricePercent(CommonUtil.objectParseInteger(group.get("pricePercent")));
            onSalesParams.setPriceType(CommonUtil.objectParseInteger(group.get("priceType")));
            onSalesParams.setSaleEpId(CommonUtil.objectParseInteger(params.get("ep_id")));
            onSalesParams.setGroupId(CommonUtil.objectParseInteger(group.get("groupId")));
            onSalesParamses.add(onSalesParams);
        }
        return onSalesParamses;
    }

    @RequestMapping(value = "off_sale/ep", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubOffSaleEp(@RequestBody Map params) {
        List<Map> paramList = (List<Map>) params.get("offSaleArray");
        if (paramList == null || paramList.isEmpty()) return new Result(false, "参数错误");
        return productService.productOffSaleBatch(params);
    }

    @RequestMapping(value = "off_sale/group", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubOffSaleGroup(@RequestBody Map params) {
        List<Map> paramList = (List<Map>) params.get("offSaleArray");
        if (paramList == null || paramList.isEmpty()) return new Result(false, "参数错误");
        return productService.productOffSaleGroup(paramList);
    }

    /**
     * 查询产品销售日历
     * @param productSubId
     * @param startDate
     * @param endDate
     * @param recordStart
     * @param recordCount
     * @return
     */
    @RequestMapping(value = "sale/calendar/list")
    @ResponseBody
    public Result<Paginator<ProductPlanInfo>> searchPlanCalendar (@RequestParam("product_sub_id") Integer productSubId, @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate, @RequestParam("record_start") Integer recordStart, @RequestParam("record_count") Integer recordCount ) {
        return productSalesPlanService.searchPlanCalendar(DateFormatUtils.converToDate(startDate), DateFormatUtils.converToDate(endDate), productSubId, recordStart, recordCount);
    }

}
