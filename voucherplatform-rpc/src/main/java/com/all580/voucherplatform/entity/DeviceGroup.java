package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class DeviceGroup implements Serializable {
    /**
     *  ,所属表字段为t_device_group.id
     */
    private Integer id;

    /**
     *  组编码,所属表字段为t_device_group.code
     */
    private String code;

    /**
     *  组名称,所属表字段为t_device_group.name
     */
    private String name;

    /**
     *  描述,所属表字段为t_device_group.description
     */
    private String description;

    /**
     *  状态,所属表字段为t_device_group.status
     */
    private Boolean status;

    /**
     *  创建时间,所属表字段为t_device_group.createTime
     */
    private Date createTime;

    /**
     *  供应商id,所属表字段为t_device_group.supply_id
     */
    private Integer supply_id;

    /**
     *  签名类型,所属表字段为t_device_group.signType
     */
    private Integer signType;

    /**
     *  私钥,所属表字段为t_device_group.privateKey
     */
    private String privateKey;

    /**
     *  公钥,所属表字段为t_device_group.publicKey
     */
    private String publicKey;

    /**
     * 序列化ID,t_device_group
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_device_group.id
     *
     * @return t_device_group.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_device_group.id
     *
     * @param id t_device_group.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 组编码 字段:t_device_group.code
     *
     * @return t_device_group.code, 组编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置 组编码 字段:t_device_group.code
     *
     * @param code t_device_group.code, 组编码
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取 组名称 字段:t_device_group.name
     *
     * @return t_device_group.name, 组名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 组名称 字段:t_device_group.name
     *
     * @param name t_device_group.name, 组名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 描述 字段:t_device_group.description
     *
     * @return t_device_group.description, 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 描述 字段:t_device_group.description
     *
     * @param description t_device_group.description, 描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取 状态 字段:t_device_group.status
     *
     * @return t_device_group.status, 状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 状态 字段:t_device_group.status
     *
     * @param status t_device_group.status, 状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 创建时间 字段:t_device_group.createTime
     *
     * @return t_device_group.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_device_group.createTime
     *
     * @param createTime t_device_group.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 供应商id 字段:t_device_group.supply_id
     *
     * @return t_device_group.supply_id, 供应商id
     */
    public Integer getSupply_id() {
        return supply_id;
    }

    /**
     * 设置 供应商id 字段:t_device_group.supply_id
     *
     * @param supply_id t_device_group.supply_id, 供应商id
     */
    public void setSupply_id(Integer supply_id) {
        this.supply_id = supply_id;
    }

    /**
     * 获取 签名类型 字段:t_device_group.signType
     *
     * @return t_device_group.signType, 签名类型
     */
    public Integer getSignType() {
        return signType;
    }

    /**
     * 设置 签名类型 字段:t_device_group.signType
     *
     * @param signType t_device_group.signType, 签名类型
     */
    public void setSignType(Integer signType) {
        this.signType = signType;
    }

    /**
     * 获取 私钥 字段:t_device_group.privateKey
     *
     * @return t_device_group.privateKey, 私钥
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置 私钥 字段:t_device_group.privateKey
     *
     * @param privateKey t_device_group.privateKey, 私钥
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey == null ? null : privateKey.trim();
    }

    /**
     * 获取 公钥 字段:t_device_group.publicKey
     *
     * @return t_device_group.publicKey, 公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置 公钥 字段:t_device_group.publicKey
     *
     * @param publicKey t_device_group.publicKey, 公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey == null ? null : publicKey.trim();
    }
}