package com.all580.ep.dao;


import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface EpBalanceThresholdMapper {
   int createOrUpdate(Map<String,Object> map);
    Map<String,Object> select(Map<String,Object> map);
}
