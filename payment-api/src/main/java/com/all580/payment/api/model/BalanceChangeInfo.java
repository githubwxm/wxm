package com.all580.payment.api.model;

import java.io.Serializable;

/**
 * 余额变动信息
 *
 * @author Created by panyi on 2016/10/8.
 */
public class BalanceChangeInfo implements Serializable {
    private Integer epId; // 企业ID
    private Integer coreEpId; // 平台商ID
    private Integer balance; // 余额
    private Integer canCash; // 可提现金额

    public BalanceChangeInfo() {
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

    public Integer getBalance() {
        return balance = balance == null ? 0 : balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getCanCash() {
        return canCash = canCash == null ? 0 : canCash;
    }

    public void setCanCash(Integer canCash) {
        this.canCash = canCash;
    }

}
