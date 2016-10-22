package com.all580.ep.service;

import com.all580.ep.api.service.CoreEpChannelService;
import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.CoreEpChannelMapper;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.Map;
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class CoreEpChannelServiceImple implements CoreEpChannelService {

    @Autowired
    private CoreEpChannelMapper coreEpChannelMapper;

    @Autowired
    private BalancePayService balancePayService;

    @Autowired
    private EpBalanceThresholdService epBalanceThresholdService;

    @Override   //// TODO: 2016/10/11 0011   上游下游供销关系同步 数据
    public Result<Integer> create(Map params) {
        Result<Integer> result = new Result<Integer>();

        try {
            //     销售
            Integer epId= Common.objectParseInteger(params.get("seller_core_ep_id"));
            Integer coreEpId = Common.objectParseInteger(params.get("supplier_core_ep_id"));
            if(coreEpChannelMapper.selectChannel(params)>0){
                throw new ApiException("通道汇率已经存在");
            }
            result.put(coreEpChannelMapper.create(params));
            result.setSuccess();
            balancePayService.createBalanceAccount(epId,coreEpId);//添加钱包
            params.put("ep_id",epId);
            params.put("core_ep_id",coreEpId);
            epBalanceThresholdService.createOrUpdate(params);//添加余额阀值
        } catch (Exception e) {
            log.error("添加汇率通道参数错误", e);
            throw new ApiException("添加汇率通道参数错误", e);
        }
        return result;
    }

    @Override
    public Result<Integer> update(Map params) {
        Result<Integer> result = new Result<Integer>();
        try {
            result.put(coreEpChannelMapper.update(params));
            result.setSuccess();
        } catch (Exception e) {
           log.error("数据库修改出错", e);
            throw new ApiException("数据库修改出错", e);
        }
        return result;
    }

    @Override
    public Result<Integer> cancel(Integer id) {
        Result<Integer> result = new Result<Integer>();
        try {
            result.put(coreEpChannelMapper.cancel(id));
            result.setSuccess();
        } catch (Exception e) {
            log.error("添加汇率通道参数错误", e);
            throw new ApiException("添加汇率通道参数错误", e);
        }
        return result;
    }

    @Override  //EpChannelRep
    public Result<Map> select(Map params) {
        Map resultMap = new HashMap();
        Result<Map> result = new Result<>();
        try {
            CommonUtil.checkPage(params);
            resultMap.put("list",coreEpChannelMapper.select(params));
            resultMap.put("totalCount",coreEpChannelMapper.selectCount(params));
            result.put(resultMap);
            result.setSuccess();
        } catch (Exception e) {
           // log.error("数据库查询出错", e);
            throw new ApiException("数据库查询出错", e);
        }
        return result;
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
