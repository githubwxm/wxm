package com.all580.ep.service;

import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.EpBalanceThresholdMapper;
import com.framework.common.Result;
import com.framework.common.validate.ValidRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
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
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库查询出错"+e);
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
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库查询出错"+e);
        }
        return result;
    }

}
