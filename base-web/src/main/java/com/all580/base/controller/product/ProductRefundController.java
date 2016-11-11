package com.all580.base.controller.product;

import com.all580.product.api.model.ProductRefundRuleInfo;
import com.all580.product.api.model.ProductRefundRuleParams;
import com.all580.product.api.service.ProductRefundRuleRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.Paginator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 退货规则
 */
@Controller
@RequestMapping("api/refund_rule")
public class ProductRefundController extends BaseController {

    @Resource
    ProductRefundRuleRPCService productRefundRuleService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addProductRefund (@RequestBody Map params) {
        return productRefundRuleService.addProductRefundRule(initProductRefundRuleInfo(params));
    }

    @RequestMapping(value = "info")
    @ResponseBody
    public Result searchRefundInfo (@RequestParam("refundId") Integer refundId) {
        return productRefundRuleService.searchProductRefundRule(refundId);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateProductRefund (@RequestBody Map params) {
        return productRefundRuleService.updateProductRefundRule(initUpdateParams(params));
    }

    private ProductRefundRuleInfo initUpdateParams(Map params) {
        ProductRefundRuleInfo info = new ProductRefundRuleInfo();
        info.setId(CommonUtil.objectParseInteger(params.get("refundId")));
        info.setName(CommonUtil.objectParseString(params.get("name")));
        info.setAll(CommonUtil.objectParseInteger(params.get("all")));
        info.setRule(JsonUtils.toJson(params.get("rule")));
        return info;
    }

    /**
     * 查询退货规则列表
     * @param epId
     * @param name
     * @param productType
     * @param start
     * @param count
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Result<Paginator<ProductRefundRuleInfo>> searchProductRefunds (@RequestParam("ep_id") Integer epId, @RequestParam("name") String name, @RequestParam("productType") Integer productType, @RequestParam("record_start") Integer start, @RequestParam("record_count") Integer count) {
        return productRefundRuleService.searchProductRefundRules(initParams(epId, name, productType, start, count));
    }

    private ProductRefundRuleParams initParams (Integer epId, String name, Integer productType, Integer start, Integer count) {
        ProductRefundRuleParams params = new ProductRefundRuleParams();
        params.setEpId(epId);
        params.setName(name);
        params.setProductType(productType);
        params.setRecordStart(start);
        params.setRecordCount(count);
        return params;
    }

    ProductRefundRuleInfo initProductRefundRuleInfo(Map params) {
        ProductRefundRuleInfo productRefundRuleInfo = new ProductRefundRuleInfo();
        productRefundRuleInfo.setEpId(CommonUtil.objectParseInteger(params.get("ep_id")));
        productRefundRuleInfo.setAll(CommonUtil.objectParseInteger(params.get("all")));
        productRefundRuleInfo.setName(CommonUtil.objectParseString(params.get("name")));
        productRefundRuleInfo.setProductType(CommonUtil.objectParseInteger(params.get("productType")));
        productRefundRuleInfo.setRule(JsonUtils.toJson(params.get("rule")));
//        productRefundRuleInfo.setStatus(CommonUtil.objectParseInteger(params.get("status")));
        productRefundRuleInfo.setType(CommonUtil.objectParseInteger(params.get("type")));
        return productRefundRuleInfo;
    }

}
