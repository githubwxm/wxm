package com.all580.ep.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public interface CoreEpChannelService {
    /**params{supplier_core_ep_id:int 必填 供应侧平台商ID,
     * seller_core_ep_id:int 必填 销售侧平台商ID,rate:int'费率' 在这里是已经乘过100以后 }
     * @param params
     * @return
     */
    Result<Integer> create(Map<String,Object> params);
    /**
     * 只能修改汇率
     * @param params  ｛id:int 必填，rate:int  必填 费率｝
     * @return
     */
    Result<Integer> update(Map<String,Object> params);

    /**
     *
     * @param params  ｛supplier_name：非必填  供应侧平台商名字模糊查询，
     *                record_start 记录开始，record_count获取记录数｝
     * @return
     * id  int 供销关系id,supplier_core_ep_id 供应商id,supplier_name  供应商名称,supplier_phone 电话
     * seller_core_ep_id 销售商id,seller_name 名称,seller_phone 电话,rate  汇率  0.12
     *
     */
    Result<Map<String,Object>> select(Map<String,Object> params);

    Result<Integer> selectPlatfromRate(int supplier_core_ep_id,int seller_core_ep_id);
    /**
     * 取消平台供应关系
     * @param id
     * @return
     */
    Result<Integer> cancel(Integer id);

}
