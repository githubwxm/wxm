package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class DeviceProduct implements Serializable {
    /**
     *  ,所属表字段为t_device_product.id
     */
    private Integer id;

    /**
     *  设备组id,所属表字段为t_device_product.device_group_id
     */
    private Integer device_group_id;

    /**
     *  产品id,所属表字段为t_device_product.supplierproduct_id
     */
    private Integer supplierproduct_id;

    /**
     *  创建时间,所属表字段为t_device_product.createTime
     */
    private Date createTime;

    /**
     *  启用状态,所属表字段为t_device_product.status
     */
    private Boolean status;

    /**
     * 序列化ID,t_device_product
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_device_product.id
     *
     * @return t_device_product.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_device_product.id
     *
     * @param id t_device_product.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 设备组id 字段:t_device_product.device_group_id
     *
     * @return t_device_product.device_group_id, 设备组id
     */
    public Integer getDevice_group_id() {
        return device_group_id;
    }

    /**
     * 设置 设备组id 字段:t_device_product.device_group_id
     *
     * @param device_group_id t_device_product.device_group_id, 设备组id
     */
    public void setDevice_group_id(Integer device_group_id) {
        this.device_group_id = device_group_id;
    }

    /**
     * 获取 产品id 字段:t_device_product.supplierproduct_id
     *
     * @return t_device_product.supplierproduct_id, 产品id
     */
    public Integer getSupplierproduct_id() {
        return supplierproduct_id;
    }

    /**
     * 设置 产品id 字段:t_device_product.supplierproduct_id
     *
     * @param supplierproduct_id t_device_product.supplierproduct_id, 产品id
     */
    public void setSupplierproduct_id(Integer supplierproduct_id) {
        this.supplierproduct_id = supplierproduct_id;
    }

    /**
     * 获取 创建时间 字段:t_device_product.createTime
     *
     * @return t_device_product.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_device_product.createTime
     *
     * @param createTime t_device_product.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 启用状态 字段:t_device_product.status
     *
     * @return t_device_product.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_device_product.status
     *
     * @param status t_device_product.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }
}