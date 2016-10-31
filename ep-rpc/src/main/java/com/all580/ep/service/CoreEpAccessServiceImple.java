package com.all580.ep.service;

import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.CoreEpAccessMapper;

import com.framework.common.Result;

import javax.lang.exception.ApiException;

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
    private CoreEpAccessMapper coreEpAccessMapper;//ddd

    @Override
    public Result<Integer> create(Map<String,Object> map) {
        Result<Integer> result = new Result<>();
        try {
            result.put(coreEpAccessMapper.create(map));
            result.setSuccess();
        } catch (Exception e) {
            log.error("创建中心平台接口访问配置", e);
            throw new ApiException("创建中心平台接口访问配置", e);
        }
        return result;
    }

    @Override
    public Result<Map<String,Object>> selectAccess(Map<String,Object> params) {
        Result<Map<String,Object>> result = new Result<>();
        try {
            result.put(coreEpAccessMapper.selectAccess(params));
            result.setSuccess();
        } catch (Exception e) {
            log.error("查询中心平台接口访问配置", e);
            throw new ApiException("查询中心平台接口访问配置", e);
        }
        return result;//CoreEpAccess
    }

    @Override
    public Result<List<String>> selectAccessList(List<Integer> ids) {
        Result<List<String>> result = new Result<>();
        try {
            List<String> list = coreEpAccessMapper.selectAccessList(ids);
            if (list.isEmpty()) {
                result.setError("未查询到数据");
            } else {
                result.put(list);
                result.setSuccess();
            }
        } catch (Exception e) {
            log.error("查询中心平台接口访问配置", e);
            throw new ApiException("查询中心平台接口访问配置", e);
        }
        return result;//CoreEpAccess
    }

    @Override
    public Result<List<Map<String,Object>>> select(Map<String,Object> params) {
        Result<List<Map<String,Object>>> result = new Result<>();
        try {
            result.put(coreEpAccessMapper.select(params));
            result.setSuccess();
        } catch (Exception e) {
            log.error("查询中心平台接口访问配置", e);
            throw new ApiException("查询中心平台接口访问配置", e);
        }
        return result;//CoreEpAccess
    }

    @Override
    public Result<Integer> checkAccessId(Object access_id) {
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("access_id", access_id);
            Result<List<Map<String,Object>>> access = select(map);//校验access_id
            if (null == access.get()) {
                throw new ApiException("查询access_id结果为null");
            } else {
                Result<Integer> result = new Result<>(true);
                result.put(Common.objectParseInteger(access.get().get(0).get("id")));
                return result;
            }
        } catch (Exception e) {
            log.error("查询中心平台接口访问配置", e);
            throw new ApiException("查询中心平台接口访问配置", e);
        }

    }
}
