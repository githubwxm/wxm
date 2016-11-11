package com.all580.role.service;

import com.all580.role.api.service.FuncService;
import com.all580.role.dao.FuncMapper;
import com.framework.common.Result;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/11/10 0010.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class FuncServiceImple implements FuncService{
    @Autowired
    private FuncMapper funcMapper;



    @Override
    public Result<List<Map<String, Object>>> getAll() {
        Result<List<Map<String, Object>>> result = new Result<>(true);
        try{
            result.put(funcMapper.getAll());
        }catch (Exception e){
            log.error("查询菜单功能异常",e);
            new ApiException("查询菜单功能异常");
        }
        return result;
    }

    @Override
    public Result insertSelective(Map<String, Object> params) {
        try{
            funcMapper.insertSelective(params);
        }catch (Exception e){
            log.error("添加菜单功能异常",e);
            new ApiException("添加菜单功能异常");
        }
        return new Result(true);
    }

    @Override
    public Result updateByPrimaryKeySelective(Map<String, Object> params) {
        try{
            funcMapper.updateByPrimaryKeySelective(params);
        }catch (Exception e){
            log.error("修改菜单功能异常",e);
            new ApiException("修改菜单功能异常");
        }
        return new Result(true);
    }

    @Override
    public Result deleteByPrimaryKey(Long id) {
        try{
            List<Long> list = new ArrayList<>();
            list.add(id);
            deletePid(list);
        }catch (Exception e){
            log.error("删除菜单功能异常",e);
            new ApiException("删除菜单功能异常");
        }
        return new Result(true);
    }

    private void deletePid(List<Long> list){
        if(null==list||list.isEmpty()){

        }else{
            List<Long> ids= funcMapper.selectPidRefId(list);
            funcMapper.deletePidAll(list);
            deletePid(ids);
        }

    }

}
