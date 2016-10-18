package com.all580.ep.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public interface EpBalanceThresholdService {
    Result<Integer>  createOrUpdate(Map map);
    Result<Map>  select(Map map);

    /**
     *
     * @param mpa
     * @return  余额小于阀值
     */
    boolean warn(Map mpa);
}
