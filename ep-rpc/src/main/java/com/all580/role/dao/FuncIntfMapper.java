package com.all580.role.dao;

import com.all580.role.entity.FuncIntf;

public interface FuncIntfMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FuncIntf record);

    int insertSelective(FuncIntf record);

    FuncIntf selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FuncIntf record);

    int updateByPrimaryKey(FuncIntf record);
}