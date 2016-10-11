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

    /**
     * 校验access_id 是否存在
     * @param access_id
     * @return   平台商id
     */
     Integer checkAccess_id(Object access_id);

}
