package com.all580.role.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.manager.SyncEpData;
import com.all580.role.api.service.EpRoleService;
import com.all580.role.api.service.FuncGroupLinkService;
import com.all580.role.api.service.FuncService;
import com.all580.role.api.service.UserRoleService;
import com.all580.role.dao.FuncMapper;
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
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/11/10 0010.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class FuncServiceImpl implements FuncService{
    @Autowired
    private FuncMapper funcMapper;


    @Autowired
    private SyncEpData syncEpData;

    @Autowired
    private EpRoleService epRoleService;
    @Autowired
    private FuncGroupLinkService funcGroupLinkService;

    @Autowired
    UserRoleService UserRoleService;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public Result<List<Map<String, Object>>> getAll() {
        Result<List<Map<String, Object>>> result = new Result<>(true);
        try{
            result.put(funcMapper.getAll());
        }catch (Exception e){
            log.error("查询菜单功能异常",e);
            throw    new javax.lang.exception.ApiException("查询菜单功能异常");
        }
        return result;
    }

    @Override
    public Result insertSelective(Map<String, Object> params) {
        Result result= new Result(true);
        try{
            funcMapper.insertSelective(params);
            Integer id = CommonUtil.objectParseInteger(params.get("id"));
            result.put(id);
            funcGroupLinkService.addDefaultGroupLink(id);//添加默认组关联关系
            syncEpData.syncEpAllData(EpConstant.Table.T_FUNC,funcMapper.selectByPrimaryKey(id));
        }catch (Exception e){
            log.error("添加菜单功能异常",e);
            throw  new ApiException("添加菜单功能异常");
        }
        return result;
    }

    @Override
    public Result updateByPrimaryKeySelective(Map<String, Object> params) {
        try{
          int ref=  funcMapper.updateByPrimaryKeySelective(params);
            if(ref>0){
                Integer id = CommonUtil.objectParseInteger(params.get("id"));
                syncEpData.syncEpAllData(EpConstant.Table.T_FUNC, funcMapper.selectByPrimaryKey(id));
            }
        }catch (Exception e){
            log.error("修改菜单功能异常",e);
            throw  new ApiException("修改菜单功能异常");
        }
        return new Result(true);
    }

    @Override
    public Result deleteByPrimaryKey(int id) {
        try{
            List<Integer> list = new ArrayList<>();
            list.add(id);
            list.addAll(deletePid(list,null)) ;
            funcMapper.deletePidAll(list);
            syncEpData.syncDeleteAllData(EpConstant.Table.T_FUNC,(Integer [])list.toArray(new Integer[list.size()]) );
            // 查找  菜单对应的角色  同步到鉴权
            // 同步删除角色对应菜单数据
            funcGroupLinkService.deleteFuncId(id);
            epRoleService.deleteFuncIdsEpRole(list);
            UserRoleService.deleteFuncId(id);
            Auth.updateAuthMap(null,redisUtils);//更新鉴权数据
        }catch (Exception e){
            log.error("删除菜单功能异常",e);
            throw  new ApiException("删除菜单功能异常");
        }
        return new Result(true);
    }

//    private void deletePid(List<Integer> list){//  list 1  list 22  33
//        if(null==list||list.isEmpty()){
//
//        }else{
//            List<Integer> ids= funcMapper.selectPidRefId(list);// ids  22  33     ids  222 3333
//            funcMapper.deletePidAll(list);// list 1 delete   22  33
//            deletePid(ids);  // ids  22   33 - 222  333
//        }
//    }

    private List<Integer> deletePid(List<Integer> list, List<Integer> resultList){
        if(resultList==null){
            resultList=new ArrayList<>();
        }
        List<Integer> ids= funcMapper.selectPidRefId(list);
        if(ids==null||ids.isEmpty()){
            return  resultList;
        }
        resultList.addAll(ids);
        deletePid(ids, resultList);
        return  resultList;
    }

}
