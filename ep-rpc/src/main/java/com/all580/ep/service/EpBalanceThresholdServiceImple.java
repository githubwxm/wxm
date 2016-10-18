package com.all580.ep.service;

import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.EpBalanceThresholdMapper;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class EpBalanceThresholdServiceImple implements EpBalanceThresholdService {

    @Autowired
    private EpBalanceThresholdMapper coreEpAccessMapper;


    @Override
    public  Result<Integer> createOrUpdate(Map map) {
        Result<Integer> result = new Result<>();
        try {
            result.put(coreEpAccessMapper.createOrUpdate(map));
            result.setSuccess();
        } catch (Exception e) {
           // log.error("添加更新余额阀值出错", e);
            throw new ApiException("添加更新余额阀值出错", e);
        }
        return result;
    }



    @Override
    public  Result<Map> select(Map map) {
        Result<Map> result = new Result<>();
        try {
            result.put(coreEpAccessMapper.select(map));
            result.setSuccess();
        } catch (Exception e) {
           // log.error("查询余额阀值出错", e);
            throw new ApiException("查询余额阀值出错", e);
        }
        return result;
    }

    @Override
    public boolean warn(Map map) {
        try {
            Result<Map> result = select(map);
            Integer balance=Common.objectParseInteger(map.get("balance"));//传来的余额
            Integer threshold = Common.objectParseInteger(result.get("threshold"));//阀值
            if(null==result.get()){
               // log.error("未查询到阀值数据"+map);
                throw new ApiException("添加更新余额阀值出错"+map);
            }
            return balance<threshold;
        } catch (Exception e) {
           // log.error("查询数据库出错", e);
            throw new ApiException("查询数据库出错", e);
        }

    }

}
