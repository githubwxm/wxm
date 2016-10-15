package com.all580.ep.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28 0028. 企业与凭证商户对应表
 */
public class EpMa implements Serializable{
    private static final long serialVersionUID = -2705926519879500609L;
    private Integer id ;
    private String ep_id ;
    private String ma_id ;//凭证外键ID：凭证.t_ma.id',
    private String ma_name ;//凭证平台名+商户名',
    private String core_ep_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEp_id() {
        return ep_id;
    }

    public void setEp_id(String ep_id) {
        this.ep_id = ep_id;
    }

    public String getMa_id() {
        return ma_id;
    }

    public void setMa_id(String ma_id) {
        this.ma_id = ma_id;
    }

    public String getMa_name() {
        return ma_name;
    }

    public void setMa_name(String ma_name) {
        this.ma_name = ma_name;
    }

    public String getCore_ep_id() {
        return core_ep_id;
    }

    public void setCore_ep_id(String core_ep_id) {
        this.core_ep_id = core_ep_id;
    }
}
