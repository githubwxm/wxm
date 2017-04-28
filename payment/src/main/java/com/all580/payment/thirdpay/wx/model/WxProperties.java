package com.all580.payment.thirdpay.wx.model;

import lombok.*;

/**
 * 微信账号配置信息
 *
 * @author panyi on 2016/10/13.
 * @since V0.0.1
 */
@Data
public class WxProperties {
    private String api_client_cert_p12_str; // 私钥证书文件字符串
    private String app_id; // 开发应用ID
    private String mch_id; // 商户号
    private String mch_key; //  商户秘钥
    private String app_secret; //应用密钥

    private int core_ep_id; // 平台商ID
    //private
    private String web_access_key; //网站access_key
    private String web_access_id; //网站access_id
}
