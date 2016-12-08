package com.all580.role.dao;


import java.util.List;
import java.util.Map;

public interface IntfMapper {
    int deleteByPrimaryKey(int id);
    int deleteByFuncId(int func_id);
    int insert(Map<String,Object> record);

    int insertSelective(Map<String,Object> record);

    Map<String,Object> selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(Map<String,Object> record);

    int updateByPrimaryKey(Map<String,Object> record);

    /**
     * @param params  name,auth   --  ,status
     * @return
     */
    int insertIntf(Map<String,Object> params);

    /**
     * 查询菜单下的所有
     */
    List<Map<String,Object>> selectFuncId(Map<String,Object> prarms);
     int selectFuncIdCount(Map<String,Object> prarms);
    int intListCount();
    List<String> authIntf(int ep_role);
    List<Map<String,Object>> intfList(Map<String,Object> params);
}