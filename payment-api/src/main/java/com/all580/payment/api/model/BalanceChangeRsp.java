package com.all580.payment.api.model;

/**
 * 余额变更的返回结果
 * @author Created by panyi on 2016/10/10.
 */
public class BalanceChangeRsp {
    private Integer epId; // 企业ID
    private Integer coreEpId; // 平台商ID
    private Integer lessBalance; // 缺少的余额

    public BalanceChangeRsp() {
    }

    public BalanceChangeRsp(Integer epId, Integer coreEpId, Integer lessBalance) {
        this.epId = epId;
        this.coreEpId = coreEpId;
        this.lessBalance = lessBalance;
    }

    public Integer getEpId() {
        return epId;
    }

    public void setEpId(Integer epId) {
        this.epId = epId;
    }

    public Integer getCoreEpId() {
        return coreEpId;
    }

    public void setCoreEpId(Integer coreEpId) {
        this.coreEpId = coreEpId;
    }

    public Integer getLessBalance() {
        return lessBalance;
    }

    public void setLessBalance(Integer lessBalance) {
        this.lessBalance = lessBalance;
    }
}
