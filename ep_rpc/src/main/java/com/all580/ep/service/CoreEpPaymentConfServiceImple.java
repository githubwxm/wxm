package com.all580.ep.service;


import com.all580.ep.api.EpConstant;
import com.all580.ep.api.entity.CoreEpPaymentConf;
import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.api.service.CoreEpPaymentConfService;
import com.all580.ep.dao.CoreEpPaymentConfMapper;
import com.framework.common.Result;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.validate.ValidRule;
import com.framework.common.validate.ParamsMapValidate;
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
public class CoreEpPaymentConfServiceImple implements CoreEpPaymentConfService {

    @Autowired
    private CoreEpPaymentConfMapper coreEpPaymentConfMapper;

    @Autowired
    private CoreEpAccessService coreEpAccessService;


    // TODO: 2016/10/9 0009  创建同步数据,提交事务,开启新线程同步数据至运营平台
    @Override
    public  Result<Integer> create(Map map) {
        Result<Integer> result = new Result<Integer>();
        try {
            result.put(coreEpPaymentConfMapper.create(map));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库插入出错，"+e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Integer> add(Map map) {
        Result<Integer> result = new Result<Integer>();
        try {
            ParamsMapValidate.validate(map, generateCreatePaymentValidate());

        } catch (ParamsMapValidationException e) {
            log.warn("收款方式配置有误", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
           try {
               Integer  core_ep_id=coreEpAccessService.checkAccess_id(map.get("access_id"));
               if(null==core_ep_id){
                   return new Result<>(false, Result.PARAMS_ERROR, "参数access_id无效");
               }
               map.put("core_ep_id",core_ep_id);
               if(EpConstant.PaymentStatus.STATUS_NORMAL.equals(Integer.parseInt(map.get("status").toString()))){
                    if(null==isExists(map)){
                      create(map);
                    }else{
                        result.setError(Result.DB_FAIL, "同一种支付方式正常只能存在一种");
                    }
               }else{
                   create(map);
               }
               result.setSuccess();
           } catch (Exception e) {
               result.setFail();
               result.setError(Result.DB_FAIL, "参数无效");
           }
        return result;
    }

    @Override
    public Result<Integer> update(Map map) {
        Result<Integer> result = new Result<Integer>();
        try {
            ParamsMapValidate.validate(map, generateCreatePaymentValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("收款方式配置有误", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            Integer  core_ep_id=coreEpAccessService.checkAccess_id(map.get("access_id"));
            if(null==core_ep_id){
                return new Result<>(false, Result.PARAMS_ERROR, "参数access_id无效");
            }
            map.put("core_ep_id",core_ep_id);
            if(EpConstant.PaymentStatus.STATUS_NORMAL.equals(Integer.parseInt(map.get("status").toString()))){
                if(null==isExists(map)){
                    result.put(updateStatus(map).get());
                }else{
                    result.setError(Result.DB_FAIL, "同一种支付方式正常只能存在一种");
                }
            }else{
                result.put(updateStatus(map).get());
            }
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "参数无效");
        }
        return result;
    }

    @Override
    public Result<Integer> updateStatus(Map map) {
        Result<Integer> result = new Result<Integer>();
        try {
            result.put(coreEpPaymentConfMapper.updateStatus(map));
            result.setSuccess();
        } catch (Exception e) {
        result.setFail();
        result.setError(Result.DB_FAIL, "参数无效");
        }
        return result;
    }

    @Override
    public CoreEpPaymentConf findById(Integer core_ep_id) {
        return coreEpPaymentConfMapper.findById(core_ep_id);
    }

    @Override
    public List<CoreEpPaymentConf> isExists(Map map) {
        return coreEpPaymentConfMapper.isExists(map);
    }
    private Map<String[], ValidRule[]> generateCreatePaymentValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "access_id", //
                "payment_type", //
                "conf_data", //
                "status",
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "payment_type", // 订单子产品ID
                "status",
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

}
