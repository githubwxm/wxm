package com.all580.payment.service;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.Result;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 余额支付测试
 *
 * @author Created by panyi on 2016/10/10.
 */
public class BalancePayServiceTest extends BaseTest {
    @Autowired
    private BalancePayService balancePayService;

    @Test
    public void changeBalancesForAddOrdTest() {
        List<BalanceChangeInfo> balanceChangeInfoList = new ArrayList<>();
        BalanceChangeInfo changeInfo = new BalanceChangeInfo();
        changeInfo.setEpId(10);
        changeInfo.setCoreEpId(100);
        changeInfo.setBalance(10);
        changeInfo.setCanCash(10);
        balanceChangeInfoList.add(changeInfo);
        BalanceChangeInfo changeInfo2 = new BalanceChangeInfo();
        changeInfo2.setEpId(20);
        changeInfo2.setCoreEpId(100);
        changeInfo2.setBalance(10);
        changeInfo2.setCanCash(10);
        balanceChangeInfoList.add(changeInfo2);
        Integer type = 7001;
        String serialNum = "ord-123";
        Result<BalanceChangeRsp> result = balancePayService.changeBalances(balanceChangeInfoList, type, serialNum);
        Assert.isTrue(result.isSuccess());
    }

    @Test
    public void getBalanceListTest(){
        List<Integer> epIds = Arrays.asList(new Integer[]{10,20});
        Result<List<Map<String, String>>> result = balancePayService.getBalanceList(epIds, 100);
        Assert.isTrue(result.isSuccess());
    }

}
