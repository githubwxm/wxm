package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class Supplier implements Serializable {
    /**
     *  ,所属表字段为t_supplier.id
     */
    private Integer id;

    /**
     *  名字,所属表字段为t_supplier.name
     */
    private String name;

    /**
     *  地址,所属表字段为t_supplier.address
     */
    private String address;

    /**
     *  区域,所属表字段为t_supplier.region
     */
    private String region;

    /**
     *  电话,所属表字段为t_supplier.phone
     */
    private String phone;

    /**
     *  联系人,所属表字段为t_supplier.agent
     */
    private String agent;

    /**
     *  描述,所属表字段为t_supplier.description
     */
    private String description;

    /**
     *  创建时间,所属表字段为t_supplier.createTime
     */
    private Date createTime;

    /**
     *  签名方式,所属表字段为t_supplier.signType
     */
    private Integer signType;

    /**
     *  私钥,所属表字段为t_supplier.privateKey
     */
    private String privateKey;

    /**
     *  公钥,所属表字段为t_supplier.publicKey
     */
    private String publicKey;

    /**
     *  启用状态,所属表字段为t_supplier.status
     */
    private Boolean status;

    /**
     *  供应商,所属表字段为t_supplier.ticketsys_id
     */
    private Integer ticketsys_id;

    /**
     *  配置信息,所属表字段为t_supplier.conf
     */
    private String conf;

    /**
     * 序列化ID,t_supplier
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_supplier.id
     *
     * @return t_supplier.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_supplier.id
     *
     * @param id t_supplier.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 名字 字段:t_supplier.name
     *
     * @return t_supplier.name, 名字
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 名字 字段:t_supplier.name
     *
     * @param name t_supplier.name, 名字
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 地址 字段:t_supplier.address
     *
     * @return t_supplier.address, 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置 地址 字段:t_supplier.address
     *
     * @param address t_supplier.address, 地址
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * 获取 区域 字段:t_supplier.region
     *
     * @return t_supplier.region, 区域
     */
    public String getRegion() {
        return region;
    }

    /**
     * 设置 区域 字段:t_supplier.region
     *
     * @param region t_supplier.region, 区域
     */
    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }

    /**
     * 获取 电话 字段:t_supplier.phone
     *
     * @return t_supplier.phone, 电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置 电话 字段:t_supplier.phone
     *
     * @param phone t_supplier.phone, 电话
     */
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    /**
     * 获取 联系人 字段:t_supplier.agent
     *
     * @return t_supplier.agent, 联系人
     */
    public String getAgent() {
        return agent;
    }

    /**
     * 设置 联系人 字段:t_supplier.agent
     *
     * @param agent t_supplier.agent, 联系人
     */
    public void setAgent(String agent) {
        this.agent = agent == null ? null : agent.trim();
    }

    /**
     * 获取 描述 字段:t_supplier.description
     *
     * @return t_supplier.description, 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 描述 字段:t_supplier.description
     *
     * @param description t_supplier.description, 描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取 创建时间 字段:t_supplier.createTime
     *
     * @return t_supplier.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_supplier.createTime
     *
     * @param createTime t_supplier.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 签名方式 字段:t_supplier.signType
     *
     * @return t_supplier.signType, 签名方式
     */
    public Integer getSignType() {
        return signType;
    }

    /**
     * 设置 签名方式 字段:t_supplier.signType
     *
     * @param signType t_supplier.signType, 签名方式
     */
    public void setSignType(Integer signType) {
        this.signType = signType;
    }

    /**
     * 获取 私钥 字段:t_supplier.privateKey
     *
     * @return t_supplier.privateKey, 私钥
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置 私钥 字段:t_supplier.privateKey
     *
     * @param privateKey t_supplier.privateKey, 私钥
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey == null ? null : privateKey.trim();
    }

    /**
     * 获取 公钥 字段:t_supplier.publicKey
     *
     * @return t_supplier.publicKey, 公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置 公钥 字段:t_supplier.publicKey
     *
     * @param publicKey t_supplier.publicKey, 公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey == null ? null : publicKey.trim();
    }

    /**
     * 获取 启用状态 字段:t_supplier.status
     *
     * @return t_supplier.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_supplier.status
     *
     * @param status t_supplier.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 供应商 字段:t_supplier.ticketsys_id
     *
     * @return t_supplier.ticketsys_id, 供应商
     */
    public Integer getTicketsys_id() {
        return ticketsys_id;
    }

    /**
     * 设置 供应商 字段:t_supplier.ticketsys_id
     *
     * @param ticketsys_id t_supplier.ticketsys_id, 供应商
     */
    public void setTicketsys_id(Integer ticketsys_id) {
        this.ticketsys_id = ticketsys_id;
    }

    /**
     * 获取 配置信息 字段:t_supplier.conf
     *
     * @return t_supplier.conf, 配置信息
     */
    public String getConf() {
        return conf;
    }

    /**
     * 设置 配置信息 字段:t_supplier.conf
     *
     * @param conf t_supplier.conf, 配置信息
     */
    public void setConf(String conf) {
        this.conf = conf == null ? null : conf.trim();
    }
}