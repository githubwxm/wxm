package com.all580.ep.entity;

import java.io.Serializable;

public class EpBank implements Serializable {
    private Integer bankEpId;

    private String bankUsername;

    private String bankNameAddress;

    private String bankNum;

    private static final long serialVersionUID = 1L;

    public Integer getBankEpId() {
        return bankEpId;
    }

    public void setBankEpId(Integer bankEpId) {
        this.bankEpId = bankEpId;
    }

    public String getBankUsername() {
        return bankUsername;
    }

    public void setBankUsername(String bankUsername) {
        this.bankUsername = bankUsername == null ? null : bankUsername.trim();
    }

    public String getBankNameAddress() {
        return bankNameAddress;
    }

    public void setBankNameAddress(String bankNameAddress) {
        this.bankNameAddress = bankNameAddress == null ? null : bankNameAddress.trim();
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum == null ? null : bankNum.trim();
    }
}