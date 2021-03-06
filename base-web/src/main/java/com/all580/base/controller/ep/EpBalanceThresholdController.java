package com.all580.base.controller.ep;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.api.service.EpService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
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
@RequestMapping("api/ep/platform/balance_threshold")
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
    public Result<Integer> update(@RequestBody Map<String,Object> map) {
            //core_ep_id
            // map.put(EpConstant.EpKey.CORE_EP_ID,getAttribute(EpConstant.EpKey.CORE_EP_ID)) ;
            // map.put("id",map.get("id"));
            ParamsMapValidate.validate(map, generateEpBalanceThresholdUpdateValidate());
            //   Integer core_ep_id = Integer.parseInt(map.get("id").toString());

            return epBalanceThresholdService.createOrUpdate(map);

    }

    @RequestMapping(value = "create/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> CreateOnUpdateThreshold(@RequestBody Map<String,Object> map) {
        //core_ep_id
        // map.put("id",map.get("id"));
        //   Integer core_ep_id = Integer.parseInt(map.get("id").toString());
        map.put("send_phone1",CommonUtil.emptyStringParseNull( map.get("send_phone1")));
        map.put("send_phone2",CommonUtil.emptyStringParseNull( map.get("send_phone2")));
        ParamsMapValidate.validate(map, generateEpBalanceThresholdMobileWarnValidate());
        return epBalanceThresholdService.createOnUpdateThreshold(map);

    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "select/threshold/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectThresholdList( String name,Integer start_threshold,Integer end_threshold,Integer ep_id,
                                          Integer record_start,Integer record_count
    ) {
        Map<String,Object> map = new HashMap<>();
        map.put("ep_id",ep_id);
        map.put("name",name);
        map.put("record_start",record_start);
        map.put("record_count",record_count);
        map.put("start_threshold",start_threshold);
        map.put("end_threshold",end_threshold);
        //map.put(EpConstant.EpKey.CORE_EP_ID,getAttribute(EpConstant.EpKey.CORE_EP_ID)) ;
        return epBalanceThresholdService.selectThresholdList(map);
    }
    /**
     *余额提醒查询
     * @return
     */
    @RequestMapping(value = "select", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> select( Integer id
    ) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",id);
            map.put(EpConstant.EpKey.CORE_EP_ID,getAttribute(EpConstant.EpKey.CORE_EP_ID)) ;//
         ParamsMapValidate.validate(map, generateEpBalanceThresholdValidate());
            return epBalanceThresholdService.selectBalance(map);
    }

    /**
     *余额阀值校验
     * @param  {balance:int  必填余额}
     * @return
     */
    @RequestMapping(value = "balance/warn", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> warn(Integer id,Integer balance ) {
            Map<String,Object> map = new HashMap<>();
             map.put("id",id);//企业id
             map.put(EpConstant.EpKey.CORE_EP_ID,getAttribute(EpConstant.EpKey.CORE_EP_ID)) ;//
             map.put("balance",balance);
            ParamsMapValidate.validate(map, generateEpBalanceValidate());
            Result r=  epBalanceThresholdService.warn(map);//是否发送成功
            if(r.isSuccess()){

            }
            return null;
    }

    private Map<String[], ValidRule[]> generateEpBalanceThresholdValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id", //
                "core_ep_id",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id", //
                "core_ep_id",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    private Map<String[], ValidRule[]> generateEpBalanceThresholdUpdateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id", //
                "core_ep_id", //
                "threshold",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id", //
                "core_ep_id", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    private Map<String[], ValidRule[]> generateEpBalanceThresholdWarnValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id", //
                "balance",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id", //
                "balance",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    private Map<String[], ValidRule[]> generateEpBalanceThresholdMobileWarnValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "send_phone1", // 订单联系人手机号码
                "send_phone2", // 订单联系人手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});
        return rules;
    }
    private Map<String[], ValidRule[]> generateEpBalanceValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id", //
                "core_ep_id", //
                "balance",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id", //
                "core_ep_id", //
                "balance",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
