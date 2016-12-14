package com.all580.payment.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by wxming on 2016/12/13 0013.
 */
public interface PlatfromFundService {
    /**
     * core_ep_id
     total_fund   总资金
     add_fund     收入资金
     exit_fund  支出资金
     * @return
     */
    Result<Map<String,Object>> selectPlatfromFund(int core_ep_id);

    Result<Integer> insertPlatfromFund(int core_ep_id);
    Result<Integer> exitFund(int money,int core_ep_id);
    Result<Integer> addFund(int money,int core_ep_id);
}
