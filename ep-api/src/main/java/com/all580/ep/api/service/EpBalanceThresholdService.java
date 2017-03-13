package com.all580.ep.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public interface EpBalanceThresholdService {
    /**
     * #{ep_id},#{core_ep_id}
     * @param map｛ep_id：int 必填 销售商id,core_ep_id int 必填 供应商id平台商id,threshold  int 非必填 余额阀值｝
     * @return
     */
    Result<Integer>  createOrUpdate(Map<String,Object> map);


    Result<Map<String,Object>> selectBalance(Map<String,Object> map);

    /**
     *
     * @param map ｛ep_id：int 必填 销售商id,core_ep_id int 必填 供应商id平台商id｝
     * @return ｛id ,ep_id :销售商id,core_ep_id:供应商id平台商id,threshold:余额｝
     */
    Result<Map<String,Object>>  select(Map<String,Object> map);

    /**
     * ep_id  core_ep_id  balance
     * @param mpa ｛balance：int 必填  余额｝
     * @return  余额小于阀值
     */
    Result warn(Map<String,Object> mpa);


    Result selectThresholdList(Map<String,Object> map);
    Result CreateOnUpdateThreshold(Map<String,Object> map);
}
