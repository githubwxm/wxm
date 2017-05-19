package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class DeviceApply implements Serializable {
    /**
     *  ,所属表字段为t_device_apply.id
     */
    private Integer id;

    /**
     *  设备编号,所属表字段为t_device_apply.code
     */
    private String code;

    /**
     *  申请用途说明,所属表字段为t_device_apply.description
     */
    private String description;

    /**
     *  创建时间,所属表字段为t_device_apply.createTime
     */
    private Date createTime;

    /**
     *  状态,所属表字段为t_device_apply.status
     */
    private Integer status;

    /**
     * 序列化ID,t_device_apply
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_device_apply.id
     *
     * @return t_device_apply.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_device_apply.id
     *
     * @param id t_device_apply.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 设备编号 字段:t_device_apply.code
     *
     * @return t_device_apply.code, 设备编号
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置 设备编号 字段:t_device_apply.code
     *
     * @param code t_device_apply.code, 设备编号
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取 申请用途说明 字段:t_device_apply.description
     *
     * @return t_device_apply.description, 申请用途说明
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 申请用途说明 字段:t_device_apply.description
     *
     * @param description t_device_apply.description, 申请用途说明
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取 创建时间 字段:t_device_apply.createTime
     *
     * @return t_device_apply.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_device_apply.createTime
     *
     * @param createTime t_device_apply.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 状态 字段:t_device_apply.status
     *
     * @return t_device_apply.status, 状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置 状态 字段:t_device_apply.status
     *
     * @param status t_device_apply.status, 状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}