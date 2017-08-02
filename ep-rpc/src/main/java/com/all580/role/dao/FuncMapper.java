package com.all580.role.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FuncMapper {
    int deleteByPrimaryKey(int id);

    int insert(Map<String,Object> record);

    int insertSelective(Map<String,Object> params);

    Map<String,Object> selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(Map<String,Object> params);

    int updateByPrimaryKey(Map<String,Object> record);

    List<Map<String,Object>> getAll();

    List<Integer> selectPidRefId(@Param("pids") List<Integer> pids);

    int deletePidAll(@Param("ids")List<Integer> ids);

    /**
     *
     * @return
     */
    List<Integer> selectTopPidFunc();

}