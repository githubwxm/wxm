package com.all580.base.controller.product;

import com.all580.base.manager.ProductValidateManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.*;
import com.all580.product.api.service.ProductDistributionRPCService;
import com.all580.product.api.service.ProductRPCService;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.Paginator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    /**
     * 查询景区主产品
     * @param  productId  主产品id
     * @return
     */
    @RequestMapping(value = "findByProductId", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectSceneryProduct(@RequestParam("productId") Integer productId){
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
                productService.addSceneryProduct(params.get("name").toString(), Integer.valueOf(params.get("ep_id").toString()), productSceneryInfo);
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
        productSceneryInfo.setBusinessTime(props.get("business_time").toString());
        productSceneryInfo.setTel(props.get("tel").toString());
        productSceneryInfo.setMap(props.get("map").toString());
        productSceneryInfo.setImgs(JsonUtils.toJson(props.get("imgs")));
        productSceneryInfo.setLevel(Integer.valueOf(props.get("level").toString()));
        productSceneryInfo.setTransitLine(props.get("transit_lines").toString());
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
        subProductInfo.setEpId(CommonUtil.objectParseInteger(params.get("ep_id")));
        subProductInfo.setBookingDayLimit(CommonUtil.objectParseInteger(params.get("bookingDayLimit")));
        subProductInfo.setBookingNotes(CommonUtil.objectParseString(params.get("bookingNotes")));
        subProductInfo.setBookingLimit(CommonUtil.objectParseInteger(params.get("bookingLimit")));
        subProductInfo.setBookingTimeLimit(CommonUtil.objectParseInteger(params.get("bookingTimeLimit")));
        subProductInfo.setBuyStartDate(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("buyStartDate"))));
        subProductInfo.setBuyEndDate(DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("buyEndDate"))));
        subProductInfo.setDescription(CommonUtil.objectParseString(params.get("description")));
        subProductInfo.setDisableDate(CommonUtil.objectParseString(params.get("disableDate")));
        subProductInfo.setDisableWeek(CommonUtil.objectParseString(params.get("disableWeek")));
        subProductInfo.setEffectiveDay(CommonUtil.objectParseInteger(params.get("effectiveDay")));
        subProductInfo.setEffectiveEndDate(CommonUtil.objectParseString(params.get("effectiveEndDate")));
        subProductInfo.setEffectiveStartDate(CommonUtil.objectParseString(params.get("effectiveStartDate")));
        subProductInfo.setEffectiveType(CommonUtil.objectParseInteger(params.get("effectiveType")));
        subProductInfo.setImg(JsonUtils.toJson(params.get("img")));
        subProductInfo.setEpMaId(CommonUtil.objectParseInteger(params.get("epMaId")));
        subProductInfo.setMaProductId(CommonUtil.objectParseString(params.get("maProductId")));
        subProductInfo.setMarketPrice(CommonUtil.objectParseInteger(params.get("marketPrice")));
        subProductInfo.setMaxBuyQuantity(CommonUtil.objectParseInteger(params.get("maxBuyQuantity")));
        subProductInfo.setMinBuyQuantity(CommonUtil.objectParseInteger(params.get("minBuyQuantity")));
        subProductInfo.setMinSellPrice(CommonUtil.objectParseInteger(params.get("minSellPrice")));
        subProductInfo.setName(CommonUtil.objectParseString(params.get("name")));
        subProductInfo.setPayType(CommonUtil.objectParseInteger(params.get("payType")));
        subProductInfo.setProductId(CommonUtil.objectParseInteger(params.get("productId")));
        subProductInfo.setProductName(CommonUtil.objectParseString(params.get("productName")));
        subProductInfo.setRealName(CommonUtil.objectParseInteger(params.get("realName")));
        subProductInfo.setRequireSid(CommonUtil.objectParseInteger(params.get("requireSid")));
        subProductInfo.setSaleQuantity(CommonUtil.objectParseInteger(params.get("saleQuantity")));
