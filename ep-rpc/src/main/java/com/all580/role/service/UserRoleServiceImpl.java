package com.all580.role.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.com.Common;
import com.all580.manager.SyncEpData;
import com.all580.role.api.service.UserRoleService;
import com.all580.role.dao.UserRoleFuncMapper;
import com.all580.role.dao.UserRoleMapper;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2017/7/3 0003.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class UserRoleServiceImpl  implements UserRoleService {


    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserRoleFuncMapper userRoleFuncMapper;
    @Autowired
    private SyncEpData syncEpData;

    public Result addUserRole(Map<String, Object> params) {
        Result result = new Result(true);
        try {
            int ref = userRoleMapper.checkName(params);
            if (ref > 0) {
                log.error("角色名字已存在 {}", params.get("name"));
                return new Result(false,Result.NAME_UNIQUE_KEY_ERROR, "角色名字已存在");
            }
            userRoleMapper.insertSelective(params);
            Integer user_role_id = CommonUtil.objectParseInteger(params.get("id"));
            result.put(user_role_id);
            syncEpData.syncEpAllData(EpConstant.Table.T_USER_ROLE, userRoleMapper.selectId(user_role_id));
        } catch (ApiException e1) {
            throw new ApiException(e1.getMessage());
        } catch (Exception e) {
            log.error("添加角色出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return result;  //addEpRoleFunc
    }
    public Result selectUserRoleList(){
        Result result = new Result(true);
        result.put(userRoleMapper.selectList());
        return result;
    }
    public Result selectUserRoleTypeList(int ep_type){
        Result result = new Result(true);
        result.put(userRoleMapper.selectEpType(ep_type));
        return result;
    }

    public Result selectUserId(int id){
        Result result = new Result(true);
        result.put(userRoleMapper.selectId(id));
        return result;
    }

    public Result updateUserRole(Map<String, Object> params) {
        Result result = new Result(true);
        try {
            int ref = userRoleMapper.checkName(params);
            if (ref > 0) {
                log.error("角色名字已存在 {}", params.get("name"));
                return new Result(false,Result.NAME_UNIQUE_KEY_ERROR, "角色名字已存在");
            }
            userRoleMapper.updateByPrimaryKeySelective(params);
            Integer user_role_id = CommonUtil.objectParseInteger(params.get("id"));
            result.put(user_role_id);
            syncEpData.syncEpAllData(EpConstant.Table.T_USER_ROLE, userRoleMapper.selectId(user_role_id));
        } catch (ApiException e1) {
            throw new ApiException(e1.getMessage());
        } catch (Exception e) {
            log.error("添加角色出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return result;  //addEpRoleFunc
    }
    public Result addUserRoleFunc(Map params){
        Result result = new Result(true);
        try {
            Integer user_role_id = CommonUtil.objectParseInteger(params.get("user_role_id"));
            Integer oper_id = CommonUtil.objectParseInteger(params.get("oper_id"));
            List<Integer> func_ids = (List<Integer>) params.get("func_ids");
            if(!(null == func_ids || func_ids.isEmpty())){
                if(!"".equals(func_ids.get(0))){
                    userRoleFuncMapper.insertUserRoleFuncBatch(user_role_id, oper_id,func_ids);
                    List list = userRoleFuncMapper.selectUserRoleIdFuncId(user_role_id,func_ids);
                    syncEpData.syncEpAllData(EpConstant.Table.T_USER_ROLE , list);
                }
            }

        } catch (Exception e) {
            log.error("添加菜单出错 {}", e.getMessage());
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }
        return result;  //
    }
    public Result deleteFuncId(int func_id){
        Result result = new Result(true);
        try {
          List list =  userRoleFuncMapper.selectFuncId(func_id);
            userRoleFuncMapper.deleteFuncId(func_id);
            syncEpData.syncDeleteAllData(EpConstant.Table.T_USER_ROLE_FUNC,(Integer [])list.toArray(new Integer[list.size()]) );
        } catch (Exception e) {
            log.error("删除组菜单功能异常", e);
            throw new ApiException("删除组菜单功能异常");
        }
        return result;
    }
    public Result selectUserRoleIdFunc(int user_role_id){
        Result result = new Result (true);
        result.put(userRoleFuncMapper.selectUserRoleIdFunc(user_role_id));
        return result;
    }
    public Result updateUserRoleFunc(Map<String, Object> params) {//物理删除
        try {
            Integer user_role_id = CommonUtil.objectParseInteger(params.get("user_role_id"));//角色
            Integer oper_id = CommonUtil.objectParseInteger(params.get("oper_id"));//操作人
            List<Integer> func_ids = (List<Integer>) params.get("func_ids");//需要存在的id数据
            // epRoleFuncMapper.updateEpRoleFuncIsDelete(ep_role_id, oper_id);//更新原始的 为delete状态
            List<Integer> initFunc=null;
            initFunc  = userRoleFuncMapper.selectUserRoleIdFuncId(user_role_id, null);//已经存在的数据
            if (!(null == initFunc || initFunc.isEmpty())) {//删除已经存在而不需要的数据
                List<Integer> deleteList = Common.deleteAllList(func_ids, initFunc);
                if(!deleteList.isEmpty()){
                    List<Integer> synDelete=  userRoleFuncMapper.selectUserRoleIdId(user_role_id, deleteList);
                    userRoleFuncMapper.deleteUserFunc(user_role_id,deleteList );
                    syncEpData.syncDeleteAllData(EpConstant.Table.T_USER_ROLE_FUNC,(Integer [])synDelete.toArray(new Integer[synDelete.size()]) );
                }
            }
            if(!(null==func_ids||func_ids.isEmpty())){
                initFunc  = userRoleFuncMapper.selectUserRoleIdFuncId(user_role_id, func_ids);//已经存在的数据
            }
            if(!(null == initFunc || initFunc.isEmpty())){
                Common.removeAllList(func_ids, initFunc);//删除已经存在且需要添加的数据
            }
            if (!(null == func_ids || func_ids.isEmpty())) {//需要添加的数据
                if(!"".equals(func_ids.get(0))){
                    userRoleFuncMapper.insertUserRoleFuncBatch(user_role_id, oper_id, func_ids);
                    List<Map<String,Object>>  list = userRoleFuncMapper.selectUserRoleId(user_role_id,func_ids);
                    if (!list.isEmpty()) {
                        syncEpData.syncEpAllData(EpConstant.Table.T_USER_ROLE_FUNC, list);
                    }
                }
            }
//            List<Integer> tempList = new ArrayList<Integer>();
//            tempList.add(user_role_id);
            //epRoleFuncMapper.selectFuncIdsAllEpRole(tempList)
          //  Auth.updateAuthMap( tempList,redisUtils);//更新鉴权数据
        } catch (Exception e) {
            log.error("修改菜单出错 {}", e.getMessage());
            return new Result(false, e.getMessage());
        }
        return new Result(true);
    }

}
