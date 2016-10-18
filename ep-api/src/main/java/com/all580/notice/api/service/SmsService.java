package com.all580.notice.api.service;

import java.util.Map;

/**
 * 短信发送服务
 *
 * @author panyi on 2016/10/18.
 * @since V0.0.1
 */
public interface SmsService {
    /**
     * 发送短信
     *
     * @param destPhoneNum 目的手机号码
     * @param smsTpl       短信模板
     * @param params       模板参数
     * @param outTplId     外部模板ID
     * @return 发送是否成功
     */
    public boolean send(String destPhoneNum, String smsTpl, Map<String, String> params, String outTplId);
}
