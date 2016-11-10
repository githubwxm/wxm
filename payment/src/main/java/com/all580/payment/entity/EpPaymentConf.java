package com.all580.payment.entity;

import java.io.Serializable;

public class EpPaymentConf implements Serializable {
    private Integer id;

    private Integer coreEpId;

    private Integer paymentType;

    private String confData;

    private Integer status;
    private String certP12; // 微信证书文件

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCoreEpId() {
        return coreEpId;
    }

    public void setCoreEpId(Integer coreEpId) {
        this.coreEpId = coreEpId;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getConfData() {
        return confData;
    }

    public void setConfData(String confData) {
        this.confData = confData == null ? null : confData.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCertP12() {
        return certP12;
    }

    public void setCertP12(String certP12) {
        this.certP12 = certP12;
    }
}