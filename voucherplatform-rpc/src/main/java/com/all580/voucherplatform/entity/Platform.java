package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class Platform implements Serializable {
    /**
     *  ,所属表字段为t_platform.id
     */
    private Integer id;

    /**
     *  平台名称,所属表字段为t_platform.name
     */
    private String name;

    /**
     *  描述,所属表字段为t_platform.description
     */
    private String description;

    /**
     *  加密验证方式,所属表字段为t_platform.signType
     */
    private Integer signType;

    /**
     *  私钥,所属表字段为t_platform.privateKey
     */
    private String privateKey;

    /**
     *  公钥,所属表字段为t_platform.publicKey
     */
    private String publicKey;

    /**
     *  实现的包名,所属表字段为t_platform.implPackage
     */
    private String implPackage;

    /**
     *  配置文件,所属表字段为t_platform.conf
     */
    private String conf;

    /**
     *  启用状态,所属表字段为t_platform.status
     */
    private Boolean status;

    /**
     *  创建时间,所属表字段为t_platform.createTime
     */
    private Date createTime;

    /**
     * 序列化ID,t_platform
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_platform.id
     *
     * @return t_platform.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_platform.id
     *
     * @param id t_platform.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 平台名称 字段:t_platform.name
     *
     * @return t_platform.name, 平台名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 平台名称 字段:t_platform.name
     *
     * @param name t_platform.name, 平台名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 描述 字段:t_platform.description
     *
     * @return t_platform.description, 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 描述 字段:t_platform.description
     *
     * @param description t_platform.description, 描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取 加密验证方式 字段:t_platform.signType
     *
     * @return t_platform.signType, 加密验证方式
     */
    public Integer getSignType() {
        return signType;
    }

    /**
     * 设置 加密验证方式 字段:t_platform.signType
     *
     * @param signType t_platform.signType, 加密验证方式
     */
    public void setSignType(Integer signType) {
        this.signType = signType;
    }

    /**
     * 获取 私钥 字段:t_platform.privateKey
     *
     * @return t_platform.privateKey, 私钥
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置 私钥 字段:t_platform.privateKey
     *
     * @param privateKey t_platform.privateKey, 私钥
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey == null ? null : privateKey.trim();
    }

    /**
     * 获取 公钥 字段:t_platform.publicKey
     *
     * @return t_platform.publicKey, 公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置 公钥 字段:t_platform.publicKey
     *
     * @param publicKey t_platform.publicKey, 公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey == null ? null : publicKey.trim();
    }

    /**
     * 获取 实现的包名 字段:t_platform.implPackage
     *
     * @return t_platform.implPackage, 实现的包名
     */
    public String getImplPackage() {
        return implPackage;
    }

    /**
     * 设置 实现的包名 字段:t_platform.implPackage
     *
     * @param implPackage t_platform.implPackage, 实现的包名
     */
    public void setImplPackage(String implPackage) {
        this.implPackage = implPackage == null ? null : implPackage.trim();
    }

    /**
     * 获取 配置文件 字段:t_platform.conf
     *
     * @return t_platform.conf, 配置文件
     */
    public String getConf() {
        return conf;
    }

    /**
     * 设置 配置文件 字段:t_platform.conf
     *
     * @param conf t_platform.conf, 配置文件
     */
    public void setConf(String conf) {
        this.conf = conf == null ? null : conf.trim();
    }

    /**
     * 获取 启用状态 字段:t_platform.status
     *
     * @return t_platform.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_platform.status
     *
     * @param status t_platform.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 创建时间 字段:t_platform.createTime
     *
     * @return t_platform.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_platform.createTime
     *
     * @param createTime t_platform.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}