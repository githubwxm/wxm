package com.all580.payment.api.service;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.List;
import java.util.Map;

/**
 * 支付服务接口
 *
 * @author Created by panyi on 2016/9/27.
 */
public interface BalancePayService {

    /**
     * 修改流水备注
     * @param id
     * @param summary
     * @return
     */
    Result<Integer> updateSummary(int id,String summary);
    /**
     * 批量修改余额 - 1、分账（支付分账，核销分账，核销冲正分账，退票分账）;2、余额调整(提现，线下充值)
     *
     * @param balanceChangeInfoList [{epId:B平台商,coreEpId:B平台商,balance:0,canCash:15},
     *                              {epId:A平台商,coreEpId:A平台商,balance:0,canCash:5},
     *                              {epId:A供应商,coreEpId:A平台商,balance:0,canCash:5}]
     * @param type                  余额变动类型 7001-余额支付 7002-支付分账 7003-核销分账 7004-核销冲正分账 7005-退票分账
     *                              7007-余额调整
     * @param serialNum             流水号
     * @return 成功，返回:{success:true}
     * 失败，返回：{success:false,result:{epId:xx,coreEpId:xx},error:错误信息}
     */
    Result<BalanceChangeRsp> changeBalances(List<BalanceChangeInfo> balanceChangeInfoList, Integer type, String
            serialNum);

    /**
     * 创建余额账户
     *
     * @param epId     企业ID
     * @param coreEpId 平台ID
     * @return 成功，返回{success:true}；失败，返回{success:false,error:错误信息}
     */
    Result createBalanceAccount(Integer epId, Integer coreEpId);

    /**
     * 设置授信额度
     *
     * @param epId     企业ID
     * @param coreEpId 平台ID
     * @param credit   授信额度
     * @return 成功，返回{success:true}；失败，返回{success:false,error:错误信息}
     */
    Result setCredit(Integer epId, Integer coreEpId, Integer credit);

    /**
     * 获取余额账户信息
     *
     * @param epId     企业ID
     * @param coreEpId 平台ID
     * @return 成功，返回{success:true,result:{id:x,epId:x,coreEpId:x,canCash:x,balance:x,
     * credit:x}}；失败，返回{success:false,error:错误信息}
     */
    Result<Map<String, String>> getBalanceAccountInfo(Integer epId, Integer coreEpId);

    /**
     * 批量获取余额账户信息
     *
     * @param epIdList 限制最大100
     * @param coreEpId
     * @return [{id,epId,coreEpId,balance,canCash,credit}]
     */
    Result<List<Map<String, String>>> getBalanceList(List<Integer> epIdList, Integer coreEpId);

    /**
     * 获取指定企业的余额流水
     *
     * @param epId        企业ID
     * @param coreEpId    平台商ID
     * @param startRecord 开始记录
     * @param maxRecords  最大记录数
     * @return [{id,refId,refType,createTime,balanceAdd,canCashAdd}]
     * -------------refId - String - 关联ID
     * -------------refType - int - 余额变动类型 @see PaymentConstant.BalanceChangeType
     * -------------createTime - String - 创建时间 yyyyMMddHHmmSS
     * -------------balanceAdd - int - 余额增加金额，单位分
     * -------------canCashAdd - int - 可提现金额变动金额，单位分
     */
    Result<PageRecord<Map<String, String>>> getBalanceSerialList(Integer epId, Integer coreEpId,
                                                                 String balanceSatatus,String startDate,String endDate,String ref_id,
                                                                 Integer startRecord, Integer maxRecords);
}
