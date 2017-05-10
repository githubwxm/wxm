package com.all580.ep.service;

import com.all580.notice.api.conf.SmsType;
import com.all580.notice.api.service.SmsService;
import com.framework.common.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author panyi on 2016/10/19.
 * @since V0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
public class SmsServiceTest {
    @Autowired
    private SmsService smsService;

    @Test
    public void sendTest(){
        String destPhoneNum = "13534291521";
        Integer smsType = 12114; // 12006
        Integer epId = 1;
        Map<String, String> params = new HashMap<>();
//        params.put("dingdanhao","13417325939");
//        params.put("money","12");

        //Map<String, String> smsParams = new HashMap<>();
        //smsParams.put("dianhuahaoma",SmsType.Ep.CHANGLV_SERVICE_PHONE);//客户
        Result send = smsService.send(destPhoneNum, smsType, epId, null);
        Assert.isTrue(send.isSuccess());
    }

    @Test
    public void sendCty() throws Exception {
        Result send = smsService.sendForCty("测试短信", "18229950359");
        Assert.isTrue(send.isSuccess());
    }
}
