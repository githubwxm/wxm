package com.all580.base.controller.product;

import com.all580.product.api.model.AddProductPlanParams;
import com.all580.product.api.model.CanSaleSubProductInfo;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.StringUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.Paginator;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
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
    /**
     * 增加销售计划
     * @param params
     * @return
     */
    @RequestMapping(value = "config")
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
    @RequestMapping(value = "update")
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
    public Result<Paginator<CanSaleSubProductInfo>> canSaleSubProductList(
            @RequestParam("ep_id") Integer epId,
            @RequestParam("productName") String productName,
            @RequestParam("productSubName") String productSubName,
            @RequestParam("province") Integer province,
            @RequestParam("city") Integer city,
            @RequestParam("area") Integer area,
            @RequestParam("ticketFlag") Integer ticketFlag,
            @RequestParam("payType") Integer payType,
            @RequestParam("record_start") Integer start,
            @RequestParam("record_count") Integer count) {
        return productSalesPlanService.selectCanSaleSubProduct(epId,productName,productSubName,province,city,area,ticketFlag,payType,start,count);
    }
}
