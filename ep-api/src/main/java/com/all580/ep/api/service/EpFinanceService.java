package com.all580.ep.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by wxming on 2016/10/21 0021.
 */
public interface EpFinanceService {
    /**
     * 查询企业信息合并余额信息
     * @param map
     * @return   name,id,epId,coreEpId,balance,canCash,credit
     * ep.id ,ep.name,ep.linkman,ep.link_phone,ep.province,ep.city,ep.area,ep.address,ep.status,
    date_format(ep.add_time,'%Y-%m-%d') add_time,param.`name` as ep_type_name
     */
      Result<Map<String,Object>> getAccountInfoList(Map<String,Object> map);

    Result<Map<String, String>> getBalanceAccountInfo(Integer epId, Integer coreEpId);

    Result addBalance(Integer epId,Integer coreEpId,Integer balance);

    Result getBalanceSerialList(Integer epId, Integer coreEpId,
                                String balanceSatatus,String startDate,String endDate,String ref_id,
                                Integer startRecord, Integer maxRecords,Integer changType);
}
