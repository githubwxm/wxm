package com.all580.payment.thirdpay.ali.service;

/**
 * 支付宝账号配置信息类
 *
 * @author panyi on 2016/10/15
 * @since V0.0.1
 */
public class AliPayProperties {
    private String partner; //签约的支付宝账号对应的支付宝唯一用户号。
    private String key;
    private String sellerEmail;//卖家支付宝账号。

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

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }
}