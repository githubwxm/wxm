package com.all580.notice.entity;

import java.io.Serializable;
import java.util.Date;

public class SmsTmpl implements Serializable {
    private Integer id;

    private String name;

    private String content;

    private Integer status;

    private Integer ep_id;

    private Date create_time;

    private Integer sms_type;

    private Date update_time;

    private Boolean allow_associate_ep;

    private Integer channel_type;

    private String out_sms_tpl_id;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getEp_id() {
        return ep_id;
    }

    public void setEp_id(Integer ep_id) {
        this.ep_id = ep_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getSms_type() {
        return sms_type;
    }

    public void setSms_type(Integer sms_type) {
        this.sms_type = sms_type;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Boolean getAllow_associate_ep() {
        return allow_associate_ep;
    }

    public void setAllow_associate_ep(Boolean allow_associate_ep) {
        this.allow_associate_ep = allow_associate_ep;
    }

    public Integer getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(Integer channel_type) {
        this.channel_type = channel_type;
    }

    public String getOut_sms_tpl_id() {
        return out_sms_tpl_id;
    }

    public void setOut_sms_tpl_id(String out_sms_tpl_id) {
        this.out_sms_tpl_id = out_sms_tpl_id;
    }
}