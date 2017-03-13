package com.all580.ep.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SmsSendMapper {
    int insert(Map<String,Object> map);
    int updateByPrimaryKey(Map<String,Object> map);
    Map<String,Object> selectByEpId(int ep_id);
    List<Map<String,Object>> selectThresholdList(Map<String,Object> map);
    int selectThresholdCount(Map<String,Object> map);
}