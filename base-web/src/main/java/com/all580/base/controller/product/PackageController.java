package com.all580.base.controller.product;

import com.all580.base.manager.PackageValidateManager;
import com.all580.base.util.Utils;
import com.all580.product.api.service.PackageService;
import com.all580.product.api.service.PackageSubService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
        ParamsMapValidate.validate(params, packageValidateManager.addSubValidate());
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

    @RequestMapping("own")
    @ResponseBody
    public Result<?> selectOwn(@RequestParam Integer ep_id,
                               String name, String sub_name,
                               Integer up_down, String start, String end,
                               @RequestParam(defaultValue = "0") Integer record_start,
                               @RequestParam(defaultValue = "20") Integer record_count) {
        Date[] dates = Utils.checkDateTime(start, end);
        return packageService.selectOwnPage(ep_id, name, sub_name, up_down, dates[0], dates[1], record_start, record_count);
    }

    @RequestMapping("pending/scenery")
    @ResponseBody
    public Result<?> selectPendingPackageForScenery(@RequestParam Integer ep_id,
                                                    String name, Integer province, Integer city,
                                                    @RequestParam(defaultValue = "0") Integer record_start,
                                                    @RequestParam(defaultValue = "20") Integer record_count) {
        return packageService.selectByPackageScenery(ep_id, name, province, city, record_start, record_count);
    }

    @RequestMapping("pending/hotel")
    @ResponseBody
    public Result<?> selectPendingPackageForHotel(@RequestParam Integer ep_id,
                                                    String name, Integer province, Integer city,
                                                    @RequestParam(defaultValue = "0") Integer record_start,
                                                    @RequestParam(defaultValue = "20") Integer record_count) {
        return packageService.selectByPackageHotel(ep_id, name, province, city, record_start, record_count);
    }

    @RequestMapping("view/main")
    @ResponseBody
    public Result<?> viewMain(@RequestParam Integer id) {
        return packageService.selectPackageInfo(id);
    }

    @RequestMapping("view/sub")
    @ResponseBody
    public Result<?> viewSub(@RequestParam Integer id) {
        return packageSubService.view(id);
    }
}
