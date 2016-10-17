/**
 *
 */
package com.all580.payment.thirdpay.ali.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Zale
 */
@Component
public class AlipayProperties {
    @Value("${alipay.partner}")
    private String partner; //签约的支付宝账号对应的支付宝唯一用户号。
    @Value("${alipay.key}")
    private String key;
    @Value("${alipay.selleremail}")
    private String seller_email;//卖家支付宝账号。
    @Value("${alipay.payment.callback.url}")
    private String pay_notify_url;//支付宝服务器主动通知商户网站里指定的页面http路径
    @Value("${alipay.recharge.callback.url}")
    private String recharge_notify_url;//支付宝服务器主动通知商户网站里指定的页面http路径
    @Value("${alipay.refund.callback.url}")
    private String refund_notify_url;//支付宝服务器主动通知商户网站里指定的页面http路径
    @Value("${alipay.phonePayment.callback.url}")
    private String phonepay_notify_url;//支付宝服务器主动通知商户网站里指定的页面http路径


    @Value("${base.url}")
    private String domain;


    public String getRecharge_notify_url() {
        return this.domain + recharge_notify_url;
    }

    public void setRecharge_notify_url(String recharge_notify_url) {
        this.recharge_notify_url = recharge_notify_url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRefund_notify_url() {
        return domain + refund_notify_url;
    }

    public void setRefund_notify_url(String refund_notify_url) {
        this.refund_notify_url = refund_notify_url;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getPay_notify_url() {
        return domain + pay_notify_url;
    }


    public String getPhonepay_notify_url() {
        return domain + phonepay_notify_url;
    }

    public void setPay_notify_url(String pay_notify_url) {
        this.pay_notify_url = pay_notify_url;
    }

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }

}
