package com.all580.base.controller.ep;

import com.all580.base.manager.PlatfromValidateManager;
import com.all580.ep.api.service.EpService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.util.CommonUtil;
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
 * Created by Administrator on 2016/10/11 0011.
 */
@Controller
@RequestMapping("api/ep")
@Slf4j
public class EpController extends BaseController {
    @Autowired
    private EpService epService;

    @Autowired
    private PlatfromValidateManager platfromValidateManager;//select

    /**
     * 创建企业
     * @param map
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map> create(@RequestBody Map map) {
        // 验证参数
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("创建企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.createEp(map);
        } catch (ParamsMapValidationException e) {
            log.warn("创建企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }catch(ApiException e){
            log.warn("创建企业参数验证失败", e);
            return new Result<>(false, e.getCode(), e.getMessage());
        }




    }

    /**
     * 修改企业
     * @param map
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map> update(@RequestBody Map map) {
        // 验证参数
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
            String ep_class = (String) map.get("ep_class");
            String id = map.get("id").toString();
            if (!CommonUtil.isTrue(id, "\\d+")) {
                throw new ParamsMapValidationException("企业id错误");
            }
            if (!CommonUtil.isTrue(ep_class, "\\d+")) {
                throw new ParamsMapValidationException("企业分类错误");
            }
        } catch (ParamsMapValidationException e) {
            log.warn("修改企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.updateEp(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }

    /**
     * 冻结企业
     * @param map
     * @return
     */
    @RequestMapping(value = "status/freeze", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> statusFreeze(@RequestBody Map map) {
        // 验证参数
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("修改企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.freeze(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }
    /**
     * 停用企业
     * @param map
     * @return
     */
    @RequestMapping(value = "status/disable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> statusDisable(@RequestBody Map map) {
        // 验证参数D
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("修改企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.disable(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }
    /**
     * 激活企业
     * @param map
     * @return
     */
    @RequestMapping(value = "status/enable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> statusEnable(@RequestBody Map map) {
        // 验证参数D
        try {
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("修改企业参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epService.enable(map);
        }catch (ApiException e){
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }
}
