package com.all580.base.controller.ep;

import com.all580.ep.api.service.CoreEpChannelService;
import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.api.service.EpService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
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
 * Created by Administrator on 2016/10/11 0011.余额提醒设置
 */

@Controller
@RequestMapping("api/ep/platform/balanceThreshold")
@Slf4j
public class EpBalanceThresholdController extends BaseController {
    @Autowired
    private EpBalanceThresholdService epBalanceThresholdService;
    @Autowired
    private EpService epService;

    /**
     *余额提醒设置
     * @param map
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> update(@RequestBody Map map) {

        try {//core_ep_id
            ParamsMapValidate.validate(map, generateEpBalanceThresholdUpdateValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("余额提醒设置失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            Integer core_ep_id = Integer.parseInt(map.get("id").toString());
            map.put("core_ep_id",epService.selectPlatformId(core_ep_id)) ;
            return epBalanceThresholdService.createOrUpdate(map);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }

    /**
     *余额提醒设置
     * @param map
     * @return
     */
    @RequestMapping(value = "select", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> select(@RequestBody Map map) {

        try {
            ParamsMapValidate.validate(map, generateEpBalanceThresholdValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("查询参数校验", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return epBalanceThresholdService.select(map);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }

    }

    private Map<String[], ValidRule[]> generateEpBalanceThresholdValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id.", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id.", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    private Map<String[], ValidRule[]> generateEpBalanceThresholdUpdateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id.", //
                "core_ep_id", //
                "threshold",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id.", //
                "core_ep_id", //
                "threshold",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
