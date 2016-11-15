package com.all580.base.controller.ep;


import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpFinanceService;
import com.all580.ep.api.service.LogCreditService;
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
 * Created by wxming on 2016/10/19 0019.
 */
@Controller
@RequestMapping("api/finance")
@Slf4j
public class EpFinanceController extends BaseController {
    @Autowired
    private LogCreditService logCreditService;

    @Autowired
    private EpFinanceService epFinanceService;

    /**
     * 查询授信列表
     * @param
     * @return
     */
    @RequestMapping(value = "credit/platform/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getList(HttpServletRequest request,
                                String name,
                              String link_phone,
                               Integer province,
                               Integer city,
                                Integer area,
                               Integer limitStart,
                                Integer limitEnd,
                               boolean credit,
                               Integer record_start,
                               Integer record_count
                             ) {
        Map<String,Object> map = new HashMap<>();
        map.put("name", name);
        map.put("link_phone", link_phone);
        map.put("province", province);
        map.put("city", city);
        map.put("area", area);
        map.put("limitStart", limitStart);
        map.put("limitEnd", limitEnd);
        map.put("credit", credit);
        map.put("record_start", record_start);
        map.put("record_count", record_count);
        map.put("core_ep_id",request.getAttribute("core_ep_id"));
        ParamsMapValidate.validate(map, generateCoreEpIdValidate());
        return logCreditService.selectList(map);
    }

    /**
     * 查询授信历史列表
     *
     * @return
     */
    @RequestMapping(value = "credit/hostoryCredit", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> hostoryCredit(HttpServletRequest request,Integer ep_id) {
        Map<String,Object> map = new HashMap<>();
        map.put("ep_id", ep_id);
        map.put("core_ep_id", request.getAttribute("core_ep_id"));
        ParamsMapValidate.validate(map, generateCreateSelectValidate());
        return logCreditService.hostoryCredit(map);
    }

    /**
     * 添加授信
     * @param map
     * @return
     */
    @RequestMapping(value = "credit/set", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> set(@RequestBody Map map) {
            ParamsMapValidate.validate(map, generateCreateCreditValidate());
            return logCreditService.create(map);
    }//set
    /**
     * 企业账户管理列表
     * @return
     */
    @RequestMapping(value = "credit/getAccountInfoList", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getAccountInfoList(HttpServletRequest request,Integer ep_id,String name,String link_phone,Integer ep_type,
    Integer province,Integer city){
        Map<String,Object> map = new HashMap<>();
        map.put("ep_id",ep_id);  //todo 获取平台商id
        map.put("core_ep_id",request.getAttribute("core_ep_id"));
        map.put("name",name);
        map.put("link_phone",link_phone);
        map.put("ep_type",ep_type);
        map.put("province",province);
        map.put("city",city);
        ParamsMapValidate.validate(map, generateCreateSelectValidate());
        return epFinanceService.getAccountInfoList(map);
    }

    @RequestMapping(value = "account/info", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getAccountInfoList(@RequestParam(value = "epId") Integer epId){
     Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return epFinanceService.getBalanceAccountInfo(epId,coreEpId);
    }
    @RequestMapping(value = "lstBalance", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> lstBalance(@RequestParam(value = "epId") Integer epId,Integer record_start, Integer record_count){
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return epFinanceService.getBalanceSerialList(epId,coreEpId,record_start,record_count,null);
    }

    /**
     * 修改余额
     * @param map
     * @return
     */
    @RequestMapping(value = "balance/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> balanceAdd(HttpServletRequest request, @RequestBody Map<String,Object> map) {
        ParamsMapValidate.validate(map, generateBalanceSelectValidate());//
        Integer coreEpId=CommonUtil.objectParseInteger(request.getAttribute(EpConstant.EpKey.CORE_EP_ID) ) ;
        Integer balance=CommonUtil.objectParseInteger(map.get("balance")) ;
        Integer balanceEpId=CommonUtil.objectParseInteger(map.get("balanceEpId")) ;
        return epFinanceService.addBalance(balanceEpId,coreEpId,balance);
    }


    public Map<String[], ValidRule[]> generateCreateCreditValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "core_ep_id",
                "ep_id", //
                "credit_after", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "core_ep_id",
                "ep_id", //
                "credit_after", //授信额度
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> generateBalanceSelectValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "balanceEpId", //
                "balance",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "balanceEpId", //
                "balance",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    public Map<String[], ValidRule[]> generateCreateSelectValidate() {
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
    /**
     * 校验平台商id
     * @return
     */
    public Map<String[], ValidRule[]> generateCoreEpIdValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "core_ep_id", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "core_ep_id" // 平台商id
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

}
