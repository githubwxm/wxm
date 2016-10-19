package com.all580.ep.api.service;

import com.framework.common.Result;

import java.util.List;
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
    Result<Integer> create(Map params);
    /**
     * 只能修改汇率
     * @param params  ｛id:int 必填，rate:int  必填 费率｝
     * @return
     */
    Result<Integer> update(Map params);

    /**
     *
     * @param params  ｛supplier_name：非必填  供应侧平台商名字模糊查询，
     *                record_start 记录开始，record_count获取记录数｝
     * @return
     */
    Result<Map> select(Map params);

    /**
     * 取消平台供应关系
     * @param id
     * @return
     */
    Result<Integer> cancel(Integer id);

}
