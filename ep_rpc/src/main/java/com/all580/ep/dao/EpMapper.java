package com.all580.ep.dao;



import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface EpMapper {
    int create(Object obj);
    List<Map> select (Map params);
    List<Map>  getEp(Map params);
    List<Map> all(Map params);
    List<List<?>>  validate(Map params);
    int update(Map params);
    int updateStatus(Map params);
    int updatePlatfromStatus(Map map);
    int platformEnable(Map map);
    List<Map> platformListDown(Map map);
    int platformListDownCount(Map map);

    List<Map> platformListUp(Map map);
    int platformListUpCount(Map map);
}
