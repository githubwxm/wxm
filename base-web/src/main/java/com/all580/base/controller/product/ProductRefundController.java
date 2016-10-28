package com.all580.base.controller.product;

import com.all580.product.api.model.ProductRefundRuleInfo;
import com.all580.product.api.service.ProductRefundRuleRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping("add")
    @ResponseBody
    public Result<?> addProductRefund (@RequestBody Map params) {
        return productRefundRuleService.addProductRefundRule(initProductRefundRuleInfo(params));
    }

    ProductRefundRuleInfo initProductRefundRuleInfo(Map params) {
        ProductRefundRuleInfo productRefundRuleInfo = new ProductRefundRuleInfo();
        productRefundRuleInfo.setEpId(CommonUtil.objectParseInteger(params.get("ep_id")));
        productRefundRuleInfo.setAll(CommonUtil.objectParseInteger(params.get("all")));
        productRefundRuleInfo.setName(CommonUtil.objectParseString(params.get("name")));
        productRefundRuleInfo.setProductType(CommonUtil.objectParseInteger(params.get("productType")));
        productRefundRuleInfo.setRule(CommonUtil.objectParseString(params.get("rule")));
//        productRefundRuleInfo.setStatus(CommonUtil.objectParseInteger(params.get("status")));
        productRefundRuleInfo.setType(CommonUtil.objectParseInteger(params.get("type")));
        return productRefundRuleInfo;
    }
}
