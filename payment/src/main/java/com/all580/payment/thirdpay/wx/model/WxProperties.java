package com.all580.payment.thirdpay.wx.model;

/**
 * 微信账号配置信息
 *
 * @author panyi on 2016/10/13.
 * @since V0.0.1
 */
public class WxProperties {
    private String api_client_cert_p12_str; // 私钥证书文件字符串
    private String app_id; // 开发应用ID
    private String mch_id; // 商户号
    private String mch_key; //  商户秘钥
    private String app_secret; //应用密钥

    private int core_ep_id; // 平台商ID
    //private

    public String getApp_secret() {
        return app_secret;
    }

    public void setApp_secret(String app_secret) {
        this.app_secret = app_secret;
    }

    public String getApi_client_cert_p12_str() {
        return api_client_cert_p12_str;
    }

    public void setApi_client_cert_p12_str(String api_client_cert_p12_str) {
        this.api_client_cert_p12_str = api_client_cert_p12_str;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getMch_key() {
        return mch_key;
    }

    public void setMch_key(String mch_key) {
        this.mch_key = mch_key;
    }

    public int getCore_ep_id() {
        return core_ep_id;
    }

    public void setCore_ep_id(int core_ep_id) {
        this.core_ep_id = core_ep_id;
    }
}
