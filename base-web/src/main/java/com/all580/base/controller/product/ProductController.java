package com.all580.base.controller.product;

import com.all580.base.manager.ProductValidateManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.ProductAndSubsInfo;
import com.all580.product.api.model.ProductSceneryInfo;
import com.all580.product.api.service.ProductRPCService;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
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
    ProductRPCService productRPCService;

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
                productRPCService.addSceneryProduct(params.get("name").toString(), Integer.valueOf(params.get("epId").toString()), productSceneryInfo);
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
        productSceneryInfo.setBusinessTime(props.get("businessTime").toString());
        productSceneryInfo.setTel(props.get("tel").toString());
        productSceneryInfo.setMap(props.get("map").toString());
        productSceneryInfo.setImgs(JsonUtils.toJson(props.get("imgs")));
        productSceneryInfo.setLevel(Integer.valueOf(props.get("level").toString()));
        productSceneryInfo.setTransitLine(props.get("transitLines").toString());
        productSceneryInfo.setType(JsonUtils.toJson(props.get("type")));
        return productSceneryInfo;
    }

    @RequestMapping(value = "sub/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addScenerySubProduct(@RequestBody Map params) {
        return new Result<>(true);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateSceneryProduct(@RequestBody Map params) {
        return null;
    }

    @RequestMapping(value = "sub/update", method = RequestMethod.POST)
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
    public Result<Paginator<ProductAndSubsInfo>> searchSelfProviderProduct(@RequestParam Integer epId, @RequestParam String productName,
                                                                           @RequestParam String productSubName, @RequestParam Integer productType,
                                                                           @RequestParam Integer recordStart, @RequestParam Integer recordCount) {
       // TODO: 验证入参
       return productRPCService.searchSelfProviderProductList(epId, productName, productSubName, recordStart, recordCount);
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
