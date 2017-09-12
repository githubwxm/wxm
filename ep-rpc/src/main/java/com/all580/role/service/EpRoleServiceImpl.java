package com.all580.role.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.com.Common;
import com.all580.manager.SyncEpData;
import com.all580.role.api.service.EpRoleService;
import com.all580.role.dao.EpRoleFuncMapper;
import com.all580.role.dao.EpRoleMapper;
import com.framework.common.Result;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.util.Auth;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/11/11 0011.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class EpRoleServiceImpl implements EpRoleService {

    @Autowired
    private EpRoleMapper epRoleMapper;

    @Autowired
    private EpRoleFuncMapper epRoleFuncMapper;

    @Autowired
    private SyncEpData syncEpData;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Result deleteFuncIdsEpRole(List<Integer> list){
        Result result = new Result();
        if(list!=null&&!list.isEmpty()){
            Auth.updateAuthMap(null,redisUtils);//更新鉴权数据
          //Auth.updateAuthMap(epRoleFuncMapper.selectFuncIdsAllEpRole(list),redisUtils);//更新鉴权数据
          List<Integer>  synDate = epRoleFuncMapper.selectFuncIdsAllEpRoleIds(list);//同步删除数据
            epRoleFuncMapper.deleteFuncIdsEpRole(list);//删除
            syncEpData.syncDeleteAllData(EpConstant.Table.T_EP_ROLE_FUNC,(Integer [])synDate.toArray(new Integer[synDate.size()]) );
            result.setSuccess();
        }
       return result;
    }
    @Override
    public Result addEpRole(Map<String, Object> params) {
        Result result = new Result(true);
        try {
            int ref = epRoleMapper.checkName(params);
            if (ref > 0) {
                log.error("角色名字已存在 {}", params.get("name"));
                //throw new ApiException("角色名字已存在");
                return new Result(false,Result.NAME_UNIQUE_KEY_ERROR, "角色名字已存在");
            }
            epRoleMapper.insertSelective(params);
            Integer ep_role_id = CommonUtil.objectParseInteger(params.get("id"));
            result.put(ep_role_id);
            syncEpData.syncEpAllData(EpConstant.Table.T_EP_ROLE, epRoleMapper.select(ep_role_id));
        } catch (ApiException e1) {
            throw new ApiException(e1.getMessage());
        } catch (Exception e) {
            log.error("添加角色出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return result;  //addEpRoleFunc
    }

    @Override
    public Result select(Integer id) {
        Result result = new Result(true);
        try {
            result.put(epRoleMapper.select(id));
        } catch (Exception e) {
            log.error("查询角色出错 {}", e.getMessage());
            throw new ApiException("查询角色出错");
        }
        return result;
    }

    @Override
    public Result updateEpRole(Map<String, Object> params) {
        try {
            int ref = epRoleMapper.checkName(params);
            if (ref > 0) {
                log.error("角色名字已存在 {}", params.get("name"));
                return new Result(false,Result.NAME_UNIQUE_KEY_ERROR, "角色名字已存在");
            }
            ref = epRoleMapper.updateByPrimaryKeySelective(params);
            if (ref >0) {
                Integer id = CommonUtil.objectParseInteger(params.get("id"));
                syncEpData.syncEpAllData(EpConstant.Table.T_EP_ROLE, epRoleMapper.select(id));
            }
        } catch (Exception e) {
            log.error("修改角色出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return new Result(true);
    }

    @Override
    public Result selectepRoleId(int ep_role_id) {
        Result result = new Result(true);
        try {
            result.put(epRoleFuncMapper.selectepRoleId(ep_role_id,null));
        } catch (Exception e) {
            log.error("查询角色出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return result;
    }

    @Override
    public Result selectRoleFunc(int ep_role_id) {
        Result result = new Result(true);
        try {
            result.put(epRoleFuncMapper.selectRoleFunc(ep_role_id));
        } catch (Exception e) {
            log.error("查询角色出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return result;
    }

    @Override
    public Result selectList(Map<String, Object> params) {
        Result result = new Result(true);
        Map<String, Object> resultMap = new HashMap<>();
        try {
            int ref = epRoleMapper.selectListCount();
            resultMap.put("totalCount", ref);
            if (ref < 1) {
                resultMap.put("list", new ArrayList<>());
                result.put(resultMap);
                return result;
            }
            Common.checkPage(params);
            List list = epRoleMapper.selectList(params);
            resultMap.put("list", list);
            result.put(resultMap);
        } catch (Exception e) {
            log.error("查询角色列表出错 {}", e.getMessage());
            throw new ApiException("查询角色列表出错");
        }
        return result;
    }

    @Override
    public Result addEpRoleFunc(Map<String, Object> params) {
        Result result = new Result(true);
        try {
            Integer ep_role_id = CommonUtil.objectParseInteger(params.get("ep_role_id"));
            Integer oper_id = CommonUtil.objectParseInteger(params.get("oper_id"));
            List<Integer> func_ids = (List<Integer>) params.get("func_ids");
            if(!(null == func_ids || func_ids.isEmpty())){
                if(!"".equals(func_ids.get(0))){
                    epRoleFuncMapper.insertEpRoleFuncBatch(ep_role_id, oper_id, func_ids);
                    List list = epRoleFuncMapper.selectepRoleId(ep_role_id,func_ids);
                        syncEpData.syncEpAllData(EpConstant.Table.T_EP_ROLE_FUNC , list);
                }
            }

        } catch (Exception e) {
            log.error("添加菜单出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return result;  //
    }

    public Result updateEpRoleFunc(Map<String, Object> params) {//物理删除
        try {
            Integer ep_role_id = CommonUtil.objectParseInteger(params.get("ep_role_id"));//角色
            Integer oper_id = CommonUtil.objectParseInteger(params.get("oper_id"));//操作人
            List<Integer> func_ids = (List<Integer>) params.get("func_ids");//需要存在的id数据
            // epRoleFuncMapper.updateEpRoleFuncIsDelete(ep_role_id, oper_id);//更新原始的 为delete状态
            List<Integer> initFunc=null;
             initFunc  = epRoleFuncMapper.selectEpRoleIdFuncId(ep_role_id, null);//已经存在的数据
            if (!(null == initFunc || initFunc.isEmpty())) {//删除已经存在而不需要的数据
                List<Integer> deleteList =Common.deleteAllList(func_ids, initFunc);
                if(!deleteList.isEmpty()){
                    List<Integer> synDelete=  epRoleFuncMapper.selectEpRoleIdId(ep_role_id, deleteList);
                    epRoleFuncMapper.deleteEpFunc(ep_role_id,deleteList );
                    syncEpData.syncDeleteAllData(EpConstant.Table.T_EP_ROLE_FUNC,(Integer [])synDelete.toArray(new Integer[synDelete.size()]) );
                }
            }
            if(!(null==func_ids||func_ids.isEmpty())){
                initFunc  = epRoleFuncMapper.selectEpRoleIdFuncId(ep_role_id, func_ids);//已经存在的数据
            }
            if(!(null == initFunc || initFunc.isEmpty())){
                Common.removeAllList(func_ids, initFunc);//删除已经存在且需要添加的数据
            }
            if (!(null == func_ids || func_ids.isEmpty())) {//需要添加的数据
                if(!"".equals(func_ids.get(0))){
                    epRoleFuncMapper.insertEpRoleFuncBatch(ep_role_id, oper_id, func_ids);
                    List<Map<String,Object>>  list = epRoleFuncMapper.selectepRoleId(ep_role_id,func_ids);
                    if (!list.isEmpty()) {
                        syncEpData.syncEpAllData(EpConstant.Table.T_EP_ROLE_FUNC, list);
                    }
                }
            }
            //List<Integer> tempList = new ArrayList<Integer>();
            //tempList.add(ep_role_id);
            //epRoleFuncMapper.selectFuncIdsAllEpRole(tempList)
            Auth.updateAuthMap( null,redisUtils);//更新鉴权数据
        } catch (Exception e) {
            log.error("修改菜单出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return new Result(true);
    }
    /**
     * 逻辑删除   先把原有状态置为删除状态  过滤出添加的状态已经存在的更新为正常状态 添加没有的数据
     */
  /*  @Override
    public Result updateEpRoleFunc(Map<String, Object> params) {//
        try {
            Integer ep_role_id = CommonUtil.objectParseInteger(params.get("ep_role_id"));//角色
            Integer oper_id = CommonUtil.objectParseInteger(params.get("oper_id"));//操作人
            List<Integer> func_ids = (List<Integer>) params.get("func_ids");
            epRoleFuncMapper.updateEpRoleFuncIsDelete(ep_role_id, oper_id);//更新原始的 为delete状态
            List<Integer> initFunc = epRoleFuncMapper.selectEpRoleIdFuncId(ep_role_id, func_ids);
            if (!(null == initFunc || initFunc.isEmpty())) {//添加的已经存在 功能 修改为正常
                epRoleFuncMapper.updateEpRoleFuncIsNotDelete(ep_role_id, initFunc);
                removeAllList(func_ids,initFunc);
            }
            if(!(null == func_ids || func_ids.isEmpty())){
                epRoleFuncMapper.insertEpRoleFuncBatch(ep_role_id, oper_id, func_ids);
            }
        } catch (Exception e) {
            log.error("修改菜单出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return new Result(true);
    }*/



}
