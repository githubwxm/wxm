package com.all580.base.controller.ep;

import com.all580.ep.api.service.CoreEpChannelService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/11 0011.
 */

@Controller
@RequestMapping("api/ep/platform/channel")
@Slf4j
public class EpChannelController extends BaseController {
    @Autowired
    private CoreEpChannelService coreEpChannelService;


    /**
     * 修改通道汇率
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> update(@RequestBody Map params) {

        try {
            ParamsMapValidate.validate(params, generateUpdateEpChannelValidate());
            String rate = params.get("rate").toString();
            if (CommonUtil.isTrue(rate, "\\d{1}\\.\\d{1,2}$|\\d{1}")) {//校验汇率在0 - 9.99之间 乘100 取整
                Double temp = Double.parseDouble(rate) * 100;
                params.put("rate", temp.intValue());
            } else {
                throw new ParamsMapValidationException("汇率不合法");
            }
        } catch (ParamsMapValidationException e) {
            log.warn("修改汇率通道参数错误");
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return coreEpChannelService.update(params);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }


    }

    /**
     * 添加通道汇率
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> create(@RequestBody Map params) {
        try {
            ParamsMapValidate.validate(params, generateCreateEpChannelValidate());
            String rate = params.get("rate").toString();
            if (CommonUtil.isTrue(rate, "\\d{1}\\.\\d{1,2}$|\\d{1}")) {//校验汇率在0 - 9.99之间 乘100 取整
                Double temp = Double.parseDouble(rate) * 100;
                params.put("rate", temp.intValue());
            } else {
                throw new ParamsMapValidationException("汇率不合法");
            }
        } catch (ParamsMapValidationException e) {
            log.warn("添加汇率通道参数错误");
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return coreEpChannelService.create(params);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }

    /**
     * 取消通道汇率   现在是删除，
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "cancel", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> cancel(@RequestBody Map map) {
        try {
            if(CommonUtil.objectIsNumber(map.get("id"))){
                 throw new ParamsMapValidationException("参数不合法");
            }
        } catch (ParamsMapValidationException e) {
            log.warn("取消通道汇率参数错误");
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return coreEpChannelService.cancel(CommonUtil.objectParseInteger(map.get("id")));
        } catch (ApiException e) {
            log.error("取消通道汇率", e);
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }//

    /**
     * 查找通道汇率
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map> list(@RequestBody Map map) {
        try {
            return coreEpChannelService.select(map);
        } catch (ApiException e) {
            log.error("查找通道汇率", e);
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }

    private Map<String[], ValidRule[]> generateCreateEpChannelValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "supplier_core_ep_id", // '供应商id
                "seller_core_ep_id", // '销售商id
                "rate", // 费率
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "supplier_core_ep_id", //
                "seller_core_ep_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    private Map<String[], ValidRule[]> generateUpdateEpChannelValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id", //
                "rate", // 费率
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