//        subProductInfo.setSalerRefundRule(CommonUtil.objectParseInteger(params.get("salerRefundRule")));
        subProductInfo.setCustRefundRule(CommonUtil.objectParseInteger(params.get("salerRefundRule")));
        subProductInfo.setSaleRuleType(CommonUtil.objectParseInteger(params.get("saleRuleType")));
        subProductInfo.setSettlePrice(CommonUtil.objectParseInteger(params.get("settlePrice")));
        subProductInfo.setSidDayCount(CommonUtil.objectParseInteger(params.get("sidDayCount")));
        subProductInfo.setSidDayQuantity(CommonUtil.objectParseInteger(params.get("sidDayQuantity")));
        subProductInfo.setStatus(CommonUtil.objectParseInteger(params.get("status")));
        subProductInfo.setStockLimit(CommonUtil.objectParseInteger(params.get("stockLimit")));
        subProductInfo.setTicketDict(CommonUtil.objectParseInteger(params.get("ticketDict")));
        subProductInfo.setTicketFlag(CommonUtil.objectParseInteger(params.get("ticketFlag")));
        subProductInfo.setTicketMsg(CommonUtil.objectParseString(params.get("ticketMsg")));
        subProductInfo.setTicketFlag(CommonUtil.objectParseInteger(params.get("ticketFlag")));
        subProductInfo.setTicketType(CommonUtil.objectParseInteger(params.get("ticketType")));
        subProductInfo.setTotalStock(CommonUtil.objectParseInteger(params.get("totalStock")));
        subProductInfo.setUseHoursLimit(CommonUtil.objectParseInteger(params.get("useHoursLimit")));
        subProductInfo.setUseNotes(CommonUtil.objectParseString(params.get("useNotes")));
        subProductInfo.setVoucherMsg(CommonUtil.objectParseString(params.get("voucherMsg")));
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
                                       @RequestParam("productName") String productName,
                                       @RequestParam("orderStr") Integer order,
                                       @RequestParam("record_start") Integer start,
                                       @RequestParam("record_count") Integer count) {

        String orderStr = null;
        if (order != null) {
            switch (CanSaleOrderState.getCanSaleOrderSate(order)) {
                case CREATE_TIME_ASC: orderStr = CanSaleOrderState.CREATE_TIME_ASC.getValue(); break;
                case CREATE_TIME_DESC: orderStr = CanSaleOrderState.CREATE_TIME_DESC.getValue(); break;
                case PRODUCT_NAME_ASC: orderStr = CanSaleOrderState.PRODUCT_NAME_ASC.getValue(); break;
                case PRODUCT_NAME_DESC: orderStr = CanSaleOrderState.PRODUCT_NAME_DESC.getValue(); break;
            }
        }
        Result<Paginator<ProductAndSubInfo>> result = productService.searchSubProductListByProductName(epId, productName, orderStr, start, count);
        return result;
    }

    @RequestMapping("sale/ep/list")
    @ResponseBody
    public Result<List<DistributionEpInfo>> searchDistributionEpInfo(
            @RequestParam("ep_id") Integer epId,
            @RequestParam("productSubId") Integer productSubId,
            @RequestParam("status") Integer distributionStatus) {
        switch (CommonUtil.objectParseInteger(distributionStatus)) {
            case ProductConstants.ProductDistributionState.HAD_DISTRIBUTE:
                return productDistributionService.selectAlreadyDistributionEp(productSubId);
            case ProductConstants.ProductDistributionState.NOT_DISTRIBUTE:
                return productDistributionService.selectNoDistributionEp(epId, productSubId);
        }
        return new Result<>(false, "状态参数错误");
    }

    @RequestMapping("sale/group/list")
    @ResponseBody
    public Result<Paginator<DistributionGroupInfo>> searchDistributionGroupInfo (
        @RequestParam("ep_id") Integer epId,
        @RequestParam("productSubId") Integer productSubId,
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



    @RequestMapping(value = "sub/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateScenerySubProduct(@RequestBody Map params) {
        return null;
    }

    @RequestMapping(value = "booking/view")
    @ResponseBody
    public Result<Map> searchProductBookingView(@RequestParam("ep_id") Integer epId, @RequestParam("id") Integer productSubId) {
        return productService.searchProductBookingView(epId, productSubId);
    }

    /**
     * 自供产品
     * @param epId
     * @return
     */
    @RequestMapping(value = "self/list")
    @ResponseBody
    public Result<Paginator<ProductAndSubsInfo>> searchSelfProviderProduct(@RequestParam("ep_id") Integer epId,
                                                                           @RequestParam("product_name") String productName,
                                                                           @RequestParam("product_sub_name") String productSubName,
                                                                           @RequestParam("record_start") Integer recordStart,
                                                                           @RequestParam("record_count") Integer recordCount) {
       // TODO: 验证入参
       return productService.searchSelfProviderProductList(epId, productName, productSubName, recordStart, recordCount);
    }

    /**
     * 他供产品
     * @param epId
     * @return
     */
    @RequestMapping(value = "other/list")
    @ResponseBody
    public Result<?> searchOtherProdiverProduct(@RequestParam Integer epId,
                                                @RequestParam String productName,
                                                @RequestParam String supplierName,
                                                @RequestParam Integer sale,
                                                @RequestParam Integer recordStart,
                                                @RequestParam Integer recordCount) {
        return null;
    }

}
