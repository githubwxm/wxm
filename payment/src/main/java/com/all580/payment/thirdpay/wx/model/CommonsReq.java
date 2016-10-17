package com.all580.payment.thirdpay.wx.model;

/**
 * @author panyi on 2016/10/13.
 * @since V0.0.1
 */
public class CommonsReq {
    //    公众账号ID	appid	是	String(32)	wx8888888888888888	微信分配的公众账号ID
    protected String appid;
    //    商户号	mch_id	是	String(32)	1900000109	微信支付分配的商户号
    protected String mch_id;
    //    随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法
    protected String nonce_str;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }
}
