package com.all580.role.api.service;

import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/11/9 0009.
 */
public interface FuncService {
    /**
     * 获取树形功能菜单
     * @return
     */
    Result <List<Map<String,Object>>> getAll();

    /**
     * pid,name,alias,description,seq,status,create_time,type,path,funcId,target,icon
     * @param params
     * @return
     */
    Result insertSelective(Map<String,Object> params);

    Result updateByPrimaryKeySelective(Map<String,Object> params);

    Result deleteByPrimaryKey(int id);

}
