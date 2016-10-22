package com.all580.order.api.service;

import com.framework.common.Result;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务
 * @date 2016/10/22 17:20
 */
public interface OrderService {
    /**
     * 根据第三方交易号获取收款企业ID
     * @param outTranId 第三方交易号
     * @return
     */
    Result<Integer> getPayeeEpIdByOutTransId(String outTranId);
}
