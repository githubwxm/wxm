package com.all580.order.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by xiangzw on 2017/5/24.
 */
public interface LineOrderService {

    /**
     *查询线路团队列表接口
     * @param params
     * @return
     */
    Result<?> listGroup(Map params);

    /**
     * 线路团队详情接口
     * @param number
     * @param epId
     * @return
     */
    Result<?> getLineGroupDetailByNumber(String number, String epId);

    /**
     * 线路订单游客列表
     * @param number
     * @param epId
     * @param record_start
     * @param record_count
     * @return
     */
    Result<?> listOrderVisitor(String number, String epId, int record_start,int record_count);
}
