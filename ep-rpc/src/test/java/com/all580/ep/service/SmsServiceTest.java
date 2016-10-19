package com.all580.ep.service;

import com.all580.notice.api.service.SmsService;
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
        String destPhoneNum = "13808466967";
        Integer smsType = 1;
        Integer epId = 2;
        Map<String, String> params = new HashMap<>();
        params.put("qiye","测试企业22");
        params.put("jinqian", "300.00");
        boolean send = smsService.send(destPhoneNum, smsType, epId, params);
        Assert.isTrue(send);
    }
}
