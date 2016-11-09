package com.all580.role.dao;

import com.all580.role.entity.Func;

import java.util.List;
import java.util.Map;

public interface FuncMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Func record);

    int insertSelective(Func record);

    Func selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Func record);

    int updateByPrimaryKey(Func record);

    List<Map<String,Object>> getAll();
}