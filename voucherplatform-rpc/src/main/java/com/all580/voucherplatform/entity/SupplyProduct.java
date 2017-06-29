package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class SupplyProduct implements Serializable {
    /**
     *  ,所属表字段为t_supplyproduct.id
     */
    private Integer id;

    /**
     *  供应商的产品id,所属表字段为t_supplyproduct.code
     */
    private String code;

    /**
     *  供应商的产品名称,所属表字段为t_supplyproduct.name
     */
    private String name;

    /**
     *  供应商的产品描述,所属表字段为t_supplyproduct.description
     */
    private String description;

    /**
     *  供应商的产品扩展数据,所属表字段为t_supplyproduct.data
     */
    private String data;

    /**
     *  创建时间,所属表字段为t_supplyproduct.createTime
     */
    private Date createTime;

    /**
     *  启用状态,所属表字段为t_supplyproduct.status
     */
    private Boolean status;

    /**
     *  同步时间,所属表字段为t_supplyproduct.syncTime
     */
    private Date syncTime;

    /**
     *  供应商id,所属表字段为t_supplyproduct.supply_id
     */
    private Integer supply_id;

    /**
     * 序列化ID,t_supplyproduct
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_supplyproduct.id
     *
     * @return t_supplyproduct.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_supplyproduct.id
     *
     * @param id t_supplyproduct.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 供应商的产品id 字段:t_supplyproduct.code
     *
     * @return t_supplyproduct.code, 供应商的产品id
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置 供应商的产品id 字段:t_supplyproduct.code
     *
     * @param code t_supplyproduct.code, 供应商的产品id
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取 供应商的产品名称 字段:t_supplyproduct.name
     *
     * @return t_supplyproduct.name, 供应商的产品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 供应商的产品名称 字段:t_supplyproduct.name
     *
     * @param name t_supplyproduct.name, 供应商的产品名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 供应商的产品描述 字段:t_supplyproduct.description
     *
     * @return t_supplyproduct.description, 供应商的产品描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 供应商的产品描述 字段:t_supplyproduct.description
     *
     * @param description t_supplyproduct.description, 供应商的产品描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取 供应商的产品扩展数据 字段:t_supplyproduct.data
     *
     * @return t_supplyproduct.data, 供应商的产品扩展数据
     */
    public String getData() {
        return data;
    }

    /**
     * 设置 供应商的产品扩展数据 字段:t_supplyproduct.data
     *
     * @param data t_supplyproduct.data, 供应商的产品扩展数据
     */
    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }

    /**
     * 获取 创建时间 字段:t_supplyproduct.createTime
     *
     * @return t_supplyproduct.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_supplyproduct.createTime
     *
     * @param createTime t_supplyproduct.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 启用状态 字段:t_supplyproduct.status
     *
     * @return t_supplyproduct.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_supplyproduct.status
     *
     * @param status t_supplyproduct.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 同步时间 字段:t_supplyproduct.syncTime
     *
     * @return t_supplyproduct.syncTime, 同步时间
     */
    public Date getSyncTime() {
        return syncTime;
    }

    /**
     * 设置 同步时间 字段:t_supplyproduct.syncTime
     *
     * @param syncTime t_supplyproduct.syncTime, 同步时间
     */
    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    /**
     * 获取 供应商id 字段:t_supplyproduct.supply_id
     *
     * @return t_supplyproduct.supply_id, 供应商id
     */
    public Integer getSupply_id() {
        return supply_id;
    }

    /**
     * 设置 供应商id 字段:t_supplyproduct.supply_id
     *
     * @param supply_id t_supplyproduct.supply_id, 供应商id
     */
    public void setSupply_id(Integer supply_id) {
        this.supply_id = supply_id;
    }
}