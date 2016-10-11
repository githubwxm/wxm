package com.all580.payment.api.service;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.framework.common.Result;
import com.all580.payment.api.model.PaymentReqResult;

import java.util.List;
import java.util.Map;

/**
 * 支付服务接口
 * @author Created by panyi on 2016/9/27.
 */
public interface BalancePayService {

    /**
     * 批量修改余额 - 1、分账（支付分账，核销分账，核销冲正分账，退票分账）;2、余额调整(提现，线下充值)
     * @param balanceChangeInfoList [{epId:B平台商,coreEpId:B平台商,balance:0,canCash:15},
     *              {epId:A平台商,coreEpId:A平台商,balance:0,canCash:5},
     *              {epId:A供应商,coreEpId:A平台商,balance:0,canCash:5}]
     * @param type 余额变动类型 7001-余额支付 7002-支付分账 7003-核销分账 7004-核销冲正分账 7005-退票分账
     *                           7006-余额调整
     * @param serialNum 流水号
     * @return 成功，返回:{success:true}
     *          失败，返回：{success:false,result:{epId:xx,coreEpId:xx},error:错误信息}
     */
    Result<BalanceChangeRsp> changeBalances(List<BalanceChangeInfo> balanceChangeInfoList,Integer type,String
            serialNum);

}
