package com.all580.payment.thirdpay.ali.service;

import com.alipay.api.AlipayClient;

/**
 * 支付宝账号配置信息类
 *
 * @author panyi on 2016/10/15
 * @since V0.0.1
 */
public class AliPayProperties {
    private String partner; //签约的支付宝账号对应的支付宝唯一用户号。
    private String key; // 秘钥
    private String seller_email;//卖家支付宝账号。

    private String app_id; // 应用ID
    private String private_key; // 应用私钥
    private String alipay_public_key; // 应用支付宝公钥

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getAlipay_public_key() {
        return alipay_public_key;
    }

    public void setAlipay_public_key(String alipay_public_key) {
        this.alipay_public_key = alipay_public_key;
    }

    private AlipayClient alipayClient; // 每个帐号单例客户端

    public AlipayClient getAlipayClient() {
        return alipayClient;
    }

    public void setAlipayClient(AlipayClient alipayClient) {
        this.alipayClient = alipayClient;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }
}