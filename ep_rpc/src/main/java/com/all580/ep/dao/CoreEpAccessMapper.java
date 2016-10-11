package com.all580.ep.dao;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface CoreEpAccessMapper {
   int create(Map map);
   Map select(Map params);
}
