package com.all580.payment.entity;

import java.io.Serializable;
import java.util.Date;

public class CapitalSerial implements Serializable {
    private Integer id;

    private String refId;

    private Integer refType;

    private Integer capitalId;

    private Integer oldBalance;

    private Integer oldCanCash;

    private Integer newBalance;

    private Integer newCanCash;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId == null ? null : refId.trim();
    }

    public Integer getRefType() {
        return refType;
    }

    public void setRefType(Integer refType) {
        this.refType = refType;
    }

    public Integer getCapitalId() {
        return capitalId;
    }

    public void setCapitalId(Integer capitalId) {
        this.capitalId = capitalId;
    }

    public Integer getOldBalance() {
        return oldBalance;
    }

    public void setOldBalance(Integer oldBalance) {
        this.oldBalance = oldBalance;
    }

    public Integer getOldCanCash() {
        return oldCanCash;
    }

    public void setOldCanCash(Integer oldCanCash) {
        this.oldCanCash = oldCanCash;
    }

    public Integer getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(Integer newBalance) {
        this.newBalance = newBalance;
    }

    public Integer getNewCanCash() {
        return newCanCash;
    }

    public void setNewCanCash(Integer newCanCash) {
        this.newCanCash = newCanCash;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}