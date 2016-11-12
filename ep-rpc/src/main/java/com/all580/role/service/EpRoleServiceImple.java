package com.all580.role.service;

import com.all580.ep.com.Common;
import com.all580.role.api.service.EpRoleService;
import com.all580.role.dao.EpRoleFuncMapper;
import com.all580.role.dao.EpRoleMapper;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.sun.org.apache.bcel.internal.generic.LSTORE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/11/11 0011.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class EpRoleServiceImple implements EpRoleService {

    @Autowired
    private EpRoleMapper epRoleMapper;

    @Autowired
    private EpRoleFuncMapper epRoleFuncMapper;

    @Override
    public Result addEpRole(Map<String, Object> params) {
        Result result= new Result(true);
        try{
            epRoleMapper.insertSelective(params);
            Integer ep_role_id =CommonUtil.objectParseInteger(params.get("id"));
            result.put(ep_role_id);
        }catch (Exception e){
            log.error("添加角色出错 {}",e.getMessage());
            return new Result(false,e.getMessage());
        }
        return result;  //addEpRoleFunc
    }
    @Override
    public Result addEpRoleFunc(Map<String, Object> params) {
        Result result= new Result(true);
        try{
          Integer ep_role_id=CommonUtil.objectParseInteger(params.get("ep_role_id"));
            Integer oper_id = CommonUtil.objectParseInteger(params.get("oper_id")) ;
            List<Integer> func_ids =(List<Integer>)params.get("func_ids");
            epRoleFuncMapper.insertEpRoleFuncBatch(ep_role_id,oper_id,func_ids);
        }catch (Exception e){
            log.error("添加角色出错 {}",e.getMessage());
            return new Result(false,e.getMessage());
        }
        return result;  //
    }

    @Override
    public Result updateEpRole(Map<String, Object> params) {
        try {
            epRoleMapper.updateByPrimaryKeySelective(params);
        }catch (Exception e){
            log.error("修改角色出错 {}",e.getMessage());
            return new Result(false,e.getMessage());
        }
        return new Result(true);
    }

    @Override
    public Result updateEpRoleFunc(Map<String, Object> params) {
        try {
            Integer ep_role_id = CommonUtil.objectParseInteger(params.get("ep_role_id")) ;//角色
            Integer oper_id= CommonUtil.objectParseInteger(params.get("oper_id")) ;//操作人
            List<Integer> func_ids =(List<Integer>)params.get("func_ids");
            epRoleFuncMapper.updateEpRoleFuncIsDelete(ep_role_id,oper_id);//更新原始的 为delete状态
            List<Integer> initFunc= epRoleFuncMapper.selectEpRoleIdFuncId(ep_role_id,func_ids);
            if(!(null==initFunc||initFunc.isEmpty())){//添加的已经存在 功能 修改为正常
                epRoleFuncMapper.updateEpRoleFuncIsNotDelete(ep_role_id,initFunc);
                func_ids.removeAll(initFunc);
            }
            epRoleFuncMapper.insertEpRoleFuncBatch(ep_role_id,oper_id,func_ids);
        }catch (Exception e){
            log.error("修改角色出错 {}",e.getMessage());
            return new Result(false,e.getMessage());
        }
        return new Result(true);
    }
}
