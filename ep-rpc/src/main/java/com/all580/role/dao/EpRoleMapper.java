package com.all580.role.dao;


import java.util.Map;

public interface EpRoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Map<String,Object> record);

    int insertSelective(Map<String,Object> params);

    Map<String,Object> selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(Map<String,Object> record);

    int updateByPrimaryKey(Map<String,Object> record);

    int checkName(String name);
}