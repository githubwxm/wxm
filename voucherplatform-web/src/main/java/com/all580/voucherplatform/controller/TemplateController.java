package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.TemplateService;
import com.all580.voucherplatform.manager.TemplateValidateManager;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Linv2 on 2017-06-26.
 */

@Controller
@RequestMapping("api/template")
@Slf4j
public class TemplateController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private TemplateValidateManager templateValidateManager;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(@RequestBody Map map) {
        ParamsMapValidate.validate(map, templateValidateManager.createValidate());
        return templateService.create(map);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map map) {
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


    @RequestMapping(value = "selectTemplateList", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> selectTemplateList(String name,
                                                      Integer supplyId,
                                                      Integer prodId,
                                                      Boolean defaultOption,
                                                      @RequestParam(value = "record_Start", defaultValue = "0") Integer recordStart,
                                                      @RequestParam(value = "record_Count", defaultValue = "15") Integer recordCount) {
        return templateService.selectTemplateList(name, supplyId, prodId, defaultOption, recordStart, recordCount);

    }
}
