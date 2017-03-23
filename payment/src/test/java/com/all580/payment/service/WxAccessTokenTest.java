package com.all580.payment.service;

import com.all580.payment.api.service.ThirdPayService;
import com.all580.payment.thirdpay.wx.service.AccessToken;
import com.all580.payment.thirdpay.wx.service.WxPayService;
import com.framework.common.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/3/23 15:51
 */
public class WxAccessTokenTest extends BaseTest {
    @Autowired
    private ThirdPayService thirdPayService;

    @Test
    public void test() {
        Result<Map> result = thirdPayService.getWxAccessToken(1);
        Assert.isTrue(result.isSuccess());
        System.out.println(result.get());
    }
}
