package com.all580.role.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.com.Common;
import com.all580.manager.SyncEpData;
import com.all580.role.api.service.FuncGroupLinkService;
import com.all580.role.dao.FuncGroupLinkMapper;
import com.all580.role.dao.FuncMapper;
import com.all580.role.dao.PlatFuncMapper;
import com.framework.common.Result;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.util.Auth;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2017/6/29 0029.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class FuncGroupLinkServiceImpl implements FuncGroupLinkService {

    @Autowired
    FuncGroupLinkMapper funcGroupLinkMapper;
    @Autowired
    PlatFuncMapper platFuncMapper;
    @Autowired
    FuncMapper funcMapper;
    @Autowired
    private SyncEpData syncEpData;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * -- 删除掉已经存在的 组对应菜单直接添加新的 ， 同步时每次都要同步同步组对应的 --
     * 添加时过滤掉已经存在的，只删除需要删除的
     * @param map
     * @return
     */
    public Result addFuncGroupLink(Map<String, Object> map) {
        Result result = new Result();
        try {
            Integer func_group_id = CommonUtil.objectParseInteger(map.get("func_group_id"));
            List func_ids = (List) map.get("func_ids");
            List<Integer> currentList = funcGroupLinkMapper.selectFuncGroupLinkFuncId(func_group_id);//已经存在 默认的需要添加
            List<Integer> deleteList=   Common.deleteAllList(func_ids,currentList);//最小化删除菜单id
            if(!deleteList.isEmpty()){
                Auth.updateAuthMap( platFuncMapper.selectCoreEpIdFunc(deleteList),redisUtils);//更新鉴权数据
                List<Integer> synDeleteList=funcGroupLinkMapper.selectFuncGroupLinkId(func_group_id,deleteList);//获取需要删除的同步id
                funcGroupLinkMapper.deleteFuncGroupLinkList(func_group_id,deleteList);
                syncEpData.syncDeleteAllData(EpConstant.Table.T_FUNC_GROUP_LINK,(Integer [])synDeleteList.toArray(new Integer[synDeleteList.size()]) );
            }
            Common.removeAllList(func_ids,currentList);
            if (func_ids != null && !func_ids.isEmpty()) {
                funcGroupLinkMapper.addFuncGroupLinkList(func_group_id, func_ids);
                Auth.updateAuthMap( platFuncMapper.selectCoreEpIdFunc(func_ids),redisUtils);//更新鉴权数据
                syncEpData.syncEpAllData(EpConstant.Table.T_FUNC_GROUP_LINK, funcGroupLinkMapper.selectFuncGroupLinkMap(func_group_id,func_ids));
            }//

            changeDefault(deleteList, func_ids);
        } catch (Exception e) {
            log.error("添加组关联菜单出错 {}", e.getMessage());
            throw new ApiException("添加组关联菜单出错");
        }
        result.setSuccess();
        return result;
    }

    public Result addDefaultGroupLink(int func_id) {
        Result result = new Result();
        try {
            Integer func_group_id = funcGroupLinkMapper.selectFuncGroupLinkDefaultId();
            Map map = new HashMap();
            map.put("func_group_id", func_group_id);
            map.put("func_id", func_id);
            funcGroupLinkMapper.insertSelective(map);
            Auth.updateAuthMap( platFuncMapper.selectCoreEpIdFunc(Arrays.asList(func_id)),redisUtils);//更新鉴权数据
            syncEpData.syncEpAllData(EpConstant.Table.T_FUNC_GROUP_LINK, funcGroupLinkMapper.selectByPrimaryKey(CommonUtil.objectParseInteger(map.get("id"))));
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
        result.setSuccess();
        return result;
    }
    public  Result deleteFuncId(int func_id){
        Result result = new Result();
        try {
           List<Integer> synDeleteList= funcGroupLinkMapper.selectFuncIdLink(func_id);
            Auth.updateAuthMap( platFuncMapper.selectCoreEpIdFunc(Arrays.asList(func_id)),redisUtils);//更新鉴权数据
            funcGroupLinkMapper.deleteFuncId(func_id);
            syncEpData.syncDeleteAllData(EpConstant.Table.T_FUNC_GROUP_LINK,(Integer [])synDeleteList.toArray(new Integer[synDeleteList.size()]) );
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
        result.setSuccess();
        return result;
    }

    public Result deleteFuncGroupId(int func_group_id) {
        Result result = new Result();
        try {
            List<Integer> addList = funcGroupLinkMapper.selectFuncGroupLinkFuncId(func_group_id);//已经存在 默认的需要添加
            Auth.updateAuthMap( platFuncMapper.selectCoreEpIdFunc(addList),redisUtils);//更新鉴权数据
            List<Integer> synDeleteList= funcGroupLinkMapper.selectFuncGroupLinkGroupId(func_group_id);
            if(synDeleteList!=null && !synDeleteList.isEmpty()){
                funcGroupLinkMapper.deleteFuncGroupId(func_group_id);
                syncEpData.syncDeleteAllData(EpConstant.Table.T_FUNC_GROUP_LINK,(Integer [])synDeleteList.toArray(new Integer[synDeleteList.size()]) );
            }
            changeDefault(addList, null);
            result.setSuccess();
        } catch (Exception e) {
            log.error("删除数据异常", e);
            throw new ApiException("删除数据异常");
        }
        return result;
    }

    /**
     * 每次修改组对应菜单  都要修改默认菜单
     */
    private void changeDefault(List addList, List deleteList) {
        try {
            Integer func_group_id = funcGroupLinkMapper.selectFuncGroupLinkDefaultId();
            if (addList != null && !addList.isEmpty()) {//需查询不在这个也不在另外的组
                List current = funcGroupLinkMapper.selectFuncId(addList);
                Common.removeAllList(addList,current);
                if( !addList.isEmpty()){
                    funcGroupLinkMapper.addFuncGroupLinkList(func_group_id, addList);
                    syncEpData.syncEpAllData(EpConstant.Table.T_FUNC_GROUP_LINK, funcGroupLinkMapper.selectFuncGroupLinkMap(func_group_id,addList));
                }
            }

            if (deleteList != null && !deleteList.isEmpty()) {//删除不变
                List<Integer> pidList= funcMapper.selectTopPidFunc();
                Common.removeAllList(deleteList,pidList);//去掉 一级菜单的删除
                List<Integer> synDeleteList=funcGroupLinkMapper.selectFuncGroupLinkId(func_group_id,deleteList);//获取需要删除的同步id
                if(synDeleteList != null && !synDeleteList.isEmpty()){
                    funcGroupLinkMapper.deleteFuncGroupLinkList(func_group_id, deleteList);
                    syncEpData.syncDeleteAllData(EpConstant.Table.T_FUNC_GROUP_LINK,(Integer [])synDeleteList.toArray(new Integer[synDeleteList.size()]) );
                }
            }
        } catch (Exception e) {
            log.error("变更默认组对应菜单 {}", e.getMessage());
            throw new ApiException("变更默认组对应菜单");
        }


    }

    public Result selectFuncGroupLink(int func_group_id) {
        Result result = new Result();
        try {
            result.put(funcGroupLinkMapper.selectFuncGroupLink(func_group_id));
            result.setSuccess();
        } catch (Exception e) {
            log.error("查询组关联菜单 {}", e.getMessage());
            throw new ApiException("查询组关联菜单");
        }
        return result;
    }

    public Result selectPlatFromFuncGroupLink(int core_ep_id) {
        Result result = new Result();
        try {
            result.put(funcGroupLinkMapper.selectPlatFromFuncGroupLink(core_ep_id));
        } catch (Exception e) {
            log.error("查询平台关联菜单 {}", e.getMessage());
            throw new ApiException("查询平台关联菜单");
        }
        result.setSuccess();
        return result;
    }

    public Result selectPlatFromFuncGroup(int ep_func_type, int core_ep_id) {
        Result result = new Result();
        try {
            result.put(funcGroupLinkMapper.selectPlatFromFuncGroup(ep_func_type, core_ep_id));
        } catch (Exception e) {
            log.error("查询平台关联组 {}", e.getMessage());
            throw new ApiException("查询平台关联组");
        }
        result.setSuccess();
        return result;
    }

}
