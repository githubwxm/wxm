package com.all580.base.controller.product;

import com.all580.base.manager.ProductValidateManager;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addSceneryProduct(@RequestBody Map params) {

        return new Result<>(true);
    }

    @RequestMapping(value = "sub/add")
    @ResponseBody
    public Result<?> addScenerySubProduct(@RequestBody Map params) {
        return new Result<>(true);
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public Result<?> updateSceneryProduct(@RequestBody Map params) {
        return null;
    }

    @RequestMapping(value = "sub/update")
    @ResponseBody
    public Result<?> updateScenerySubProduct(@RequestBody Map params) {
        return null;
    }

    /**
     * 自供产品
     * @param epId
     * @return
     */
    @RequestMapping(value = "self/list")
    @ResponseBody
    public Result<?> searchSelfProviderProduct(@RequestParam Integer epId) {
        return null;
    }

    /**
     * 他供产品
     * @param epId
     * @return
     */
    @RequestMapping(value = "other/list")
    @ResponseBody
    public Result<?> searchOtherProdiverProduct(@RequestParam Integer epId) {
        return null;
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
    public Result<?> searchProductOnSaleList(@RequestParam Integer epId, @RequestParam String productName, @RequestParam Integer order) {
        return null;
    }


}
