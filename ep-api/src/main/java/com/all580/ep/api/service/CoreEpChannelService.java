package com.all580.ep.api.service;

import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public interface CoreEpChannelService {
    /**params{supplier_core_ep_id:int 必填 供应侧平台商ID,seller_core_ep_id:int 必填 销售侧平台商ID,rate }
     * @param params
     * @return
     */
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
