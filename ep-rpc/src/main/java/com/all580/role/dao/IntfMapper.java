package com.all580.role.dao;

import com.all580.role.entity.Intf;

public interface IntfMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Intf record);

    int insertSelective(Intf record);

    Intf selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Intf record);

    int updateByPrimaryKey(Intf record);
}