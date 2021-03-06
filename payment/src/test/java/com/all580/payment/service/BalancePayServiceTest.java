package com.all580.payment.service;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.all580.payment.api.service.FundSerialService;
import com.all580.payment.api.service.PlatfromFundService;
import com.all580.payment.dao.CapitalMapper;
import com.all580.payment.entity.Capital;
import com.framework.common.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 余额支付测试
 *
 * @author Created by panyi on 2016/10/10.
 */
public class BalancePayServiceTest extends BaseTest {
    @Autowired
    private BalancePayService balancePayService;

    @Autowired
    private PlatfromFundService platfromFundService;

    @Autowired
    private FundSerialService fundSerialService;
    @Autowired
    private CapitalMapper capitalMapper;

    @Test
    public void t(){

        //print(platfromFundService.insertPlatfromFund(4).get().toString());
       // platfromFundService.exitFund(500,4);
       //platfromFundService.addFund(-600,4);
        //print(fundSerialService.selectFundSerial(1,null,null,null,null,null,null,null).get().toString());
//        Capital capital = null;
//        Map map = new HashMap();
//        map.put("id",6);
//        map.put("summary","adfasdfds");
//        fundSerialService.updateFundSerialSummary(map);
//        capital= capitalMapper.selectByEpIdAndCoreEpId(1,11);
//        System.out.println(null==capital );
//        print(balancePayService.getBalanceType().get().toString());
        print(fundSerialService.selectFundSerialRefId("1489399140469").get().toString());

    }
    private void print(String str){
        System.out.println(str);
    }

    @Test
    public void changeBalancesForAddOrdTest() {
        List<BalanceChangeInfo> balanceChangeInfoList = new ArrayList<>();
        BalanceChangeInfo changeInfo = new BalanceChangeInfo();
        changeInfo.setEp_id(10);
        changeInfo.setCore_ep_id(100);
        changeInfo.setBalance(10);
        changeInfo.setCan_cash(10);
        balanceChangeInfoList.add(changeInfo);
        BalanceChangeInfo changeInfo2 = new BalanceChangeInfo();
        changeInfo2.setEp_id(20);
        changeInfo2.setCore_ep_id(100);
        changeInfo2.setBalance(10);
        changeInfo2.setCan_cash(10);
        balanceChangeInfoList.add(changeInfo2);
        Integer type = 7001;
        String serialNum = "ord-456";
        Result<BalanceChangeRsp> result = balancePayService.changeBalances(balanceChangeInfoList, type, serialNum);
        Assert.isTrue(result.isSuccess());
    }

    @Test
    public void getBalanceListTest(){
//        try{
//            balancePayService.getBalanceSerialList(1,1,1,1);
//
//        }catch(Exception e){
//e.printStackTrace();
//        }

//        List<Integer> epIds = Arrays.asList(new Integer[]{10,20});
//        Result<List<Map<String, String>>> result = balancePayService.getBalanceList(epIds, 100);
//        Assert.isTrue(result.isSuccess());
    }

}
