package com.all580.base.controller.product;

import com.all580.ep.api.service.EpService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.hotel.service.HotelPlanSaleService;
import com.all580.product.api.model.AddProductPlanParams;
import com.all580.product.api.model.CanSaleOrderState;
import com.all580.product.api.model.OnSalesParams;
import com.all580.product.api.model.ProductPlanInfo;
import com.all580.product.api.service.ProductRPCService;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.Paginator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.*;

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
    @Resource
    HotelPlanSaleService hotelPlanSaleService;

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
     * 查询可售子产品列表(销售商）
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
    public Result<Paginator<Map<String, Object>>> canSaleSubProductList(
            @RequestParam("ep_id") Integer epId,
            @RequestParam(value = "product_name", required = false) String productName,
            @RequestParam(value = "product_sub_name", required = false) String productSubName,
            @RequestParam(value = "province", required = false) Integer province,
            @RequestParam(value = "city", required = false) Integer city,
            @RequestParam(value = "area", required = false) Integer area,
            @RequestParam(value = "ticket_flag", required = false) Integer ticketFlag,
            @RequestParam(value = "pay_type", required = false) Integer payType,
            @RequestParam(value = "ticket_dict", required = false) Integer ticketDict,
            @RequestParam(value = "evl_level", required = false) Integer evlLevel,
            @RequestParam(value = "types", required = false) String types,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "sort", required = false) Integer sort,
            @RequestParam(value = "record_start") Integer start,
            @RequestParam(value = "record_count") Integer count) {
        String sortStr = null;
        if (sort != null) {
            switch (CanSaleOrderState.getCanSaleOrderSate(sort)) {
                case SOLD_QUANTITY_ASC: sortStr = CanSaleOrderState.SOLD_QUANTITY_ASC.getValue(); break;
                case SOLD_QUANTITY_DESC: sortStr = CanSaleOrderState.SOLD_QUANTITY_DESC.getValue(); break;
                case SALE_PRICE_ASC: sortStr = CanSaleOrderState.SALE_PRICE_ASC.getValue(); break;
                case SALE_PRICE_DESC: sortStr = CanSaleOrderState.SALE_PRICE_DESC.getValue(); break;
                case DISTRIBUTED_TIME_ASC: sortStr = CanSaleOrderState.DISTRIBUTED_TIME_ASC.getValue(); break;
                case DISTRIBUTED_TIME_DESC: sortStr = CanSaleOrderState.DISTRIBUTED_TIME_DESC.getValue(); break;
            }
        }
        List typeList = null;
        if (types != null) {
            String[] typeArray = types.split(",");
            if (typeArray != null)
            typeList = Arrays.asList(typeArray);
        }
        return productSalesPlanService.selectCanSaleSubProduct(epId,productName,productSubName,province,city,area,ticketFlag,payType,ticketDict,evlLevel, typeList, from, sortStr,start,count);
    }

    /**
     * 对多个组分销单个产品批次
     * @param params
     * @return
     */
    @RequestMapping(value = "sale/group", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubDistributionGroup(@RequestBody Map params) {
        return productService.productOnSaleToGroup(initGroupOnsalesParams(params));
    }

    /**
     * 对单个组分销多个产品批次
     * @param params
     * @return
     */
    @RequestMapping(value = "sale_batch/group", method = RequestMethod.POST)
    @ResponseBody
    public Result ProductSubBatchDistributionGroup(@RequestBody  Map params) {
        Integer group_id=CommonUtil.objectParseInteger(params.get("group_id"));
        Result result=new Result(true);
        Map<String,List<Map<String, Object>>> map =initGroupPlanSalesParams(params);
        if(!map.get("hotel").isEmpty()){
//            Map<String,Object> hotelMap = new HashMap<>();
//            hotelMap.putAll(params);
//            hotelMap.put("saled_array",map.get("hotel"));
            for(Map<String, Object> tempMap:map.get("hotel")){
                List<Map<String,Object>> list = new ArrayList<>();
                Map<String,Object> tMap = new HashMap<>();
                tMap.putAll(tempMap);
                list.add(tMap);
                tempMap.put("group_id",group_id);
                tempMap.put("saled_array",list);
                tempMap.put("ep_id",params.get("ep_id"));
                result=  hotelPlanSaleService.productGroupUp(tempMap);//  分组上架酒店产品
            }
        }
        if(!map.get("scenery").isEmpty()){
            result= productService.productBatchOnSaleToGroup(map.get("scenery"));
        }
        return result;
    }

    private Map initGroupPlanSalesParams(Map params) {
        Map<String,List<Map<String, Object>>> map = new HashMap<String,List<Map<String, Object>>>();
        List<Map<String, Object>> scenery = new ArrayList<Map<String, Object>> ();
        List<Map<String, Object>> hotel = new ArrayList<Map<String, Object>> ();
        List<Map<String, Object>> onSales = (List<Map<String, Object>>) params.get("saled_array");
        for (Map<String, Object> planSale : onSales) {
            planSale.put("sale_ep_id", params.get("ep_id"));
            planSale.put("group_id", params.get("group_id"));
             Integer type = CommonUtil.objectParseInteger(planSale.get("type"))  ;
            if(ProductConstants.ProductType.SCENERY-type==0){
                scenery.add(planSale);
            }else{// 酒店与线路一致 使用 batch_id 来操作
                hotel.add(planSale);
            }
            //planSale.put("batch_id",params.get("batch_id"));
            // planSale内有product_sub_id
            // planSale内有batch_id
            // planSale内有price
            // planSale内有price_type
            // planSale内有price_pixed
            // planSale内有price_percent
        }
        map.put("scenery",scenery);
        map.put("hotel",hotel);
        return map;
    }

    @RequestMapping(value = "sale/ep/creator", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubDistributionCreatorEp(@RequestBody Map params) {
       return productService.productOnSaleBatch(updateEpOnsalesParams(params));
    }

    @RequestMapping(value = "salp/lst")
    @ResponseBody
    public Result productSubPriceAndStock(@RequestParam("ep_id") Integer epId, @RequestParam("code") Long productSubCode, @RequestParam("start_date") Long startDate, @RequestParam("end_date") Long endDate) {
        return productSalesPlanService.searchTicketRealTimePriceAndStock(epId, productSubCode, startDate, endDate);
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
        return productService.productOnSaleProductBatch(initPlanSalesParams(CommonUtil.objectParseInteger(params.get("platform_ep_id")), CommonUtil.objectParseInteger(params.get("ep_id")),  (List<Map<String, Object>>) params.get("saled_array")));
    }

    @RequestMapping(value = "on_sale/products/ep", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubsDistributionEp(@RequestBody Map params) {
        return productService.productOnSaleProductBatch(initPlanSalesParams(CommonUtil.objectParseInteger(params.get("sub_ep_id")), CommonUtil.objectParseInteger(params.get("ep_id")),  (List<Map<String, Object>>) params.get("saled_array")));
    }

    private List<Map<String, Object>> initPlanSalesParams(Integer epId, Integer saleEpId, List<Map<String, Object>> saledArray) {
        Map<String, Object> ep = epService.selectId(epId).get();
        List<Map<String, Object>> onSales = saledArray;
        for (Map<String, Object> planSale : onSales) {
            planSale.put("sale_ep_id", saleEpId);
            planSale.put("ep_id", ep.get("id"));
            planSale.put("name", ep.get("name"));
            //planSale.put("batch_id",ep.get("batch_id"));
            // planSale内有product_sub_id
            // planSale内有batch_id
            // planSale内有price
            // planSale内有price_type
            // planSale内有price_pixed
            // planSale内有price_percent
        }
        return onSales;
    }

//    private List<Map<String, Object>> initPlanOffSalesParams(Map params) {
    private List<Map<String, Object>> initPlanOffSalesParams(List<String> batches, Integer saleEpId, Integer epId) {
//        List<String> batches = (List<String>) params.get("off_sale_array");
        List<Map<String, Object>> offSales = new ArrayList<>();
        for (String batch_id : batches) {
            Map<String, Object> ep = new HashMap<>();
//            ep.put("sale_ep_id", params.get("ep_id"));
            ep.put("sale_ep_id", saleEpId);
//            ep.put("ep_id", params.get("platform_ep_id"));
            ep.put("ep_id", epId);
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
        List<String> batchIds = (List<String>) params.get("off_sale_array");
        return productService.productOffSaleProductBatch(initPlanOffSalesParams(batchIds, CommonUtil.objectParseInteger(params.get("ep_id")), CommonUtil.objectParseInteger(params.get("platform_ep_id"))));
    }

    @RequestMapping(value = "off_sale/products/ep", method = RequestMethod.POST)
    @ResponseBody
    public Result productSubsOffDistributionEp(@RequestBody Map params) {
        List<String> batchIds = (List<String>) params.get("off_sale_array");
        return productService.productOffSaleProductBatch(initPlanOffSalesParams(batchIds, CommonUtil.objectParseInteger(params.get("ep_id")), CommonUtil.objectParseInteger(params.get("sub_ep_id"))));
    }

    private List<Map> updateEpOnsalesParams(Map params){
        Integer ep_id= epService.selectCreatorEpId(CommonUtil.objectParseInteger(params.get("ep_id"))).get();//获取上级id
        if(ep_id==null){
            return null;
        }
        Result<Map<String,Object>> ep = epService.selectId(ep_id);//获取企业信息
        if(ep == null || ep.get() == null) throw new ApiException();
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
        map.put("product_type",params.get("product_type"));
        map.put("batch_id",params.get("batch_id"));
        list.add(map);
        return list;
    }

    private List<Map> initEpOnsalesParams (Map params) {
        List<Map> onSalesEps = (List<Map>) params.get("saled_array");
        for (Map ep : onSalesEps) {
            ep.put("product_sub_id", params.get("product_sub_id"));
            ep.put("sale_ep_id", params.get("ep_id"));
            ep.put("batch_id",params.get("batch_id"));
            ep.put("product_type",params.get("product_type"));
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
     * 对多组下架单个产品批次
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
     * 组对多产品批次下架
     * @param params
     * @return
     */
    @RequestMapping("off_sale_batch/group")
    @ResponseBody
    public Result productSubBatchOffSaleGroup(@RequestBody Map params) {
        return productService.productBatchOffSaleGroup(params);
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
    public Result<Paginator<ProductPlanInfo>> searchPlanCalendar (Integer ep_id,@RequestParam("product_sub_id") Integer productSubId, @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate, @RequestParam("record_start") Integer recordStart, @RequestParam("record_count") Integer recordCount ) {
        return productSalesPlanService.searchPlanCalendar(ep_id,DateFormatUtils.converToDateTime(startDate), DateFormatUtils.converToDateTime(endDate), productSubId, recordStart, recordCount);
    }

    /**
     * 查询产品销售日历
     * @param productSubCode
     * @param startDate
     * @param endDate
     * @param recordStart
     * @param recordCount
     * @return  //
     */
    @RequestMapping(value = "sale/calendar")
    @ResponseBody
    public Result<Paginator<Map<String, Object>>> searchProductPlanCalendar (@RequestParam("ep_id") Integer epId, @RequestParam("product_sub_code") Long productSubCode, @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate, @RequestParam("record_start") Integer recordStart, @RequestParam("record_count") Integer recordCount ) {
        return productSalesPlanService.searchPlanCalendarByCode(epId, DateFormatUtils.converToDateTime(startDate), DateFormatUtils.converToDateTime(endDate), productSubCode, recordStart, recordCount);
    }

    @RequestMapping("can_sale/qunar/sub/list")
    @ResponseBody
    public Result<?> selectCanSaleSubByQunar(@RequestParam Integer ep_id, Long[] code, @RequestParam(defaultValue = "1") Integer calendar, @RequestParam(defaultValue = "0") Integer record_start, @RequestParam(defaultValue = "20") Integer record_count) {
        return productSalesPlanService.selectCanSaleSubAndCalendarByQunar(ep_id, code == null || code.length == 0 ? null : Arrays.asList(code), calendar == 1, record_start, record_count);
    }
}
