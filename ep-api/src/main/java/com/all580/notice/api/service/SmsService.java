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
     * 发送短信
     * @param epId 企业ID
     * @param type 短信类型 @see SmsType
     * @param params 短信参数
     * @param phones 手机号码
     * @return
     */
    Result<?> send(int epId, int type, Map<String, String> params, String... phones);

    /**
     * 发送短信（畅天游）
     * @param phone 接收号码
     * @param content 发送内容
     * @return
     */
    Result<?> sendForCty(String content, String... phone) throws Exception;

    /**
     * 创建企业短信通道配置
     * @return 成功
     */
    Result<?> addConfig(Map params);

    Result<?> updateConfig(Map params);

    Result<?> updateTemplate(Map params);

    Result<?> addTemplate(Map params);

    Result<?> removeTemplate(Map params);

    Result<?> getConfig(int epId);

    Result<?> listTemplate(int epId, Integer type);

    Result setIsSend(boolean isSend);
}