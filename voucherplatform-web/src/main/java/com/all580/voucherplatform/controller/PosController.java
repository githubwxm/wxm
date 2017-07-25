package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.DeviceService;
import com.all580.voucherplatform.api.service.PosService;
import com.all580.voucherplatform.manager.DeviceValidateManager;
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
 * Created by Linv2 on 2017-07-20.
 */

@Controller
@RequestMapping(value = "api/pos")
public class PosController extends BaseController {
    @Autowired
    private PosService posService;
    @Autowired
    private DeviceValidateManager deviceValidateManager;

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public Result apply(@RequestBody Map map) {
        ParamsMapValidate.validate(map, deviceValidateManager.applyDeviceValidate());
        return posService.apply(map);
    }

    @RequestMapping(value = "query", method = RequestMethod.POST)
    @ResponseBody
    public Result query(@RequestBody Map map) {
        ParamsMapValidate.validate(map, deviceValidateManager.applyDeviceValidate());
        return posService.query(map);
    }

    @RequestMapping(value = "req", method = RequestMethod.POST)
    @ResponseBody
    public Result req(@RequestBody Map map) {
        ParamsMapValidate.validate(map, deviceValidateManager.applyDeviceValidate());
        return posService.request(map);
    }
}
