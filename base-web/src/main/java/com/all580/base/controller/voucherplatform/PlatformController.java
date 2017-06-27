package com.all580.base.controller.voucherplatform;

import com.all580.base.manager.voucherplatform.PlatformValidateManager;
import com.all580.voucherplatform.api.service.PlatformService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-26.
 */
@Controller(value = "voucherplatform/platform")
public class PlatformController {
    @Autowired
    private PlatformService platformService;
    @Autowired
    private PlatformValidateManager platformValidateManager;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(Map map) {
        ParamsMapValidate.validate(map, platformValidateManager.createValidate());
        return platformService.create(map);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(Map map) {
        ParamsMapValidate.validate(map, platformValidateManager.updateValidate());
        return platformService.create(map);
    }

    @RequestMapping(value = "createRole", method = RequestMethod.GET)
    @ResponseBody
    public Result createRole(Integer platformId, Integer supplyId) {
        return platformService.createRole(platformId, supplyId);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public Result get(Integer id) {
        Map map = platformService.getPlatform(id);
        Result result = new Result(true);
        result.putExt("data", map);
        return result;
    }

    @RequestMapping(value = "getProd", method = RequestMethod.GET)
    @ResponseBody
    public Result getProd(Integer id) {
        Map map = platformService.getProd(id);
        Result result = new Result(true);
        result.putExt("data", map);
        return result;
    }

    @RequestMapping(value = "getProdCount", method = RequestMethod.GET)
    @ResponseBody
    public Result getProdCount(String name, Integer platformId, Integer supplyId, Integer supplyprodId, String platformProdCode, Integer productTypeId) {
        Integer count = platformService.getProdCount(name, platformId, supplyId, supplyprodId, platformProdCode, productTypeId);
        Result result = new Result(true);
        result.putExt("data", count);
        return result;
    }

    @RequestMapping(value = "getProdList", method = RequestMethod.GET)
    @ResponseBody
    public Result getProdList(String name, Integer platformId, Integer supplyId, Integer supplyprodId, String platformProdCode, Integer productTypeId, Integer recordStart, Integer recordCount) {
        List<Map> mapList = platformService.getProdList(name, platformId, supplyId, supplyprodId, platformProdCode, productTypeId, recordStart, recordCount);
        Result result = new Result(true);
        result.putExt("data", mapList);
        return result;
    }


    @RequestMapping(value = "getProdCount", method = RequestMethod.GET)
    @ResponseBody
    public Result getPlatformCount(String name) {
        Integer count = platformService.getPlatformCount(name);
        Result result = new Result(true);
        result.putExt("data", count);
        return result;
    }

    @RequestMapping(value = "getProdList", method = RequestMethod.GET)
    @ResponseBody
    public Result getPlatformtList(String name, Integer recordStart, Integer recordCount) {
        List<Map> mapList = platformService.getPlatformtList(name, recordStart, recordCount);
        Result result = new Result(true);
        result.putExt("data", mapList);
        return result;
    }
}
