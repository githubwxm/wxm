package com.all580.order.api.service;

import com.framework.common.Result;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付回调服务
 * @date 2016/10/11 19:13
 */
public interface PaymentCallbackService {

    /**
     * 支付成功回调
     * @param ordCode 订单编号
     * @param serialNum 支付流水号
     * @return
     */
    Result payCallback(long ordCode, String serialNum);

    /**
     * 退款回调
     * @param ordCode 订单编号
     * @param serialNum 退款流水号
     * @param success 是否成功
     * @return
     */
    Result refundCallback(long ordCode, String serialNum, boolean success);
}
