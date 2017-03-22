package com.all580.payment.entity;

import java.io.Serializable;

public class Capital implements Serializable {
    private Integer id;

    private Integer ep_id;

    private Integer credit;

    private Integer balance;

    private Integer can_cash;

    private Integer core_ep_id;

    private Integer history_balance;

    public Integer getHistory_balance() {
        return history_balance;
    }

    public void setHistory_balance(Integer history_balance) {
        this.history_balance = history_balance;
    }

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEp_id() {
        return ep_id;
    }

    public void setEp_id(Integer ep_id) {
        this.ep_id = ep_id;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getCan_cash() {
        return can_cash;
    }

    public void setCan_cash(Integer can_cash) {
        this.can_cash = can_cash;
    }

    public Integer getCore_ep_id() {
        return core_ep_id;
    }

    public void setCore_ep_id(Integer core_ep_id) {
        this.core_ep_id = core_ep_id;
    }
}