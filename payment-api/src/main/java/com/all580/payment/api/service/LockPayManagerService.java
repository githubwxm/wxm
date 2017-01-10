package com.all580.payment.api.service;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.framework.common.Result;

import java.util.List;

/**
 * Created by wxming on 2017/1/10 0010.
 */
public interface LockPayManagerService {
    public Result<BalanceChangeRsp> changeBalances(List<BalanceChangeInfo> balanceChangeInfoList, Integer type,
                                                   final String serialNum);
}
