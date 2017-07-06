package com.all580.base.controller.product;

import com.all580.base.manager.PackageValidateManager;
import com.all580.product.api.service.PackageService;
import com.all580.product.api.service.PackageSubService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description: 套票
 * @date 17-7-6 下午3:11
 */
@Controller
@RequestMapping("api/package")
public class PackageController extends BaseController {
    @Autowired
    private PackageService packageService;
    @Autowired
    private PackageSubService packageSubService;

    @Autowired
    private PackageValidateManager packageValidateManager;

    @RequestMapping(value = "product/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> add(@RequestBody Map params) {
        ParamsMapValidate.validate(params, packageValidateManager.addValidate());
        return packageService.add(params);
    }

    @RequestMapping(value = "product/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> update(@RequestBody Map params) {
        ParamsMapValidate.validate(params, packageValidateManager.updateValidate());
        return packageService.update(params);
    }

    @RequestMapping(value = "product/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> delete(@RequestBody Map params) {
        ParamsMapValidate.validate(params, packageValidateManager.deleteValidate());
        return packageService.delete(params);
    }

    @RequestMapping(value = "sub/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addSub(@RequestBody Map params) {
        ParamsMapValidate.validate(params, packageValidateManager.addValidate());
        return packageSubService.add(params);
    }

    @RequestMapping(value = "sub/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateSub(@RequestBody Map params) {
        ParamsMapValidate.validate(params, packageValidateManager.updateSubValidate());
        return packageSubService.update(params);
    }

    @RequestMapping(value = "sub/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteSub(@RequestBody Map params) {
        ParamsMapValidate.validate(params, packageValidateManager.deleteValidate());
        return packageSubService.delete(params);
    }
}
