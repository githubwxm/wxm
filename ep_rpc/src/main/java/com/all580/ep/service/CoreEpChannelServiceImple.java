package com.all580.ep.service;

import com.all580.ep.api.service.CoreEpChannelService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.CoreEpChannelMapper;
import com.framework.common.Result;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class CoreEpChannelServiceImple implements CoreEpChannelService {

    @Autowired
    private CoreEpChannelMapper coreEpChannelMapper;

    @Override
    public Result<Integer> create(Map params) {
        Result<Integer> result = new Result<Integer>();
        try {
            ParamsMapValidate.validate(params, generateCreateEpChannelValidate());
             String rate=  params.get("rate").toString();
            if(Common.isTrue(rate,"\\d{1}\\.\\d{1,2}$|\\d{1}")){//校验汇率在0 - 9.99之间 乘100 取整
                 Double temp =Double.parseDouble(rate)*100;
                params.put("rate",temp.intValue());
            }else{
                throw new ParamsMapValidationException("汇率不合法");
            }
        } catch (ParamsMapValidationException e) {
            log.warn("添加汇率通道参数错误");
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            result.put(coreEpChannelMapper.create(params));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库添加出错"+e);
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
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库修改出错"+e);
        }
        return result;
    }

    @Override
    public Result<Integer> cancle(Integer id) {
        Result<Integer> result = new Result<Integer>();
        try {
            result.put(coreEpChannelMapper.cancel(id));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库修改出错"+e);
        }
        return result;
    }

    @Override  //EpChannelRep
    public Result<List<Map>> select(Map params) {
        Result<List<Map>> result = new Result<>();
        try {
            result.put(coreEpChannelMapper.select(params));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库查询出错"+e);
        }
        return result;
    }


    private Map<String[], ValidRule[]> generateCreateEpChannelValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "supplier_core_ep_id.", // 供应侧平台商ID'
                "seller_core_ep_id", // ''销售侧平台商ID',
                "rate", // 费率
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "supplier_core_ep_id", // 订单子产品ID
                "seller_core_ep_id", // 计划ID
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }



}
