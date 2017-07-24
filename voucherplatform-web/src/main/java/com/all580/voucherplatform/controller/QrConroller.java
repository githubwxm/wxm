package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.QrService;
import com.all580.voucherplatform.manager.QrRuleValidateManager;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Linv2 on 2017/5/18.
 */

@Controller
@RequestMapping("api/qr")
@Slf4j
public class QrConroller {
    @Autowired
    private QrRuleValidateManager qrRuleManage;
    @Autowired
    private QrService qrService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(@RequestBody Map map) {
        ParamsMapValidate.validate(map, qrRuleManage.createValidate());
        return qrService.create(map);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map map) {
        ParamsMapValidate.validate(map, qrRuleManage.updateValidate());
        return qrService.update(map);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    public Result delete(int id) {
        return qrService.delete(id);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public Result get(int id) {
        return qrService.get(id);
    }

    @RequestMapping(value = "getSupply", method = RequestMethod.GET)
    @ResponseBody
    public Result getSupply(Integer supplyId, Integer prodId) {
        return qrService.get(supplyId, prodId);
    }


    @RequestMapping(value = "selectQrList", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectQrList(String name, Integer len, String prefix, String postfix, Integer supplyId, Integer prodId, Boolean defaultOption, @RequestParam(value = "record_Start", defaultValue = "0") Integer recordStart,
                                  @RequestParam(value = "record_Count", defaultValue = "15")  Integer recordCount) {
        return qrService.selectQrList(name, len, prefix, postfix, supplyId, prodId, defaultOption, recordStart, recordCount);
    }

}
