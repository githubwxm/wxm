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
     * @param smsType      短信类型 @see SmsType
     * @param epId         企业ID
     * @param params       短信参数
     * @return 发送是否成功
     */
    boolean send(String destPhoneNum, Integer smsType, Integer epId, Map<String, String> params);
}
