package com.all580.role.entity;

import java.io.Serializable;
import java.util.Date;

public class FuncIntf implements Serializable {
    private Long id;

    private Long funcId;

    private Long intfId;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFuncId() {
        return funcId;
    }

    public void setFuncId(Long funcId) {
        this.funcId = funcId;
    }

    public Long getIntfId() {
        return intfId;
    }

    public void setIntfId(Long intfId) {
        this.intfId = intfId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}