package com.all580.base.controller.ep;

import com.all580.base.manager.PlatfromValidateManager;
import com.all580.ep.api.service.EpService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by Administrator on 2016/10/11 0011.  平台商网关
 */
@Controller
@RequestMapping("api/ep/platform")
@Slf4j
public class PlatfromController extends BaseController {
    @Autowired
    private EpService epService;

    @Autowired
    private PlatfromValidateManager platfromValidateManager;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map> create(@RequestBody Map map) {
        // 验证参数
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("创建订单参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        return epService.createPlatform(map);
    }

    @RequestMapping(value = "validate", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> validate(@RequestBody Map map) {
        // 验证参数
        try {
            ParamsMapValidate.validate(map, null);
        } catch (ParamsMapValidationException e) {
            log.warn("创建订单参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        return epService.validate(map);
    }


    @RequestMapping(value = "channel/add", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> channelAdd(@RequestBody Map map) {
        // 验证参数
        try {
            ParamsMapValidate.validate(map, null);
        } catch (ParamsMapValidationException e) {
            log.warn("创建订单参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        return epService.validate(map);
    }
}
