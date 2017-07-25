package com.all580.notice.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SmsAccountConf implements Serializable {
    /**
     *  ,所属表字段为t_sms_account_conf.id
     */
    private Integer id;

    /**
     *  企业ID,所属表字段为t_sms_account_conf.ep_id
     */
    private Integer ep_id;

    /**
     *  短信通道的URL,所属表字段为t_sms_account_conf.url
     */
    private String url;

    /**
     *  应用ID,所属表字段为t_sms_account_conf.app_id
     */
    private String app_id;

    /**
     *  秘钥,所属表字段为t_sms_account_conf.app_pwd
     */
    private String app_pwd;

    /**
     *  短信签名,所属表字段为t_sms_account_conf.ep_sign
     */
    private String ep_sign;

    /**
     *  状态,所属表字段为t_sms_account_conf.status
     */
    private Boolean status;

    /**
     * 序列化ID,t_sms_account_conf
     */
    private static final long serialVersionUID = 1L;

}