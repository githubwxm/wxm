package com.all580.ep.service;

import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.CoreEpAccessMapper;

import com.framework.common.Result;
import com.framework.common.exception.ApiException;
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
           // log.error("创建中心平台接口访问配置", e);
            throw new ApiException("创建中心平台接口访问配置", e);
        }
        return result;
    }

    @Override
    public Result<List<Map>> select(Map params) {
        Result<List<Map>> result = new Result<>();
        try {
            result.put(coreEpAccessMapper.select(params));
            result.setSuccess();
        } catch (Exception e) {
           // log.error("查询中心平台接口访问配置", e);
            throw new ApiException("查询中心平台接口访问配置", e);
        }
        return result;//CoreEpAccess
    }

    @Override
    public Result<Integer> checkAccessId(Object access_id){
        try {
            Map map = new HashMap();
            map.put("access_id",access_id);
            Result<List<Map>> access=  select(map);//校验access_id
            if(null==access.get()){
                throw new ApiException("查询access_id为null");
            }else{
                Result<Integer> result = new Result<>(true);
                result.put(Common.objectParseInteger(access.get().get(0).get("id")));
                return  result ;
            }
        } catch (Exception e) {
           // log.error("查询中心平台接口访问配置", e);
            throw new ApiException("查询中心平台接口访问配置", e);
        }

    }
}
