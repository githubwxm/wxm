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
        addProductPlanParams.setProduct_sub_id(CommonUtil.objectParseInteger(params.get("product_sub_id")));
        addProductPlanParams.setEnd_date(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("end_date"))));
        addProductPlanParams.setEpId(CommonUtil.objectParseInteger(params.get("ep_id")));
        addProductPlanParams.setStart_date(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("start_date"))));
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
        return productSalesPlanService.updateProductPlan(CommonUtil.objectParseInteger(params.get("product_plan_id")), CommonUtil.objectParseInteger(params.get("stock")));
    }

    /**
     * 删除子产品销售计划
     * @param params
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteSalesPlan(@RequestBody Map params) {
        return productSalesPlanService.deleteProductPlanByProductSubId(CommonUtil.objectParseInteger(params.get("product_sub_id")));
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
            @RequestParam("product_name") String productName,
            @RequestParam("product_sub_name") String productSubName,
            @RequestParam("province") Integer province,
            @RequestParam("city") Integer city,
            @RequestParam("area") Integer area,
            @RequestParam("ticket_flag") Integer ticketFlag,
            @RequestParam("pay_type") Integer payType,
            @RequestParam("ticket_dict") Integer ticketDict,
            @RequestParam("record_start") Integer start,
            @RequestParam("record_count") Integer count) {
        return productSalesPlanService.selectCanSaleSubProduct(epId,productName,productSubName,province,city,area,ticketFlag,payType,ticketDict,start,count);
    }

    /**
     * 对组分销
     * @param params
     * @return
     */
    @RequestMapping(value = "sale/group", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubDistributionGroup(@RequestBody Map params) {
        return productService.productOnSaleToGroup(initGroupOnsalesParams(params));
    }

    @RequestMapping(value = "sale/ep/creator", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubDistributionCreatorEp(@RequestBody Map params) {
        return productService.productOnSaleBatch(updateEpOnsalesParams(params));
    }

    /**
     * 对企业分销
     * @param params
     * @return
     */
    @RequestMapping(value = "sale/ep", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubDistributionEp(@RequestBody Map params) {
        return  productService.productOnSaleBatch(initEpOnsalesParams(params));
    }

    /**
     * 对单个平台商上架多个产品批次
     * @param params
     * @return
     */
    @RequestMapping(value = "on_sale/platform_ep", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubsDistributionPlatformEp(@RequestBody Map params) {
        return productService.productOnSaleProductBatch(initPlatformPlanSalesParams(params));
    }

    private List<Map<String, Object>> initPlatformPlanSalesParams(Map params) {
        Map<String, Object> ep = epService.selectId(CommonUtil.objectParseInteger(params.get("platform_ep_id"))).get();
        List<Map<String, Object>> onSales = (List<Map<String, Object>>) params.get("saled_array");
        for (Map<String, Object> planSale : onSales) {
            planSale.put("sale_ep_id", params.get("ep_id"));
            planSale.put("ep_id", ep.get("id"));
            planSale.put("name", ep.get("name"));
            // planSale内有product_sub_id
            // planSale内有batch_id
            // planSale内有price
            // planSale内有price_type
            // planSale内有price_pixed
            // planSale内有price_percent
        }
        return onSales;
    }

    private List<Map<String, Object>> initPlatformPlanOffSalesParams(Map params) {
        List<String> batches = (List<String>) params.get("off_sale_array");
        List<Map<String, Object>> offSales = new ArrayList<>();
        for (String batch_id : batches) {
            Map<String, Object> ep = new HashMap<>();
            ep.put("sale_ep_id", params.get("ep_id"));
            ep.put("ep_id", params.get("platform_ep_id"));
            ep.put("batch_id", CommonUtil.objectParseInteger(batch_id));
            offSales.add(ep);
        }
        return offSales;
    }

    /**
     * 对单个平台商下架多个产品批次
     * @param params
     * @return
     */
    @RequestMapping(value = "off_sale/platform_ep", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubsOffDistributionPlatformEp(@RequestBody Map params) {
        return productService.productOffSaleProductBatch(initPlatformPlanOffSalesParams(params));
    }

    private List<Map> updateEpOnsalesParams(Map params){
        Integer ep_id= epService.selectCreatorEpId(CommonUtil.objectParseInteger(params.get("ep_id"))).get();//获取上级id
        if(ep_id==null){
            return null;
        }
        Map<String,Object> mapId  = new HashMap();
        mapId.put("id", ep_id);
        Result<Map<String,Object>> ep=  epService.selectId(ep_id);//获取企业信息
        List<Map> list = new ArrayList<>();
        String name = CommonUtil.objectParseString(ep.get().get("name"));
        Map map = new HashMap();
        map.put("name",name);
        map.put("sale_ep_id",params.get("ep_id"));
        map.put("ep_id",ep_id);
        map.put("price_type",params.get("price_type"));
        map.put("price_percent",params.get("price_percent"));
        map.put("price_pixed",params.get("price_pixed"));
        map.put("product_sub_id",params.get("product_sub_id"));
        list.add(map);
        return list;
    }
    private List<Map> initEpOnsalesParams (Map params) {
        List<Map> onSalesEps = (List<Map>) params.get("saled_array");
        for (Map ep : onSalesEps) {
            ep.put("product_sub_id", params.get("product_sub_id"));
            ep.put("sale_ep_id", params.get("ep_id"));
        }
        return onSalesEps;
    }

    private List<OnSalesParams> initGroupOnsalesParams (Map params) {
        List<OnSalesParams> onSalesParamses = new ArrayList<>();
        List<Map> saledArray = (List<Map>) params.get("saled_array");
        for (Map group : saledArray) {
            OnSalesParams onSalesParams = new OnSalesParams();
            onSalesParams.setProduct_sub_id(CommonUtil.objectParseInteger(params.get("product_sub_id")));
            onSalesParams.setPrice_pixed(CommonUtil.objectParseInteger(group.get("price_pixed")));
            onSalesParams.setPrice_percent(CommonUtil.objectParseInteger(group.get("price_percent")));
            onSalesParams.setPrice_type(CommonUtil.objectParseInteger(group.get("price_type")));
            onSalesParams.setSale_ep_id(CommonUtil.objectParseInteger(params.get("ep_id")));
            onSalesParams.setGroup_id(CommonUtil.objectParseInteger(group.get("group_id")));
            onSalesParamses.add(onSalesParams);
        }
        return onSalesParamses;
    }

    /**
     * 对上级供应商下架
     * @param params
     * @return
     */
    @RequestMapping(value = "off_sale/supplier", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubOffSaleSupplier(@RequestBody Map params) {
        return productService.productOffSaleSupplier(params);
    }

    /**
     * 对企业下架
     * @param params
     * @return
     */
    @RequestMapping(value = "off_sale/ep", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubOffSaleEp(@RequestBody Map params) {
        List<Map> paramList = (List<Map>) params.get("off_sale_array");
        if (paramList == null || paramList.isEmpty()) return new Result(false, "参数错误");
        return productService.productOffSaleBatch(params);
    }

    /**
     * 对组下架
     * @param params
     * @return
     */
    @RequestMapping(value = "off_sale/group", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubOffSaleGroup(@RequestBody Map params) {
        List<Map> paramList = (List<Map>) params.get("off_sale_array");
        if (paramList == null || paramList.isEmpty()) return new Result(false, "参数错误");
        return productService.productOffSaleGroup(params);
    }

    /**
     * 查询产品销售日历
     * @param productSubId
     * @param startDate
     * @param endDate
     * @param recordStart
     * @param recordCount
     * @return  //
     */
    @RequestMapping(value = "sale/calendar/list")
    @ResponseBody
    public Result<Paginator<ProductPlanInfo>> searchPlanCalendar (@RequestParam("product_sub_id") Integer productSubId, @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate, @RequestParam("record_start") Integer recordStart, @RequestParam("record_count") Integer recordCount ) {
        return productSalesPlanService.searchPlanCalendar(DateFormatUtils.converToDateTime(startDate), DateFormatUtils.converToDateTime(endDate), productSubId, recordStart, recordCount);
    }

}
