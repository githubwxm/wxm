package com.all580.payment.api.model;

import java.io.Serializable;

/**
 * 余额变动信息
 *
 * @author Created by panyi on 2016/10/8.
 */
public class BalanceChangeInfo implements Serializable {
    private Integer ep_id; // 企业ID
    private Integer core_ep_id; // 平台商ID
    private Integer balance; // 余额
    private Integer can_cash; // 可提现金额

    public BalanceChangeInfo() {
    }






    public Integer getBalance() {
        return balance = balance == null ? 0 : balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getEp_id() {
        return ep_id;
    }

    public void setEp_id(Integer ep_id) {
        this.ep_id = ep_id;
    }

    public Integer getCore_ep_id() {
        return core_ep_id;
    }

    public void setCore_ep_id(Integer core_ep_id) {
        this.core_ep_id = core_ep_id;
    }

    public Integer getCan_cash() {
           return can_cash = can_cash == null ? 0 : can_cash;
    }

    public void setCan_cash(Integer can_cash) {
        this.can_cash = can_cash;
    }

}
