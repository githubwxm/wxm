package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class Device implements Serializable {
    /**
     *  ,所属表字段为t_device.id
     */
    private Integer id;

    /**
     *  设备名，可以用于标记设备放置在哪里,所属表字段为t_device.name
     */
    private String name;

    /**
     *  设备号,所属表字段为t_device.code
     */
    private String code;

    /**
     *  状态,所属表字段为t_device.status
     */
    private Boolean status;

    /**
     *  创建时间,所属表字段为t_device.createTime
     */
    private Date createTime;

    /**
     *  设备组id,所属表字段为t_device.device_group_id
     */
    private Integer device_group_id;

    /**
     *  ,所属表字段为t_device.signType
     */
    private Integer signType;

    /**
     *  ,所属表字段为t_device.privateKey
     */
    private String privateKey;

    /**
     *  ,所属表字段为t_device.publicKey
     */
    private String publicKey;

    /**
     * 序列化ID,t_device
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_device.id
     *
     * @return t_device.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_device.id
     *
     * @param id t_device.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 设备名，可以用于标记设备放置在哪里 字段:t_device.name
     *
     * @return t_device.name, 设备名，可以用于标记设备放置在哪里
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 设备名，可以用于标记设备放置在哪里 字段:t_device.name
     *
     * @param name t_device.name, 设备名，可以用于标记设备放置在哪里
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 设备号 字段:t_device.code
     *
     * @return t_device.code, 设备号
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置 设备号 字段:t_device.code
     *
     * @param code t_device.code, 设备号
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取 状态 字段:t_device.status
     *
     * @return t_device.status, 状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 状态 字段:t_device.status
     *
     * @param status t_device.status, 状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 创建时间 字段:t_device.createTime
     *
     * @return t_device.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_device.createTime
     *
     * @param createTime t_device.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 设备组id 字段:t_device.device_group_id
     *
     * @return t_device.device_group_id, 设备组id
     */
    public Integer getDevice_group_id() {
        return device_group_id;
    }

    /**
     * 设置 设备组id 字段:t_device.device_group_id
     *
     * @param device_group_id t_device.device_group_id, 设备组id
     */
    public void setDevice_group_id(Integer device_group_id) {
        this.device_group_id = device_group_id;
    }

    /**
     * 获取  字段:t_device.signType
     *
     * @return t_device.signType, 
     */
    public Integer getSignType() {
        return signType;
    }

    /**
     * 设置  字段:t_device.signType
     *
     * @param signType t_device.signType, 
     */
    public void setSignType(Integer signType) {
        this.signType = signType;
    }

    /**
     * 获取  字段:t_device.privateKey
     *
     * @return t_device.privateKey, 
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置  字段:t_device.privateKey
     *
     * @param privateKey t_device.privateKey, 
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey == null ? null : privateKey.trim();
    }

    /**
     * 获取  字段:t_device.publicKey
     *
     * @return t_device.publicKey, 
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置  字段:t_device.publicKey
     *
     * @param publicKey t_device.publicKey, 
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey == null ? null : publicKey.trim();
    }
}