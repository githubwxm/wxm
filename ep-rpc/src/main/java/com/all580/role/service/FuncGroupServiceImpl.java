package com.all580.role.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.manager.SyncEpData;
import com.all580.role.api.service.FuncGroupLinkService;
import com.all580.role.api.service.FuncGroupService;
import com.all580.role.dao.FuncGroupMapper;
import com.framework.common.Result;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.util.Auth;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by wxming on 2017/6/29 0029.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class FuncGroupServiceImpl implements FuncGroupService {

    @Autowired
    FuncGroupMapper funcGroupMapper;
    @Autowired
    FuncGroupLinkService funcGroupLinkService;
    @Autowired
    private SyncEpData syncEpData;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public Result selectFuncGroupList() {
        Result result = new Result(true);
        try {
            result.put(funcGroupMapper.selectFuncGroupList());
        } catch (Exception e) {
            log.error("查询组菜单功能异常", e);
            throw new javax.lang.exception.ApiException("查询组菜单功能异常");
        }
        return result;
    }

    public Result updateFuncGroup(Map<String, Object> map) {
        Result result = new Result(true);
        try {
            result.put(funcGroupMapper.updateByPrimaryKeySelective(map));
            syncEpData.syncEpAllData(EpConstant.Table.T_FUNC_GROUP, funcGroupMapper.selectByPrimaryKey(CommonUtil.objectParseInteger(map.get("id"))));
        } catch (Exception e) {
            log.error("修改组菜单功能异常", e);
            throw new javax.lang.exception.ApiException("修改组菜单功能异常");
        }
        return result;
    }

    public Result addFuncGroup(Map<String, Object> map) {
        Result result = new Result(true);
        try {
            result.put(funcGroupMapper.insertSelective(map));
            syncEpData.syncEpAllData(EpConstant.Table.T_FUNC_GROUP, funcGroupMapper.selectByPrimaryKey(CommonUtil.objectParseInteger(map.get("id"))));
        } catch (Exception e) {
            log.error("添加组菜单功能异常", e);
            throw new javax.lang.exception.ApiException("添加组菜单功能异常");
        }
        return result;
    }

    public Result selectFuncGroupId(int id) {
        Result result = new Result(true);
        try {
            result.put(funcGroupMapper.selectByPrimaryKey(id));
        } catch (Exception e) {
            log.error("查询组菜单功能异常", e);
            throw new javax.lang.exception.ApiException("查询组菜单功能异常");
        }
        return result;
    }


    public Result deleteFuncGroup(int id) {
        Result result = new Result(true);
        try {
            result.put(funcGroupMapper.deleteByPrimaryKey(id));
            syncEpData.syncDeleteAllData(EpConstant.Table.T_FUNC_GROUP,new Integer []{id} );
            funcGroupLinkService.deleteFuncGroupId(id);//删除组删除 组对应的关系
            Auth.updateAuthMap(null,redisUtils);//更新鉴权数据
        } catch (Exception e) {
            log.error("删除组菜单功能异常", e);
            throw new javax.lang.exception.ApiException("删除组菜单功能异常");
        }
        return result;
    }


}
