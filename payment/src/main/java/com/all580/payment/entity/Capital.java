package com.all580.payment.entity;

import java.io.Serializable;

public class Capital implements Serializable {
    private Integer id;

    private Integer epId;

    private Integer credit;

    private Integer balance;

    private Integer canCash;

    private Integer coreEpId;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEpId() {
        return epId;
    }

    public void setEpId(Integer epId) {
        this.epId = epId;
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

    public Integer getCanCash() {
        return canCash;
    }

    public void setCanCash(Integer canCash) {
        this.canCash = canCash;
    }

    public Integer getCoreEpId() {
        return coreEpId;
    }

    public void setCoreEpId(Integer coreEpId) {
        this.coreEpId = coreEpId;
    }
}