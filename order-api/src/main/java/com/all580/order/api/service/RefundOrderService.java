package com.all580.order.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订服务
 * @date 2016/10/8 13:55
 */
public interface RefundOrderService {
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
    Result<?> apply(Map params) throws Exception;

    /**
     * 取消已支付未分账的订单
     * @param params
     * @return
     */
    Result<?> cancelNoSplit(Map params);

    /**
     * 退订审核(通过/驳回)
     * @param params
     * @return
     */
    Result<?> audit(Map params) throws Exception;

    /**
     * 支付宝退款
     * @param params
     * @return
     */
    Result<?> refundAliPayMoney(Map params);
}
