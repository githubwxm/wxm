package com.all580.order.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 预定服务
 * @date 2016/9/27 8:52
 */
public interface BookingOrderService {
    /**
     * 创建订单
     * @param params
     * @return
     */
    Result<?> create(Map params, String type) throws Exception;

    /**
     * 供应商审核订单
     * @param params
     * @return
     */
    Result<?> audit(Map params);

    /**
     * 订单支付
     * @param params
     * @return
     */
    Result<?> payment(Map params);

    /**
     * 重新发码
     * @param params
     * @return
     */
    Result<?> resendTicket(Map params);

    /**
     * 重新发码
     * @param params
     * @return
     */
    Result<?> resendTicketForGroup(Map params);

    /**
     * 修改票据
     * @param params
     * @return
     */
    Result<?> modifyTicketForGroup(Map params);

    /**
     * 供应商酒店核销
     * @param params
     * @return
     */
    Result<?> consumeHotelBySupplier(Map params);

    /**
     * 供应商线路核销
     * @param params
     * @return
     */
    Result<?> consumeLineBySupplier(Map params);
}
