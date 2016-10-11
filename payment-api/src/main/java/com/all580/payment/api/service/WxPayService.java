package com.all580.payment.api.service;

import com.all580.payment.api.model.PaymentReqResult;
import com.framework.common.Result;

/**
 * 微信支付接口
 * @author Created by panyi on 2016/10/8.
 */
public interface WxPayService {
    /**
     * 请求支付
     * @param payInfo
     * @return 二维码图片地址
     */
    Result<PaymentReqResult> reqPay(String payInfo);

    /**
     * 获取二维码
     * @param ordCode 订单号
     * @return 二维码图片
     */
    Result<byte[]> getQrCode(String ordCode);

    /**
     * 获取支付状态
     * @param ordId 订单ID
     */
    Result<PaymentReqResult> getPaidStatus(Long ordId);
}
