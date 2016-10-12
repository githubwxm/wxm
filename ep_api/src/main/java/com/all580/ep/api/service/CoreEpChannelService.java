package com.all580.ep.api.service;

import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public interface CoreEpChannelService {
    Result<Integer> create(Map params);
    /**
     * 只能修改汇率
     * @param params
     * @return
     */
    Result<Integer> update(Map params);
    Result<Map> select(Map params);
    Result<Integer> cancel(Integer id);

}
