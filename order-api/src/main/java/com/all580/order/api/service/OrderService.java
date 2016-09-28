package com.all580.order.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务
 * @date 2016/9/27 8:52
 */
public interface OrderService {
    /**
     * 创建订单
     * @param params
     * @return
     */
    Result<?> create(Map params);

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
     * 取消订单
     * @param params
     * @return
     */
    Result<?> cancel(Map params);

    /**
     * 退订申请
     * @param params
     * @return
     */
    Result<?> refundApply(Map params);

    /**
     * 退订审核(通过/驳回)
     * @param params
     * @return
     */
    Result<?> refundAudit(Map params);
}
