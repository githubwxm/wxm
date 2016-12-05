package com.all580.ep.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface CoreEpAccessMapper {
   int create(Map<String,Object> map);
   List<Map<String,Object>> select(Map<String,Object> params);
   Map<String,Object> selectAccess(Map<String,Object> params);
   List<String> selectAll();
   List<String> selectAccessList(@Param("ids") List<Integer> ids);
}
