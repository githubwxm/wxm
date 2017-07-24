package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.DeviceService;
import com.all580.voucherplatform.manager.DeviceValidateManager;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-18.
 */

@Controller
@RequestMapping("api/device")
@Slf4j
public class DeviceController extends BaseController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceValidateManager deviceValidateManager;

    @RequestMapping(value = "addGroup", method = RequestMethod.POST)
    @ResponseBody
    public Result addGroup(@RequestBody Map map) {

        ParamsMapValidate.validate(map, deviceValidateManager.createValidate());
        return deviceService.addGroup(map);

    }

    @RequestMapping(value = "selectGroupList", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> selectGroupList(Integer supplyId, String code, String name, @RequestParam(value = "record_start", defaultValue = "0") Integer recordStart, @RequestParam(value = "record_count", defaultValue = "15") Integer recordCount) {
        return deviceService.selectGroupList(supplyId, code, name, recordStart, recordCount);
    }

    @RequestMapping(value = "addDevice", method = RequestMethod.POST)
    @ResponseBody
    public Result addDevice(@RequestBody Map map) {
        ParamsMapValidate.validate(map, deviceValidateManager.createDeviceValidate());
        return deviceService.addDevice(map);
    }

    @RequestMapping(value = "delDevice", method = RequestMethod.GET)
    @ResponseBody
    public Result delDevice(String code) {
        return deviceService.delDevice(code);
    }

    @RequestMapping(value = "renameDevice", method = RequestMethod.POST)
    @ResponseBody
    public Result renameDevice(@RequestBody Map map) {
        ParamsMapValidate.validate(map, deviceValidateManager.renameDeviceValidate());
        return deviceService.renameDevice(map);
    }


    @RequestMapping(value = "selectDeviceList", method = RequestMethod.GET)
    @ResponseBody
    Result<PageRecord<Map>> selectDeviceList(Integer groupId, Integer supplyId, String code, String name, @RequestParam(value = "record_start", defaultValue = "0") Integer recordStart, @RequestParam(value = "record_count", defaultValue = "15") Integer recordCount) {
        return deviceService.selectDeviceList(groupId, supplyId, code, name, recordStart, recordCount);
    }

    @RequestMapping(value = "setProd", method = RequestMethod.POST)
    @ResponseBody
    public Result setProd(@RequestBody Map map) {
        Integer groupId = CommonUtil.objectParseInteger(map.get("groupId"));
        List<Map> list = (List<Map>) map.get("list");
        return deviceService.setProd(groupId, list);
    }

    @RequestMapping(value = "getProd", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> getProd(
            Integer groupId,
            @RequestParam(value = "record_start", defaultValue = "0") Integer recordStart,
            @RequestParam(value = "record_count", defaultValue = "15") Integer recordCount) {
        return deviceService.getProd(groupId, recordStart, recordCount);
    }



    @RequestMapping(value = "deviceAudit", method = RequestMethod.POST)
    @ResponseBody
    public Result deviceAudit(@RequestBody Map map) {
        ParamsMapValidate.validate(map, deviceValidateManager.deviceAuditValidate());
        return deviceService.deviceAudit(map);
    }

    @RequestMapping(value = "selectApplyList", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> selectApplyList(
            String code,
            Integer status,
            @RequestParam(value = "record_start", defaultValue = "0") Integer recordStart,
            @RequestParam(value = "record_count", defaultValue = "15") Integer recordCount) {

        return deviceService.selectApplyList(code, status, recordStart, recordCount);
    }
}
