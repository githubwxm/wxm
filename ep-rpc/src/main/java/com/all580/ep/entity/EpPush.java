package com.all580.ep.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EpPush implements Serializable {
    /**
     *  ,所属表字段为t_ep_push.id
     */
    private Integer id;

    /**
     *  企业ID,所属表字段为t_ep_push.ep_id
     */
    private String ep_id;

    /**
     *  企业名称 ,所属表字段为t_ep_push.ep_name
     */
    private String ep_name;

    /**
     *  推送地址,所属表字段为t_ep_push.url
     */
    private String url;

    /**
     *  类型,所属表字段为t_ep_push.type
     */
    private String type;

    /**
     *  个性化配置信息,所属表字段为t_ep_push.config
     */
    private String config;

    /**
     *  ,所属表字段为t_ep_push.access_id
     */
    private String access_id;

    /**
     *  ,所属表字段为t_ep_push.access_key
     */
    private String access_key;

    /**
     *  运营平台地址,所属表字段为t_ep_push.client_url
     */
    private String client_url;

    /**
     *  ,所属表字段为t_ep_push.create_time
     */
    private Date create_time;

    /**
     * 序列化ID,t_ep_push
     */
    private static final long serialVersionUID = 1L;

}