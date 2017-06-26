package com.all580.base.controller.voucherplatform;

import com.all580.base.manager.voucherplatform.TemplateValidateManager;
import com.all580.voucherplatform.api.service.TemplateService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-26.
 */

@Controller
@RequestMapping("voucherplatform/template")
@Slf4j
public class TemplateController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private TemplateValidateManager templateValidateManager;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(Map map) {
        ParamsMapValidate.validate(map, templateValidateManager.createValidate());
        return templateService.create(map);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(Map map) {
        ParamsMapValidate.validate(map, templateValidateManager.updateValidate());
        return templateService.update(map);
    }


    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    public Result delete(int id) {
        return templateService.delete(id);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public Result get(int id) {
        return templateService.get(id);
    }

    @RequestMapping(value = "getSupply", method = RequestMethod.GET)
    @ResponseBody
    public Result getSupply(Integer supplierId, Integer prodId) {
        return templateService.get(supplierId, prodId);
    }


    @RequestMapping(value = "getCount", method = RequestMethod.GET)
    @ResponseBody
    public Result getCount(String name, Integer supplyId, Integer prodId, Boolean defaultOption) {
        int count = templateService.getCount(name, supplyId, prodId, defaultOption);
        Result result = new Result(true);
        result.putExt("data", count);
        return result;
    }

    @RequestMapping(value = "getList", method = RequestMethod.GET)
    @ResponseBody
    public Result getList(String name,
                          Integer supplyId,
                          Integer prodId,
                          Boolean defaultOption,
                          Integer recordStart,
                          Integer recordSCount) {
        List<Map> mapList = templateService.getList(name, supplyId, prodId, defaultOption, recordStart, recordSCount);
        Result result = new Result(true);
        result.putExt("data", mapList);
        return result;
    }
}
