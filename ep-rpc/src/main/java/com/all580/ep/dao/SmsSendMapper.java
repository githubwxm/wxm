package com.all580.ep.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SmsSendMapper {
    int insert(Map<String,Object> map);
    int updateByPrimaryKey(Map<String,Object> map);
    Map<String,Object> selectByEpId(@Param("ep_id") int ep_id,@Param("core_ep_id")int core_ep_id);
    List<Map<String,Object>> selectThresholdList(Map<String,Object> map);
    int selectThresholdCount(Map<String,Object> map);
    int updateSmSSendInit(Map<String,Object> map);
    List<Map<String,Object>> selectThreshold();
    int sendUpdateNumber(Map<String,Object> map);
}