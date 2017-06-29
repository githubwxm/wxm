package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class Supply implements Serializable {
    /**
     *  ,所属表字段为t_supply.id
     */
    private Integer id;

    /**
     *  名字,所属表字段为t_supply.name
     */
    private String name;

    /**
     *  地址,所属表字段为t_supply.address
     */
    private String address;

    /**
     *  区域,所属表字段为t_supply.region
     */
    private String region;

    /**
     *  电话,所属表字段为t_supply.phone
     */
    private String phone;

    /**
     *  联系人,所属表字段为t_supply.agent
     */
    private String agent;

    /**
     *  描述,所属表字段为t_supply.description
     */
    private String description;

    /**
     *  创建时间,所属表字段为t_supply.createTime
     */
    private Date createTime;

    /**
     *  签名方式,所属表字段为t_supply.signType
     */
    private Integer signType;

    /**
     *  私钥,所属表字段为t_supply.privateKey
     */
    private String privateKey;

    /**
     *  公钥,所属表字段为t_supply.publicKey
     */
    private String publicKey;

    /**
     *  启用状态,所属表字段为t_supply.status
     */
    private Boolean status;

    /**
     *  供应商,所属表字段为t_supply.ticketsys_id
     */
    private Integer ticketsys_id;

    /**
     *  配置信息,所属表字段为t_supply.conf
     */
    private String conf;

    /**
     * 序列化ID,t_supply
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_supply.id
     *
     * @return t_supply.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_supply.id
     *
     * @param id t_supply.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 名字 字段:t_supply.name
     *
     * @return t_supply.name, 名字
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 名字 字段:t_supply.name
     *
     * @param name t_supply.name, 名字
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 地址 字段:t_supply.address
     *
     * @return t_supply.address, 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置 地址 字段:t_supply.address
     *
     * @param address t_supply.address, 地址
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * 获取 区域 字段:t_supply.region
     *
     * @return t_supply.region, 区域
     */
    public String getRegion() {
        return region;
    }

    /**
     * 设置 区域 字段:t_supply.region
     *
     * @param region t_supply.region, 区域
     */
    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }

    /**
     * 获取 电话 字段:t_supply.phone
     *
     * @return t_supply.phone, 电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置 电话 字段:t_supply.phone
     *
     * @param phone t_supply.phone, 电话
     */
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    /**
     * 获取 联系人 字段:t_supply.agent
     *
     * @return t_supply.agent, 联系人
     */
    public String getAgent() {
        return agent;
    }

    /**
     * 设置 联系人 字段:t_supply.agent
     *
     * @param agent t_supply.agent, 联系人
     */
    public void setAgent(String agent) {
        this.agent = agent == null ? null : agent.trim();
    }

    /**
     * 获取 描述 字段:t_supply.description
     *
     * @return t_supply.description, 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 描述 字段:t_supply.description
     *
     * @param description t_supply.description, 描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取 创建时间 字段:t_supply.createTime
     *
     * @return t_supply.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_supply.createTime
     *
     * @param createTime t_supply.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 签名方式 字段:t_supply.signType
     *
     * @return t_supply.signType, 签名方式
     */
    public Integer getSignType() {
        return signType;
    }

    /**
     * 设置 签名方式 字段:t_supply.signType
     *
     * @param signType t_supply.signType, 签名方式
     */
    public void setSignType(Integer signType) {
        this.signType = signType;
    }

    /**
     * 获取 私钥 字段:t_supply.privateKey
     *
     * @return t_supply.privateKey, 私钥
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置 私钥 字段:t_supply.privateKey
     *
     * @param privateKey t_supply.privateKey, 私钥
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey == null ? null : privateKey.trim();
    }

    /**
     * 获取 公钥 字段:t_supply.publicKey
     *
     * @return t_supply.publicKey, 公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置 公钥 字段:t_supply.publicKey
     *
     * @param publicKey t_supply.publicKey, 公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey == null ? null : publicKey.trim();
    }

    /**
     * 获取 启用状态 字段:t_supply.status
     *
     * @return t_supply.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_supply.status
     *
     * @param status t_supply.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 供应商 字段:t_supply.ticketsys_id
     *
     * @return t_supply.ticketsys_id, 供应商
     */
    public Integer getTicketsys_id() {
        return ticketsys_id;
    }

    /**
     * 设置 供应商 字段:t_supply.ticketsys_id
     *
     * @param ticketsys_id t_supply.ticketsys_id, 供应商
     */
    public void setTicketsys_id(Integer ticketsys_id) {
        this.ticketsys_id = ticketsys_id;
    }

    /**
     * 获取 配置信息 字段:t_supply.conf
     *
     * @return t_supply.conf, 配置信息
     */
    public String getConf() {
        return conf;
    }

    /**
     * 设置 配置信息 字段:t_supply.conf
     *
     * @param conf t_supply.conf, 配置信息
     */
    public void setConf(String conf) {
        this.conf = conf == null ? null : conf.trim();
    }
}