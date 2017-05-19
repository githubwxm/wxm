package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class SupplierProduct implements Serializable {
    /**
     *  ,所属表字段为t_supplierproduct.id
     */
    private Integer id;

    /**
     *  供应商的产品id,所属表字段为t_supplierproduct.code
     */
    private String code;

    /**
     *  供应商的产品名称,所属表字段为t_supplierproduct.name
     */
    private String name;

    /**
     *  供应商的产品描述,所属表字段为t_supplierproduct.description
     */
    private String description;

    /**
     *  供应商的产品扩展数据,所属表字段为t_supplierproduct.data
     */
    private String data;

    /**
     *  创建时间,所属表字段为t_supplierproduct.createTime
     */
    private Date createTime;

    /**
     *  启用状态,所属表字段为t_supplierproduct.status
     */
    private Boolean status;

    /**
     *  同步时间,所属表字段为t_supplierproduct.syncTime
     */
    private Date syncTime;

    /**
     *  供应商id,所属表字段为t_supplierproduct.supplier_id
     */
    private Integer supplier_id;

    /**
     * 序列化ID,t_supplierproduct
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_supplierproduct.id
     *
     * @return t_supplierproduct.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_supplierproduct.id
     *
     * @param id t_supplierproduct.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 供应商的产品id 字段:t_supplierproduct.code
     *
     * @return t_supplierproduct.code, 供应商的产品id
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置 供应商的产品id 字段:t_supplierproduct.code
     *
     * @param code t_supplierproduct.code, 供应商的产品id
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取 供应商的产品名称 字段:t_supplierproduct.name
     *
     * @return t_supplierproduct.name, 供应商的产品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 供应商的产品名称 字段:t_supplierproduct.name
     *
     * @param name t_supplierproduct.name, 供应商的产品名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 供应商的产品描述 字段:t_supplierproduct.description
     *
     * @return t_supplierproduct.description, 供应商的产品描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 供应商的产品描述 字段:t_supplierproduct.description
     *
     * @param description t_supplierproduct.description, 供应商的产品描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取 供应商的产品扩展数据 字段:t_supplierproduct.data
     *
     * @return t_supplierproduct.data, 供应商的产品扩展数据
     */
    public String getData() {
        return data;
    }

    /**
     * 设置 供应商的产品扩展数据 字段:t_supplierproduct.data
     *
     * @param data t_supplierproduct.data, 供应商的产品扩展数据
     */
    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }

    /**
     * 获取 创建时间 字段:t_supplierproduct.createTime
     *
     * @return t_supplierproduct.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_supplierproduct.createTime
     *
     * @param createTime t_supplierproduct.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 启用状态 字段:t_supplierproduct.status
     *
     * @return t_supplierproduct.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_supplierproduct.status
     *
     * @param status t_supplierproduct.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 同步时间 字段:t_supplierproduct.syncTime
     *
     * @return t_supplierproduct.syncTime, 同步时间
     */
    public Date getSyncTime() {
        return syncTime;
    }

    /**
     * 设置 同步时间 字段:t_supplierproduct.syncTime
     *
     * @param syncTime t_supplierproduct.syncTime, 同步时间
     */
    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    /**
     * 获取 供应商id 字段:t_supplierproduct.supplier_id
     *
     * @return t_supplierproduct.supplier_id, 供应商id
     */
    public Integer getSupplier_id() {
        return supplier_id;
    }

    /**
     * 设置 供应商id 字段:t_supplierproduct.supplier_id
     *
     * @param supplier_id t_supplierproduct.supplier_id, 供应商id
     */
    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }
}