package com.all580.notice.api.service;

import com.framework.common.Result;

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
    Result send(String destPhoneNum, Integer smsType, Integer epId, Map<String, String> params);

    /**
     * 创建企业短信通道配置
     *
     * @param epId 企业ID
     * @param conf 配置信息:{url:xx,appId:xx,appPwd:xx,sign:xx}
     *             url - String - 短信通道url
     *             appId - String - 在短信通道中配置的应用ID
     *             appPwd - String - 短信通道提供的密码
     *             sign - String - 短信签名
     * @return 成功
     */
    Result createConf(Integer epId, Map<String, String> conf);
}
