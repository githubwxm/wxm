package com.all580.ep.api.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class CoreEpPaymentConf implements Serializable{
    private static final long serialVersionUID = -7563342961804255874L;
    private Integer id ;//
    private Integer  core_ep_id ;//平台商企业id',
    private Integer payment_type ;//支付接口类型\n1-余额\n2-支付宝扫码\n3-微信扫码\n4-银联NOCARD',
    private Integer conf_data ;//支付配置数据',

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

    public Integer getConf_data() {
        return conf_data;
    }

    public void setConf_data(Integer conf_data) {
        this.conf_data = conf_data;
    }
}
