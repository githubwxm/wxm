package com.all580.base.controller.voucherplatform;

import com.all580.base.manager.voucherplatform.QrRuleValidateManager;
import com.all580.voucherplatform.api.service.QrService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/18.
 */

@Controller
@RequestMapping("voucherplatform/qr")
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

    @RequestMapping(value = "getCount", method = RequestMethod.GET)
    @ResponseBody
    public Result getCount(String name, Integer len, String prefix, String postfix, Integer supplyId, Integer prodId, Boolean defaultOption) {
        int count = qrService.getCount(name, len, prefix, postfix, supplyId, prodId, defaultOption);
        Result result = new Result(true);
        result.putExt("data", count);
        return result;
    }

    @RequestMapping(value = "getList", method = RequestMethod.GET)
    @ResponseBody
    public Result getList(String name, Integer len, String prefix, String postfix, Integer supplyId, Integer prodId, Boolean defaultOption, Integer recordStart,
                          Integer recordCount) {
        List<Map> mapList = qrService.getList(name, len, prefix, postfix, supplyId, prodId, defaultOption, recordStart, recordCount);
        Result result = new Result(true);
        result.putExt("data", mapList);
        return result;
    }

}
