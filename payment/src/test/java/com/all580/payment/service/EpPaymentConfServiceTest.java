package com.all580.payment.service;

import com.all580.payment.api.service.EpPaymentConfService;
import com.framework.common.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author panyi on 2016/10/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/*.xml")
public class EpPaymentConfServiceTest implements ApplicationContextAware {
    @Autowired
    private EpPaymentConfService epPaymentConfService;

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void createTest() {
//        Map<String, Object> data = new HashMap<>();
//        data.put("coreEpId", 1);
//        data.put("paymentType", 7001);
//        data.put("confData", "{sss:1,bbb:2}");
//        data.put("status", 1);
//        Result result = epPaymentConfService.create(data);
//        Assert.isTrue(result.isSuccess());
        Map map = new HashMap();
        map.put("id",85);
        map.put("status",1);
        epPaymentConfService.update(map);
       // Result r = epPaymentConfService.selectByPrimaryKey(85);
        //System.out.println(r);
    }

    @Test
    public void listByEpIdTest() {
        Result<List<Map<String, String>>> result = epPaymentConfService.listByEpId(2);

        Assert.isTrue(result.isSuccess());
    }
}
