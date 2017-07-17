package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.PlatformService;
import com.all580.voucherplatform.manager.PlatformValidateManager;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.vo.PageRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Linv2 on 2017-06-26.
 */
@Controller
@RequestMapping(value = "api/action")
public class PlatformController {
    @Autowired
    private PlatformService platformService;
    @Autowired
    private PlatformValidateManager platformValidateManager;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(@RequestBody Map map) {
        ParamsMapValidate.validate(map, platformValidateManager.createValidate());
        return platformService.create(map);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map map) {
        ParamsMapValidate.validate(map, platformValidateManager.updateValidate());
        return platformService.create(map);
    }

    @RequestMapping(value = "createRole", method = RequestMethod.POST)
    @ResponseBody
    public Result createRole(@RequestBody Map map) {
        ParamsMapValidate.validate(map, platformValidateManager.createRoleValidate());
        Integer supplyId = CommonUtil.objectParseInteger(map.get("supplyId"));
        Integer platformId = CommonUtil.objectParseInteger(map.get("platformId"));
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

    @RequestMapping(value = "selectPlatformProdList", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectPlatformProdList(
            String name,
            Integer platformId,
            Integer supplyId,
            Integer supplyprodId,
            String platformProdCode,
            @RequestParam(value = "prodType") Integer productTypeId,
            @RequestParam(value = "record_Start", defaultValue = "0") Integer recordStart,
            @RequestParam(value = "record_Count", defaultValue = "15") Integer recordCount) {
        return platformService.selectPlatformProdList(name, platformId, supplyId, supplyprodId, platformProdCode, productTypeId, recordStart, recordCount);

    }


    @RequestMapping(value = "selectPlatformList", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectPlatformList(String name, @RequestParam(defaultValue = "0") Integer recordStart, @RequestParam(defaultValue = "15") Integer recordCount) {
        return platformService.selectPlatformList(name, recordStart, recordCount);
    }

    @RequestMapping(value = "selectRoleList", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectRoleList(Integer platformId, Integer supplyId, String authId, String authKey, String code, String name, @RequestParam(defaultValue = "0") Integer recordStart, @RequestParam(defaultValue = "15") Integer recordCount) {
        return platformService.selectRoleList(platformId, supplyId, authId, authKey, code, name, recordStart, recordCount);
    }

    @RequestMapping(value = "selectProdTyeList", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> selectRoleList(String name, @RequestParam(defaultValue = "0") Integer recordStart, @RequestParam(defaultValue = "15") Integer recordCount) {
        return platformService.selectProdTyeList(name, recordStart, recordCount);
    }


    @RequestMapping(value = "setProdType", method = RequestMethod.POST)
    @ResponseBody
    public Result setProdType(@RequestBody Map map) {
        ParamsMapValidate.validate(map, platformValidateManager.createProdTypeValidate());
        return platformService.setProdType(map);
    }

    @RequestMapping(value = "delProdType", method = RequestMethod.GET)
    @ResponseBody
    public Result delProdType(Integer prodTypeId) {
        return platformService.delProdType(prodTypeId);
    }
    @RequestMapping(value = "selectProdType", method = RequestMethod.GET)
    @ResponseBody
    public Result selectProdType(Integer prodTypeId){
        return platformService.selectProdType(prodTypeId);
    }

}
