package com.all580.base.controller.product;

import com.all580.base.manager.ProductValidateManager;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

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
//        String productType = params.get("type").toString();
//        String productName = params.get("name").toString();
//        String productProps = params.get("props").toString();
//        ParamsMapValidate.validate(params, );
        System.out.println("add success!");
        System.out.println(params.get("type"));
        return new Result<>(true);
    }

    @RequestMapping(value = "sub/add")
    @ResponseBody
    public Result<?> addScenerySubProduct(@RequestBody Map params) {
        System.out.println("add sub success!");
        System.out.println(params.get("type"));
        return new Result<>(true);
    }
}
