package com.all580.payment.service;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 余额支付测试
 * @author Created by panyi on 2016/10/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/*.xml")
public class BalancePayServiceTest {
    @Autowired
    private BalancePayService balancePayService;

    @Test
    public void changeBalancesForAddOrdTest(){
        List<BalanceChangeInfo> balanceChangeInfoList = new ArrayList<>();
        BalanceChangeInfo changeInfo = new BalanceChangeInfo();
        changeInfo.setEpId(10);
        changeInfo.setCoreEpId(100);
        changeInfo.setBalance(10);
        changeInfo.setCanCash(10);
        balanceChangeInfoList.add(changeInfo);
        BalanceChangeInfo changeInfo2 = new BalanceChangeInfo();
        changeInfo.setEpId(20);
        changeInfo.setCoreEpId(100);
        changeInfo.setBalance(10);
        changeInfo.setCanCash(10);
        Integer type = 7001;
        String serialNum = "ord-123";
        balanceChangeInfoList.add(changeInfo2);
        Result<BalanceChangeRsp> result= balancePayService.changeBalances(balanceChangeInfoList, type, serialNum);
        Assert.isTrue(result.isSuccess());
    }
}
