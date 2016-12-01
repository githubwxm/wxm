package com.all580.payment.entity;

import java.io.Serializable;

public class EpPaymentConf implements Serializable {
    private Integer id;

    private Integer core_ep_id;

    private Integer payment_type;

    private String conf_data;

    private Integer status;
    private String cert_p12; // 微信证书文件

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCore_ep_id() {
        return core_ep_id;
    }

    public void setCore_ep_id(Integer core_ep_id) {
        this.core_ep_id = core_ep_id;
    }

    public Integer getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(Integer payment_type) {
        this.payment_type = payment_type;
    }

    public String getConf_data() {
        return conf_data;
    }

    public void setConf_data(String conf_data) {
        this.conf_data = conf_data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCert_p12() {
        return cert_p12;
    }

    public void setCert_p12(String cert_p12) {
        this.cert_p12 = cert_p12;
    }
}