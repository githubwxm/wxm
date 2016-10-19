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
        // 验证参数
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("创建平台商参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.createPlatform(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }

    @RequestMapping(value = "validate", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> validate(@RequestBody Map map) {

        try {
            return epService.validate(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }
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
     * @param map
     * @return
     */
    @RequestMapping(value = "status/disable", method = RequestMethod.GET)
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
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }
    /**
     * 平台商冻结
     * @param map
     * @return
     */
    @RequestMapping(value = "status/freeze", method = RequestMethod.GET)
    @ResponseBody
    public Result<Integer> freeze(@RequestBody Map map) {
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("冻结企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.platformFreeze(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }
    /**
     * 平台商激活
     * @param map
     * @return
     */
    @RequestMapping(value = "status/enable", method = RequestMethod.GET)
    @ResponseBody
    public Result<Integer> enable(@RequestBody Map map) {
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("冻结企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.platformEnable(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }//
    /**
     * 上游平台商
     * @param
     * @return
     */
    @RequestMapping(value = "list/up", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> platformListUp(HttpServletRequest request, @RequestParam(value="ep_id",
            required=false) String ep_id) {
        Map map = new HashMap();
        map.put("ep_id",ep_id);
        try {//
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateDownUpValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("冻结企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }


        try {
            return epService.platformListUp(map)  ;
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }//list/up
    /**
     * 上游平台商
     * @return
     */
    @RequestMapping(value = "list/down", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> platformListDown(HttpServletRequest request, @RequestParam(value="ep_id",
            required=false) String ep_id) {
        Map map = new HashMap();
        map.put("ep_id",ep_id);
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateDownUpValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("冻结企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.platformListDown(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }//list/u
    /**
     * 上游平台商
     * @param map
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map>> platformList(@RequestBody Map map) {
        try {
            return epService.all(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }//list/u
}
