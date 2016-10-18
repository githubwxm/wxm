package com.all580.payment.thirdpay.wx.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 微信账号
 *
 * @author panyi on 2016/10/13.
 * @since V0.0.1
 */
@Component
public class WxProperties {
    @Value("${weixin.apiclient_cert.p12}")
    private String apiclient_cert_p12;
    @Value("${weixin.apiclient_cert.pem}")
    private String apiclient_cert_pem;
    @Value("${weixin.apiclient_key.pem}")
    private String apiclient_key_pem;
    @Value("${weixin.rootca.pem}")
    private String rootca_pem;
    @Value("${weixin.cerpassword}")
    private String cerpassword;
    @Value("${weixin.app_id}")
    public String APP_ID;
    @Value("${weixin.app_secret}")
    public String APP_SECRET;
    @Value("${weixin.partner}")
    public String PARTNER;
    @Value("${weixin.partner_key}")
    public String PARTNER_KEY;
    @Value("${weixin.app_key}")
    public String APP_KEY;

    public String getAPP_ID() {
        return APP_ID;
    }

    public String getAPP_SECRET() {
        return APP_SECRET;
    }

    public String getPARTNER() {
        return PARTNER;
    }

    public String getPARTNER_KEY() {
        return PARTNER_KEY;
    }

    public String getAPP_KEY() {
        return APP_KEY;
    }

    public String getApiclient_cert_p12() {
        return apiclient_cert_p12;
    }

    public String getApiclient_cert_pem() {
        return apiclient_cert_pem;
    }

    public String getApiclient_key_pem() {
        return apiclient_key_pem;
    }

    public String getRootca_pem() {
        return rootca_pem;
    }

    public String getCerpassword() {
        return cerpassword;
    }
}
