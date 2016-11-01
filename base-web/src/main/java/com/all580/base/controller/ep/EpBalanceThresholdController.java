package com.all580.base.controller.ep;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.api.service.EpService;
import com.all580.notice.api.conf.SmsType;
import com.all580.notice.api.service.SmsService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
     *余额提醒修改设置
     * @param map
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> update(@RequestBody Map map) {
            //core_ep_id
            ParamsMapValidate.validate(map, generateEpBalanceThresholdUpdateValidate());
            //   Integer core_ep_id = Integer.parseInt(map.get("id").toString());
               map.put(EpConstant.EpKey.CORE_EP_ID,getAttribute(EpConstant.EpKey.CORE_EP_ID)) ;
                map.put("ep_id",map.get("balance_id"));
            return epBalanceThresholdService.createOrUpdate(map);

    }

    /**
     *余额提醒查询
     * @return
     */
    @RequestMapping(value = "select", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> select(HttpServletRequest request, Integer balance_id
    ) {
            Map map = new HashMap();
            map.put(EpConstant.EpKey.EP_ID,balance_id);
            map.put(EpConstant.EpKey.CORE_EP_ID,getAttribute(EpConstant.EpKey.CORE_EP_ID)) ;//
         ParamsMapValidate.validate(map, generateEpBalanceThresholdValidate());
            return epBalanceThresholdService.select(map);
    }

    /**
     *余额阀值校验
     * @param  {balance:int  必填余额}
     * @return
     */
    @RequestMapping(value = "balance/warn", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> warn(Integer balance_id,Integer balance ) {
            Map map = new HashMap();
             map.put("ep_id",balance_id);
             map.put(EpConstant.EpKey.CORE_EP_ID,getAttribute(EpConstant.EpKey.CORE_EP_ID)) ;//
             map.put("balance",balance);
            ParamsMapValidate.validate(map, generateEpBalanceValidate());
            boolean bool =epBalanceThresholdService.warn(map);//是否发送成功
            if(bool){

            }
            return null;
    }

    private Map<String[], ValidRule[]> generateEpBalanceThresholdValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id", //
                "core_ep_id",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id", //
                "core_ep_id",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    private Map<String[], ValidRule[]> generateEpBalanceThresholdUpdateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id", //
                "core_ep_id", //
                "threshold",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id", //
                "core_ep_id", //
                "threshold",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    private Map<String[], ValidRule[]> generateEpBalanceThresholdWarnValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id", //
                "balance",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id", //
                "balance",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    private Map<String[], ValidRule[]> generateEpBalanceValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ep_id", //
                "core_ep_id", //
                "balance",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "ep_id", //
                "core_ep_id", //
                "balance",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
