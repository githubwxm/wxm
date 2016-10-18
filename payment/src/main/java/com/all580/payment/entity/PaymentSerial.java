package com.all580.payment.entity;

import java.io.Serializable;
import java.util.Date;

public class PaymentSerial implements Serializable {
    private Integer id;

    private String localSerialNo;

    private String remoteSerialNo;

    private Integer payAmount;

    private String payTime;

    private Integer refId;

    private Integer refType;

    private Integer paymentType;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocalSerialNo() {
        return localSerialNo;
    }

    public void setLocalSerialNo(String localSerialNo) {
        this.localSerialNo = localSerialNo == null ? null : localSerialNo.trim();
    }

    public String getRemoteSerialNo() {
        return remoteSerialNo;
    }

    public void setRemoteSerialNo(String remoteSerialNo) {
        this.remoteSerialNo = remoteSerialNo == null ? null : remoteSerialNo.trim();
    }

    public Integer getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Integer payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime == null ? null : payTime.trim();
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public Integer getRefType() {
        return refType;
    }

    public void setRefType(Integer refType) {
        this.refType = refType;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}