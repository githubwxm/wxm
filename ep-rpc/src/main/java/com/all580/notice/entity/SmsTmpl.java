package com.all580.notice.entity;

import java.io.Serializable;
import java.util.Date;

public class SmsTmpl implements Serializable {
    private Integer id;

    private String name;

    private String content;

    private Integer status;

    private Integer epId;

    private Date createTime;

    private Integer smsType;

    private Date updateTime;

    private Boolean allowAssociateEp;

    private Integer channelType;

    private String outSmsTplId;

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

    public Integer getEpId() {
        return epId;
    }

    public void setEpId(Integer epId) {
        this.epId = epId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getSmsType() {
        return smsType;
    }

    public void setSmsType(Integer smsType) {
        this.smsType = smsType;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getAllowAssociateEp() {
        return allowAssociateEp;
    }

    public void setAllowAssociateEp(Boolean allowAssociateEp) {
        this.allowAssociateEp = allowAssociateEp;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public String getOutSmsTplId() {
        return outSmsTplId;
    }

    public void setOutSmsTplId(String outSmsTplId) {
        this.outSmsTplId = outSmsTplId == null ? null : outSmsTplId.trim();
    }
}