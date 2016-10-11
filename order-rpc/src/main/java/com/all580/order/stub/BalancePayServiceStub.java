package com.all580.order.stub;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.Result;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/10 11:37
 */
public class BalancePayServiceStub implements BalancePayService {
    private final BalancePayService balancePayService;
    public BalancePayServiceStub(BalancePayService balancePayService) {
        this.balancePayService = balancePayService;
    }
    @Override
    public Result changeBalances(List<BalanceChangeInfo> balanceChangeInfoList, Integer type) {
        System.out.println("************");
        return null;
    }
}
