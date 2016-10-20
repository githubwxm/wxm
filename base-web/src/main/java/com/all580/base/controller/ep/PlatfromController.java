package com.all580.base.controller.ep;

import com.all580.base.manager.PlatfromValidateManager;
import com.all580.ep.api.service.EpService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
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
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
        return epService.createPlatform(map);

    }

    @RequestMapping(value = "validate", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> validate(@RequestParam(value = "access_id") String access_id
                               ) {
        Map map = new HashMap();
        map.put("access_id", access_id);
        return epService.validate(map);

    }
//
//    /**
//     *
//     * @param map
//     * @return
//     */
//    @RequestMapping(value = "channel/add", method = RequestMethod.GET)
//    @ResponseBody
//    public Result<Map> channelAdd(@RequestBody Map map) {
//        // 验证参数
//        try {
//            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
//        } catch (ParamsMapValidationException e) {
//            log.warn("创建订单参数验证失败", e);
//            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
//        }
//        return epService.validate(map);
//    } //

    /**
     * 平台商停用
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "status/disable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> disable(@RequestBody Map map) {
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("冻结企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.platformDisable(map);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }

    /**
     * 平台商冻结
     *
     * @param
     * @return
     */
    @RequestMapping(value = "status/freeze", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> freeze(HttpServletRequest request, @RequestParam(value = "id",
            required = true) String id) {
        Map map = new HashMap();
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());

        try {
            return epService.platformFreeze(map);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }

    /**
     * 平台商激活
     *
     * @param
     * @return
     */
    @RequestMapping(value = "status/enable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> enable(HttpServletRequest request, @RequestParam(value = "id",
            required = true) String id) {
        Map map = new HashMap();
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        map.put("id", id);
        return epService.platformEnable(map);

    }//

    /**
     * 上游平台商
     *
     * @param
     * @return
     */
    @RequestMapping(value = "list/up", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> platformListUp(HttpServletRequest request,
                                      @RequestParam(value = "ep_id") String ep_id,
                                      @RequestParam(value = "epName", required = false) String epName,
                                      @RequestParam(value = "epProvince", required = false) String epProvince,
                                      @RequestParam(value = "epCity", required = false) String epCity,
                                      @RequestParam(value = "epPhone", required = false) String epPhone) {
        Map map = new HashMap();
        map.put("ep_id", ep_id);
        map.put("epName",epName);
        map.put("epProvince",epProvince);
        map.put("epCity",epCity);
        map.put("epPhone",epPhone);
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateDownUpValidate());
        return epService.platformListUp(map);

    }//list/up

    /**
     * 下游平台商
     *
     * @return
     */
    @RequestMapping(value = "list/down", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> platformListDown(HttpServletRequest request,
                                        @RequestParam(value = "ep_id") String ep_id,
                                        @RequestParam(value = "epName", required = false) String epName,
                                        @RequestParam(value = "epProvince", required = false) String epProvince,
                                        @RequestParam(value = "epCity", required = false) String epCity,
                                        @RequestParam(value = "epPhone", required = false) String epPhone) {
        Map map = new HashMap();
        map.put("ep_id", ep_id);
        map.put("epName",epName);
        map.put("epProvince",epProvince);
        map.put("epCity",epCity);
        map.put("epPhone",epPhone);

        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateDownUpValidate());


        return epService.platformListDown(map);

    }//list/u

    /**
     * 平台商列表
     *
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map>> platformList(@RequestParam(value = "record_start") Integer record_start,
                                          @RequestParam(value = "record_count") Integer record_count) {
        Map map = new HashMap();
        map.put("record_start", record_start);
        map.put("record_count", record_count);
        return epService.all(map);

    }//list/u
}
