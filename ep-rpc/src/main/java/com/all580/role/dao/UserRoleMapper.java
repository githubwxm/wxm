package com.all580.role.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserRoleMapper {

    int deleteByPrimaryKey(Integer id);


    int insert(Map record);


    Map selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Map record);

    int insertSelective(Map<String, Object> map);

    int checkName(Map map);
    int updateByPrimaryKeySelective(Map map);
    Map selectId(int id);
    List<Map> selectList();
    List<Map> selectEpType(@Param("ep_type") Integer ep_type);
}