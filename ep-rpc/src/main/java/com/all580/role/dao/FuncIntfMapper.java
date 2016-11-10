package com.all580.role.dao;

import com.all580.role.entity.FuncIntf;

import java.util.List;
import java.util.Map;

public interface FuncIntfMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FuncIntf record);

    int insertSelective(FuncIntf record);

    FuncIntf selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FuncIntf record);

    int updateByPrimaryKey(FuncIntf record);

    /**
     * 获取某个功能下面的所有接口
     * @param func_id
     * @return
     */
    List<Map<String,Object>> selectFuncIntf(int func_id);
}