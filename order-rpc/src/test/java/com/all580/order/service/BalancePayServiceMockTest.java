package com.all580.order.service;

import com.all580.payment.api.service.BalancePayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/9/28 10:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
public class BalancePayServiceMockTest {
    @Resource
    private BalancePayService balancePayService;

    @Test
    public void createTest() {
        System.out.println(balancePayService);
        balancePayService.changeBalances(null, 1);
    }
}
