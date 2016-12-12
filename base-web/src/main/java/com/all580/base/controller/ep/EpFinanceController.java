package com.all580.base.controller.ep;


import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpFinanceService;
import com.all580.ep.api.service.LogCreditService;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
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

    @Autowired
    private BalancePayService balancePayService;


    /**
     * 查询银行卡信息余企业信息
     * @return
     */
    @RequestMapping(value = "select/bank", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectBank(Integer id) {
        Assert.notNull(id, "参数【id】不能为空");
        return epFinanceService.selectBank(id);
    }
    /**
     * 添加银行卡信息
     * @return
     */
    @RequestMapping(value = "add/bank", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addBank(@RequestBody Map<String,Object> param) {
        ParamsMapValidate.validate(param, generateBankAddValidate());
        return epFinanceService.addBank(param);
    }
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
                               Integer limit_start,
                                Integer limit_end,
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
        map.put("limit_start", limit_start);
        map.put("limit_end", limit_end==null?0:limit_end);
        map.put("credit", credit);
        map.put("record_start", record_start);
        map.put("record_count", record_count);
        map.put("core_ep_id",request.getAttribute("core_ep_id"));
       // ParamsMapValidate.validate(map, generateCoreEpIdValidate());
        return logCreditService.selectList(map);
    }

    /**
     * 查询授信历史列表
     *
     * @return
     */
    @RequestMapping(value = "credit/hostory_credit", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> hostoryCredit(HttpServletRequest request,Integer credit_ep_id) {
        Map<String,Object> map = new HashMap<>();
        map.put("credit_ep_id", credit_ep_id);
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
    public Result<Integer> set(@RequestBody Map<String,Object> map) {

            ParamsMapValidate.validate(map, generateCreateCreditValidate());
            return logCreditService.create(map);
    }//set


    /**
     * 销售平台账户管理
     */
    @RequestMapping(value = "get_seller_platfrom_accunt_info", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getSellerPlatfromAccuntInfo(HttpServletRequest request,Integer ep_id,String name,String link_phone
            , Integer record_start, Integer record_count){
        Map<String,Object> map = new HashMap<>();
        map.put("ep_id",ep_id);
        map.put("name",name);
        map.put("link_phone",link_phone);
        map.put("record_start",record_start);
        map.put("record_count",record_count);
        return epFinanceService.getSellerPlatfromAccuntInfo(map);
    }
    /**
     * 企业账户管理列表
     * @return
     */
    @RequestMapping(value = "credit/get_account_info_list", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getAccountInfoList(HttpServletRequest request,Integer ep_id,String name,String link_phone
            ,Integer ep_type,Integer province,Integer city, Integer record_start, Integer record_count){
        Map<String,Object> map = new HashMap<>();
        map.put("ep_id",ep_id);  //todo 获取平台商id
        map.put("core_ep_id",ep_id);//
        map.put("name",name);
        map.put("link_phone",link_phone);
        map.put("ep_type",ep_type);
        map.put("province",province);
        map.put("city",city);
        map.put("record_start",record_start);
        map.put("record_count",record_count);
       // ParamsMapValidate.validate(map, generateCreateSelectValidate());
        return epFinanceService.getAccountInfoList(map);
    }

    @RequestMapping(value = "update_summary", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateSummary(@RequestBody Map map){
        ParamsMapValidate.validate(map, generateSummaryValidate());
        Integer id = CommonUtil.objectParseInteger(map.get("id"));
        String summary = CommonUtil.objectParseString(map.get("summary"));
        return balancePayService.updateSummary(id,summary);
    }
    @RequestMapping(value = "account/info", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getAccountInfoList(@RequestParam(value = "balance_ep_id") Integer balance_ep_id){
     Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return epFinanceService.getBalanceAccountInfo(balance_ep_id,coreEpId);
    }
    @RequestMapping(value = "lst_balance", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> lstBalance(@RequestParam(value = "balance_ep_id") Integer balance_ep_id,
                                String balance_status,String start_date,String end_date,String ref_id,Integer export ,
                                Integer record_start, Integer record_count){
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return epFinanceService.getBalanceSerialList(balance_ep_id,coreEpId,balance_status,
                start_date,end_date,ref_id,export,record_start,record_count,null);
    }

    /**
     * 修改余额
     * @param params
     * @return
     */
    @RequestMapping(value = "balance/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> balanceAdd(@RequestBody Map<String,Object> params) {
        ParamsMapValidate.validate(params, generateBalanceSelectValidate());//
        Integer coreEpId=CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID) ) ;
        Integer balance=CommonUtil.objectParseInteger(params.get("balance")) ;
        Integer balanceEpId=CommonUtil.objectParseInteger(params.get("balance_ep_id")) ;
        return epFinanceService.addBalance(balanceEpId,coreEpId,balance);
    }


    public Map<String[], ValidRule[]> generateSummaryValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "summary", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    public Map<String[], ValidRule[]> generateCreateCreditValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "core_ep_id",
                "credit_ep_id", //
                "credit_after", //
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "core_ep_id",
                "credit_ep_id", //
                "credit_after", //授信额度
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> generateBalanceSelectValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "balance_ep_id", //
                "balance",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "balance_ep_id", //
                "balance",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
    public Map<String[], ValidRule[]> generateCreateSelectValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "credit_ep_id", //
                "core_ep_id",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "credit_ep_id", //
                "core_ep_id",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    public Map<String[], ValidRule[]> generateBankAddValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "bank_username", //
                "bank_name_address",
                "bank_num",
        }, new ValidRule[]{new ValidRule.NotNull()});
        return rules;
    }


}
