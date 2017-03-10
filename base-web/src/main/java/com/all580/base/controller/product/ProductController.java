package com.all580.base.controller.product;

import com.all580.base.manager.ProductValidateManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.*;
import com.all580.product.api.service.ProductDistributionRPCService;
import com.all580.product.api.service.ProductRPCService;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.all580.voucher.api.service.third.ThirdProductService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.Paginator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.*;

/**
 * 产品，子产品，计划批次接口网关
 */
@Controller
@RequestMapping(value = "api/product")
public class ProductController extends BaseController {

    @Resource
    ProductValidateManager productValidateManager;

    @Resource
    ProductSalesPlanRPCService productSalesPlanRPCService;

    @Resource
    ProductRPCService productService;

    @Resource
    ProductDistributionRPCService productDistributionService;

    @Resource
    ThirdProductService thirdProductService;


    /**
     * 查询景区子产品
     * @param    list
     * @return
     */
    @RequestMapping(value = "selectIds", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectIds(Integer [] list,Integer record_start,Integer record_count){
        return productService.selectIds(Arrays.asList(list), record_start,record_count);
    }
    /**
     * 修改景区子主产品
     * @param  map  主产品id
     * @return
     */
    @RequestMapping(value = "sub/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateProductSubBatch(@RequestBody Map<String,Object> map){

        return productService.updateProductSubBatch(map);
    }

    /**
     * 查询景区子产品
     * @param  productSubId  子产品id
     * @return
     */
    @RequestMapping(value = "sub", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectProductSubBatch(@RequestParam("product_sub_id") Integer productSubId, @RequestParam("ep_id") Integer epId){
        return productService.selectProductSubBatch(productSubId, epId);
    }

    /**
     * 查询景区子产品
     * @param  productSubCode 子产品code
     * @return
     */
    @RequestMapping(value = "sub_code", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectProductSubBatchByCode (@RequestParam("code") Long productSubCode, @RequestParam("ep_id") Integer epId){
        return productService.selectProductSubBatchByCode(productSubCode, epId);
    }

    /**
     * 查询景区主产品
     * @param  productId  主产品id
     * @return
     */
    @RequestMapping(value = "scenery", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectSceneryProduct(@RequestParam("product_id") Integer productId){
        return productService.findByProductId(productId);
    }

    /**
     * 修改主产品信息
     * @param params
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateSceneryProduct(@RequestBody Map params) {
        return productService.updateProductScenery(params);
    }
    /**
     * 添加景区主产品
     * @param params
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addSceneryProduct(@RequestBody Map params) {
        // TODO:验证入参
        switch (Integer.valueOf(params.get("type").toString())) {
            case ProductConstants.ProductType.SCENERY:
                ProductSceneryInfo productSceneryInfo = initProductScenery((Map) params.get("props"));
                Object phone = params.get("phone");
                productService.addSceneryProduct(CommonUtil.objectParseString(params.get("ma_name")),CommonUtil.objectParseInteger(params.get("ma_conf_id")),params.get("name").toString(),
                        Integer.valueOf(params.get("ep_id").toString()),
                        phone == null ? null : phone.toString(),
                        productSceneryInfo);
                break;
            default: return new Result<>(false, "产品类型不匹配");
        }
        return new Result<>(true);
    }

    private ProductSceneryInfo initProductScenery(Map props) {
        ProductSceneryInfo productSceneryInfo = new ProductSceneryInfo();
        productSceneryInfo.setProvince(Integer.valueOf(props.get("province").toString()));
        productSceneryInfo.setCity(Integer.valueOf(props.get("city").toString()));
        productSceneryInfo.setArea(Integer.valueOf(props.get("area").toString()));
        productSceneryInfo.setPcastr(props.get("pcastr").toString());
        productSceneryInfo.setAddress(props.get("address").toString());
        productSceneryInfo.setBlurb(props.get("blurb").toString());
        productSceneryInfo.setBusiness_time(props.get("business_time").toString());
        productSceneryInfo.setTel(props.get("tel").toString());
        productSceneryInfo.setMap(props.get("map").toString());
        productSceneryInfo.setImgs(JsonUtils.toJson(props.get("imgs")));
        productSceneryInfo.setLevel(Integer.valueOf(props.get("level").toString()));
        productSceneryInfo.setTransit_line(props.get("transit_lines").toString());
        productSceneryInfo.setType(JsonUtils.toJson(props.get("type")));
        return productSceneryInfo;
    }

    @RequestMapping(value = "sub/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addScenerySubProduct(@RequestBody Map params) {
        return productService.addScenerySubProduct(intSubProduct(params));
    }

    private SubProductInfo intSubProduct(Map params) {
        SubProductInfo subProductInfo = new SubProductInfo();
        subProductInfo.setEp_id(CommonUtil.objectParseInteger(params.get("ep_id")));
        subProductInfo.setBooking_day_limit(CommonUtil.objectParseInteger(params.get("booking_day_limit")));
        subProductInfo.setBooking_notes(CommonUtil.objectParseString(params.get("booking_notes")));
        subProductInfo.setBooking_limit(CommonUtil.objectParseInteger(params.get("booking_limit")));
        subProductInfo.setBooking_time_limit(CommonUtil.objectParseString(params.get("booking_time_limit")));
        subProductInfo.setBuy_start_date(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("buy_start_date"))));
        subProductInfo.setBuy_end_date(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("buy_end_date"))));
        subProductInfo.setDescription(CommonUtil.objectParseString(params.get("description")));
        subProductInfo.setDisable_date(JsonUtils.toJson(params.get("disable_date")));
        subProductInfo.setDisable_week(JsonUtils.toJson(params.get("disable_week")));
        subProductInfo.setEffective_day(CommonUtil.objectParseInteger(params.get("effective_day")));
        subProductInfo.setEffective_end_date(CommonUtil.objectParseString(params.get("effective_end_date")));
        subProductInfo.setEffective_start_date(CommonUtil.objectParseString(params.get("effective_start_date")));
        subProductInfo.setEffective_type(CommonUtil.objectParseInteger(params.get("effective_type")));
        subProductInfo.setImg(JsonUtils.toJson(params.get("img")));
        subProductInfo.setEp_ma_id(CommonUtil.objectParseInteger(params.get("ep_ma_id")));
        subProductInfo.setMa_product_id(CommonUtil.objectParseString(params.get("ma_product_id")));
        subProductInfo.setMarket_price(CommonUtil.objectParseInteger(params.get("market_price")));
        subProductInfo.setMax_buy_quantity(CommonUtil.objectParseInteger(params.get("max_buy_quantity")));
        subProductInfo.setMin_buy_quantity(CommonUtil.objectParseInteger(params.get("min_buy_quantity")));
        subProductInfo.setMin_sell_price(CommonUtil.objectParseInteger(params.get("min_sell_price")));
        subProductInfo.setName(CommonUtil.objectParseString(params.get("name")));
        subProductInfo.setPay_type(CommonUtil.objectParseInteger(params.get("pay_type")));
        subProductInfo.setProduct_id(CommonUtil.objectParseInteger(params.get("product_id")));
        subProductInfo.setProduct_name(CommonUtil.objectParseString(params.get("product_name")));
        subProductInfo.setReal_name(CommonUtil.objectParseInteger(params.get("real_name")));
        subProductInfo.setRequire_sid(CommonUtil.objectParseInteger(params.get("require_sid")));
        subProductInfo.setSale_quantity(CommonUtil.objectParseInteger(params.get("sale_quantity")));
//        subProductInfo.setSaler_refund_rule(CommonUtil.objectParseInteger(params.get("saler_refund_rule")));
        subProductInfo.setCust_refund_rule(CommonUtil.objectParseInteger(params.get("saler_refund_rule")));
        subProductInfo.setSale_rule_type(CommonUtil.objectParseInteger(params.get("sale_rule_type")));
        subProductInfo.setSettle_price(CommonUtil.objectParseInteger(params.get("settle_price")));
        subProductInfo.setSid_day_count(CommonUtil.objectParseInteger(params.get("sid_day_count")));
        subProductInfo.setSid_day_quantity(CommonUtil.objectParseInteger(params.get("sid_day_quantity")));
        subProductInfo.setStatus(CommonUtil.objectParseInteger(params.get("status")));
        subProductInfo.setStock_limit(CommonUtil.objectParseInteger(params.get("stock_limit")));
        subProductInfo.setTicket_dict(CommonUtil.objectParseInteger(params.get("ticket_dict")));
        subProductInfo.setTicket_flag(CommonUtil.objectParseInteger(params.get("ticket_flag")));
        subProductInfo.setTicket_msg(CommonUtil.objectParseString(params.get("ticket_msg")));
        subProductInfo.setTicket_flag(CommonUtil.objectParseInteger(params.get("ticket_flag")));
        subProductInfo.setTicket_type(CommonUtil.objectParseInteger(params.get("ticket_type")));
        subProductInfo.setTotal_stock(CommonUtil.objectParseInteger(params.get("total_stock")));
        subProductInfo.setUse_hours_limit(CommonUtil.objectParseInteger(params.get("use_hours_limit")));
        subProductInfo.setUse_notes(CommonUtil.objectParseString(params.get("use_notes")));
        subProductInfo.setVoucher_msg(CommonUtil.objectParseString(params.get("voucher_msg")));
        subProductInfo.setRefund_audit(CommonUtil.objectParseInteger(params.get("refund_audit")));
        subProductInfo.setRefund_money_audit(CommonUtil.objectParseInteger(params.get("refund_money_audit")));
        subProductInfo.setLow_use_quantity(CommonUtil.objectParseInteger(params.get("low_use_quantity")));
        return subProductInfo;
    }

    /**
     * 产品分销列表
     * @param epId
     * @param productName
     * @param order
     * @return
     */
    @RequestMapping(value = "sale/list")
    @ResponseBody
    public Result<Paginator<ProductAndSubInfo>> searchCanSaleList(@RequestParam("ep_id") Integer epId,
                                       @RequestParam("product_name") String productName,
                                       @RequestParam("is_supplier") Integer isSupplier,
                                       @RequestParam("order_str") Integer order,
                                       @RequestParam("record_start") Integer start,
                                       @RequestParam("record_count") Integer count,
                                                                  Integer type) {

        String orderStr = null;
        if(type==null){
            type=ProductConstants.ProductType.SCENERY;
        }
        if (order != null) {
            switch (CanSaleOrderState.getCanSaleOrderSate(order)) {
                case CREATE_TIME_ASC: orderStr = CanSaleOrderState.CREATE_TIME_ASC.getValue(); break;
                case CREATE_TIME_DESC: orderStr = CanSaleOrderState.CREATE_TIME_DESC.getValue(); break;
                case PRODUCT_NAME_ASC: orderStr = CanSaleOrderState.PRODUCT_NAME_ASC.getValue(); break;
                case PRODUCT_NAME_DESC: orderStr = CanSaleOrderState.PRODUCT_NAME_DESC.getValue(); break;
            }
        }
        Result<Paginator<ProductAndSubInfo>> result = productService.searchSubProductListByProductName(epId, productName, isSupplier, orderStr, start, count,type);
        return result;
    }

    /**
     * 产品分销列表
     * @param epId
     * @param productName
     * @param order
     * @return
     */
    @RequestMapping(value = "sale/list/hotel")
    @ResponseBody
    public Result<Paginator<ProductAndSubInfo>> searchCanSaleListHotel(@RequestParam("ep_id") Integer epId,
                                                                  @RequestParam("product_name") String productName,
                                                                  @RequestParam("is_supplier") Integer isSupplier,
                                                                  @RequestParam("order_str") Integer order,
                                                                  @RequestParam("record_start") Integer start,
                                                                  @RequestParam("record_count") Integer count
                                                                 ) {

        String orderStr = null;

          Integer  type=ProductConstants.ProductType.HOTEL;

        if (order != null) {
            switch (CanSaleOrderState.getCanSaleOrderSate(order)) {
                case CREATE_TIME_ASC: orderStr = CanSaleOrderState.CREATE_TIME_ASC.getValue(); break;
                case CREATE_TIME_DESC: orderStr = CanSaleOrderState.CREATE_TIME_DESC.getValue(); break;
                case PRODUCT_NAME_ASC: orderStr = CanSaleOrderState.PRODUCT_NAME_ASC.getValue(); break;
                case PRODUCT_NAME_DESC: orderStr = CanSaleOrderState.PRODUCT_NAME_DESC.getValue(); break;
            }
        }
        Result result = productService.searchSubProductListByProductNameHotel(epId, productName, isSupplier, orderStr, start, count,type);
        return result;
    }

    @RequestMapping("sale/ep/list")
    @ResponseBody
    public Result<List<DistributionEpInfo>> searchDistributionEpInfo(
            @RequestParam("ep_id") Integer epId,
            @RequestParam("product_sub_id") Integer productSubId,
            @RequestParam("status") Integer distributionStatus,String ep_name) {
        switch (CommonUtil.objectParseInteger(distributionStatus)) {
            case ProductConstants.ProductDistributionState.HAD_DISTRIBUTE:
                return productDistributionService.selectAlreadyDistributionEp(productSubId, epId,ep_name);
            case ProductConstants.ProductDistributionState.NOT_DISTRIBUTE:
                return productDistributionService.selectNoDistributionEp(epId, productSubId,ep_name);
        }
        return new Result<>(false, "状态参数错误");
    }

    @RequestMapping("sale/group/list")
    @ResponseBody
    public Result<Paginator<DistributionGroupInfo>> searchDistributionGroupInfo (
        @RequestParam("ep_id") Integer epId,
        @RequestParam("product_sub_id") Integer productSubId,
        @RequestParam("status") Integer distributionStatus,
        @RequestParam("record_start") Integer start,
        @RequestParam("record_count") Integer count
    ) {
        switch (CommonUtil.objectParseInteger(distributionStatus)) {
            case ProductConstants.ProductDistributionState.HAD_DISTRIBUTE:
                return productDistributionService.searchAlreadyDistributionGroup(epId, productSubId, start, count);
            case ProductConstants.ProductDistributionState.NOT_DISTRIBUTE:
                return productDistributionService.searchNoDistributionGroup(epId, productSubId, start, count);
        }
        return new Result<>(false, "状态参数错误");
    }



    /**
     * 跨平台分销
     * @param epId
     * @param platformEpId
     * @param distributionStatus
     * @return
     */
    @RequestMapping(value = "platform/distribution")
    @ResponseBody
    public Result<List<Map<String, Object>>> searchSelfAndOtherProduct(
            @RequestParam("ep_id") Integer epId,
            @RequestParam("platform_ep_id") Integer platformEpId,
            @RequestParam("status") Integer distributionStatus) {
        switch (CommonUtil.objectParseInteger(distributionStatus)) {
            case ProductConstants.ProductDistributionState.HAD_DISTRIBUTE:
                return productDistributionService.searchAlreadyDistributionProduct(platformEpId, epId);
            case ProductConstants.ProductDistributionState.NOT_DISTRIBUTE:
                return productDistributionService.searchNotDistributionProduct(platformEpId, epId);
        }
        return new Result<>(false, "状态参数错误");
    }

    @RequestMapping(value = "booking/view")
    @ResponseBody
    public Result<Map> searchProductBookingView(@RequestParam("ep_id") Integer epId, @RequestParam("id") Integer productSubId) {
        return productService.searchProductBookingView(epId, productSubId);
    }

    @RequestMapping(value = "booking/view_code")
    @ResponseBody
    public Result<Map> searchProductBookingViewByCode(@RequestParam("ep_id") Integer epId, @RequestParam("code") Long productSubCode) {
        return productService.searchProductBookingViewByCode(epId, productSubCode);
    }
    
    /**
     * 自供产品
     * @param epId
     * @return
     */
    @RequestMapping(value = "self/list")
    @ResponseBody
    public Result<Map<String,Object>> searchSelfProviderProduct(@RequestParam("ep_id") Integer epId,
                                                                           @RequestParam("product_name") String productName,
                                                                           @RequestParam("product_sub_name") String productSubName,
                                                                           @RequestParam("record_start") Integer recordStart,
                                                                           @RequestParam("record_count") Integer recordCount) {
       // TODO: 验证入参
        return productService.searchSelfProviderProductListMap(epId, productName, productSubName, recordStart, recordCount);
       //return productService.searchSelfProviderProductList(epId, productName, productSubName, recordStart, recordCount);
    }

    /**
     * 他供产品
     * @param epId
     * @return
     */
    @RequestMapping(value = "other/list")
    @ResponseBody
    public Result<?> searchOtherProdiverProduct(@RequestParam("ep_id") Integer epId,
                                                @RequestParam(value = "product_name", required = false) String productName,
                                                @RequestParam(value = "supplier_id", required = false) Integer supplierId,
                                                @RequestParam(value = "product_type", required = false) Integer productType,
                                                @RequestParam(value = "sale", required = false) Integer sale,
                                                @RequestParam("record_start") Integer recordStart,
                                                @RequestParam("record_count") Integer recordCount) {
        return productService.searchOtherProviderProductList(epId, productName, supplierId, productType, sale, recordStart, recordCount);
    }

    @RequestMapping(value = "other/price")
    @ResponseBody
    public Result<Map> searchPlanSaleAllPrice(@RequestParam("ep_id") Integer epId, @RequestParam("batch_id") Integer batchId) {
        return productService.searchPlanSaleAllPrice(epId, batchId);
    }

    @RequestMapping(value = "other/profit")
    @ResponseBody
    public Result<Map> searchPlanSaleAllPriceAndProfit(@RequestParam("plan_sale_id") Integer id) {
        return productService.searchPlanSaleAllPriceAndProfit(id);
    }

    /**
     * 平台商查询平台内底层供应商提供的产品退订退款审核配置
     * @param epId
     * @param supplierId
     * @param productName
     * @param productSubName
     * @param productType
     * @return
     */
    @RequestMapping("audit/list")
    @ResponseBody
    public Result<Set<Map>> searchProductAuditInfo(@RequestParam("ep_id") Integer epId, @RequestParam(value = "supplier_id", required = false) Integer supplierId, @RequestParam(value = "product_name", required = false) String productName, @RequestParam(value = "product_sub_name", required = false) String productSubName, @RequestParam(value = "product_type", required = false) Integer productType) {
        return productService.searchProductAuditSettings(epId, supplierId, productName, productSubName, productType);
    }

    /**
     * 平台商修改平台内底层供应商提供的产品退订退款审核配置
     * @param params
     * @return
     */
    @RequestMapping(value = "audit/update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateAuditSettings(@RequestBody Map params) {
        List<Map> auditSettings = (List<Map>) params.get("audit_settings");
        if (auditSettings == null || auditSettings.isEmpty()) throw new ApiException("参数缺失");
        return productService.updateAuditSettingsBatch(auditSettings);
    }

    /**
     * 已分销产品列表
     * @param epId
     * @param subEpId
     * @return
     */
    @RequestMapping(value = "ep_distributed/list")
    @ResponseBody
    public Result<List<Map<String, Object>>> searchDistributedProduct(
            @RequestParam("ep_id") Integer epId,
            @RequestParam("sub_ep_id") Integer subEpId) {
        return productDistributionService.searchAlreadyDistributionProductSubEp(subEpId, epId);
    }

    /**
     * 未分销产品列表
     * @param epId
     * @param subEpId
     * @return
     */
    @RequestMapping(value = "ep_distribute/list")
    @ResponseBody
    public Result<List<Map<String, Object>>> searchDistributeProduct(
            @RequestParam("ep_id") Integer epId,
            @RequestParam("sub_ep_id") Integer subEpId) {
        return productDistributionService.searchNotDistributionProductSubEp(subEpId, epId);
    }

    /**
     * 查询对组已分销产品信息
     * @param epId
     * @param groupId
     * @return
     */
    @RequestMapping(value = "group_distributed/list")
    @ResponseBody
    public Result<List<Map<String, Object>>> searchGroupDistributedProduct(
            @RequestParam("ep_id") Integer epId,
            @RequestParam("group_id") Integer groupId) {
        return productDistributionService.searchAlreadyDistributionProductSubGroup(epId, groupId);
    }

    /**
     * 查询对组未分销产品信息
     * @param epId
     * @param groupId
     * @return
     */
    @RequestMapping(value = "group_distribute/list")
    @ResponseBody
    public Result<List<Map<String, Object>>> searchGroupDistributeProduct(
            @RequestParam("ep_id") Integer epId,
            @RequestParam("group_id") Integer groupId) {
        return productDistributionService.searchNotDistributionProductSubGroup(epId, groupId);
    }

    /**
     * 指定景点信息查询
     * @param epId
     * @param productId
     * @param ticketFlag
     * @return
     */
    @RequestMapping("sales/view")
    @ResponseBody
    public Result<Map<String, Object>> searchProductSaleView(@RequestParam("ep_id") Integer epId, @RequestParam("product_id") Integer productId, @RequestParam("ticket_flag") Integer ticketFlag) {
        return productService.searchProductSaleView(epId, productId, ticketFlag);
    }

    /**
     * 查询单个三方供应平台景点信息
     * @param epId
     * @param maConfId
     * @return
     */
    @RequestMapping("other/park_list")
    @ResponseBody
    public Result<Paginator<Map<String, Object>>> searchOtherProviderParkList(@RequestParam("ep_id") Integer epId, @RequestParam("ma_conf_id") Integer maConfId, @RequestParam("record_start") Integer start, @RequestParam("record_count") Integer count) {
        return thirdProductService.searchParkList(epId, maConfId, start, count);
    }

    /**
     * 查询单个三方供应平台门票信息
     * @param epId
     * @param maConfId
     * @param parkId
     * @param start
     * @param count
     * @return
     */
    @RequestMapping("other/ticket_list")
    @ResponseBody
    public Result<Paginator<Map<String, Object>>> searchOtherProviderTicketList(@RequestParam("ep_id") Integer epId, @RequestParam("ma_conf_id") Integer maConfId, @RequestParam("park_id") Integer parkId, @RequestParam("record_start") Integer start, @RequestParam("record_count") Integer count) {
        return thirdProductService.searchTicketList(epId, maConfId, parkId, start, count);
    }

    /**
     * 查询所有第三方供应平台门票信息
     * @param epId
     * @param parkName
     * @param ticketName
     * @param start
     * @param count
     * @return
     */
    @RequestMapping("other/ticket_all")
    @ResponseBody
    public Result<Map<String, Object>> searchAllProviderTicketList(@RequestParam("ep_id") Integer epId, @RequestParam(value = "park_name", required = false) String parkName, @RequestParam(value = "ticket_name", required = false) String ticketName, @RequestParam("record_start") Integer start, @RequestParam("record_count") Integer count) {
        return productService.searchThirdProducts(epId, parkName, ticketName, start, count);
    }
}
