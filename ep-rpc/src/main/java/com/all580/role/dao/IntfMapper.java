package com.all580.role.dao;


import org.apache.ibatis.annotations.Param;

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

    //  查询平台与企业的权限
    List<String> authIntf(@Param("ep_id") int ep_id,@Param("core_ep_id") int core_ep_id);
    List<String> authCoreIntf(int core_ep_id);
    List<Map<String,Object>> intfList(Map<String,Object> params);
}