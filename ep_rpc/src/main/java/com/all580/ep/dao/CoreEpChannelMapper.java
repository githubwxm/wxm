package com.all580.ep.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface CoreEpChannelMapper {
   int create(Map params);
   int update(Map params);
   List<Map> select(Map params);
   int cancel(@Param("id") Integer id);
}
