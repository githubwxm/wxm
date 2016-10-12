package com.all580.payment.service;

import com.all580.payment.api.model.PaymentReqResult;
import com.all580.payment.api.service.ThirdPayService;
import com.framework.common.Result;

/**
 * @author Created by panyi on 2016/9/28.
 * 微信支付实现类
 */
public class ThirdPayServiceImpl implements ThirdPayService {
    @Override
    public Result<String> reqPay(String prodName, long ordCode, int totalFee, String serialNum, int coreEpId, int payType) {
        return null;
    }

    @Override
    public Result<String> reqRefund(long ordCode, int totalFee, int refundFee, String serialNum, int coreEpId, int payType) {
        return null;
    }

    @Override
    public Result payCallback(int payType) {
        return null;
    }

    @Override
    public Result refundCallback(int payType) {
        return null;
    }

    @Override
    public Result<byte[]> getQrCode(String ordCode) {
        return null;
    }

    @Override
    public Result getPaidStatus(Long ordCode) {
        return null;
    }
}
