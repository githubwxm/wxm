package com.all580.ep.api.service;

import com.all580.ep.api.entity.CoreEpAccess;
import com.framework.common.Result;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public interface CoreEpAccessService {
    Result<Integer> create(Map map);
    Result<CoreEpAccess> select(Map params);
}
