package com.all580.payment.thirdpay.wx.model;

/**
 * 微信账号配置信息
 *
 * @author panyi on 2016/10/13.
 * @since V0.0.1
 */
public class WxProperties {
    private String apiClientCertP12Str; // 私钥证书文件字符串
    private String appId; // 开发应用ID
    private String mchId; // 商户号
    private String mchKey; //  商户秘钥

    private int coreEpId; // 平台商ID
    //private

    public String getAppId() {
        return appId;
    }

    public String getMchId() {
        return mchId;
    }

    public String getMchKey() {
        return mchKey;
    }

    public String getApiClientCertP12Str() {
        return apiClientCertP12Str;
    }

    public void setApiClientCertP12Str(String apiClientCertP12Str) {
        this.apiClientCertP12Str = apiClientCertP12Str;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }

    public int getCoreEpId() {
        return coreEpId;
    }

    public void setCoreEpId(int coreEpId) {
        this.coreEpId = coreEpId;
    }
}
