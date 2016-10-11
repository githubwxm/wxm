package com.all580.ep.service;

import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.CoreEpAccessMapper;

import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    public Result<Map> select(Map params) {
        Result<Map> result = new Result<>();
        try {
            result.put(coreEpAccessMapper.select(params));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库查询出错。出错原因：***************");
        }
        return result;//CoreEpAccess
    }
    public Integer checkAccess_id(Object access_id){
        Map map = new HashMap();
        map.put("access_id",access_id);
        Result<Map> access=  select(map);//校验access_id
        if(null==access.get()){
           return null;
        }else{
            return   Common.objectParseInteger(access.get().get("id"));
        }
    }
}
