package com.all580.ep.dao;

import com.all580.ep.api.entity.CoreEpAccess;
import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface CoreEpAccessMapper {
   int create(Map map);
   CoreEpAccess select(Map params);
}
