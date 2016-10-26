package com.all580.payment.service;

import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author panyi on 2016/10/14.
 * @since V0.0.1
 */
public class ThirdPayServiceTest extends BaseTest {
    @Autowired
    private ThirdPayServiceImpl thirdPayService;

    @Test
    public void reqPayToWxTest() {
        long ordCode = 1111111111;
        int coreEpId = 2;
        int payType = PaymentConstant.PaymentType.WX_PAY;
        Map<String, Object> params = new HashMap<>();
        params.put("totalFee", 10);
        params.put("prodId", 1010);
        params.put("prodName", "测试产品名称");
        params.put("serialNum", "110110");
        Result<String> result = thirdPayService.reqPay(ordCode, coreEpId, payType, params);
        Assert.isTrue(result.isSuccess());
        System.out.println(result.getResult());
    }

    @Test
    public void reqPayToAliTest() {
        long ordCode = 1111111111;
        int coreEpId = 1;
        int payType = PaymentConstant.PaymentType.ALI_PAY;
        Map<String, Object> params = new HashMap<>();
        params.put("totalFee", 10);
        params.put("prodId", 1010);
        params.put("prodName", "测试产品名称");
        params.put("serialNum", "110110");
        Result<String> result = thirdPayService.reqPay(ordCode, coreEpId, payType, params);
        Assert.isTrue(result.isSuccess());
        System.out.println(result.getResult());
    }

}
