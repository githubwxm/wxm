package com.all580.role.dao;

import com.all580.role.entity.Func;

import java.util.List;
import java.util.Map;

public interface FuncMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Func record);

    int insertSelective(Map<String,Object> params);

    Func selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Map<String,Object> params);

    int updateByPrimaryKey(Func record);

    List<Map<String,Object>> getAll();

    List<Long> selectPidRefId(List<Long> pids);

    int deletePidAll(List<Long> ids);

}