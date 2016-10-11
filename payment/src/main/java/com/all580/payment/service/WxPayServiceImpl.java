package com.all580.payment.service;

import com.all580.payment.api.model.PaymentReqResult;
import com.all580.payment.api.service.BalancePayService;
import com.all580.payment.api.service.WxPayService;
import com.framework.common.Result;

import java.util.Map;

/**
 * @author Created by panyi on 2016/9/28.
 * 微信支付实现类
 */
public class WxPayServiceImpl implements WxPayService {
    @Override
    public Result<PaymentReqResult> reqPay(String payInfo) {
        return null;
    }

    @Override
    public Result<byte[]> getQrCode(String ordCode) {
        return null;
    }

    @Override
    public Result<PaymentReqResult> getPaidStatus(Long ordId) {
        return null;
    }
}
