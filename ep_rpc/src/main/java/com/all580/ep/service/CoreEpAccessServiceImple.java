package com.all580.ep.service;

import com.all580.ep.api.entity.CoreEpAccess;
import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.api.service.EpService;
import com.all580.ep.dao.CoreEpAccessMapper;
import com.all580.ep.dao.EpMapper;

import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class CoreEpAccessServiceImple implements CoreEpAccessService {

    @Autowired
    private CoreEpAccessMapper coreEpAccessMapper;

    @Override
    public Result<Integer> create(Map map) {
        Result<Integer> result = new Result<Integer>();
        try {
            result.put( coreEpAccessMapper.create(map));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库查询出错。出错原因：***************");
        }
        return result;

    }

    @Override
    public Result<CoreEpAccess> select(Map params) {
        Result<CoreEpAccess> result = new Result<CoreEpAccess>();
        try {
            result.put(coreEpAccessMapper.select(params));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库查询出错。出错原因：***************");
        }
        return result;
    }

}
