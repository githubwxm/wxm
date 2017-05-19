package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class Consume implements Serializable {
    /**
     *  ,所属表字段为t_consume.id
     */
    private Integer id;

    /**
     *  凭证消费编号,所属表字段为t_consume.consumeCode
     */
    private String consumeCode;

    /**
     *  订单id,所属表字段为t_consume.order_id
     */
    private Integer order_id;

    /**
     *  订单号,所属表字段为t_consume.order_code
     */
    private String order_code;

    /**
     *  供应商消费id,所属表字段为t_consume.supplierConsumeSeqId
     */
    private String supplierConsumeSeqId;

    /**
     *  消费前数量,所属表字段为t_consume.prevNumber
     */
    private Integer prevNumber;

    /**
     *  消费后数量,所属表字段为t_consume.afterNumber
     */
    private Integer afterNumber;

    /**
     *  消费时间,所属表字段为t_consume.consumeTime
     */
    private Date consumeTime;

    /**
     *  消费地点,所属表字段为t_consume.address
     */
    private String address;

    /**
     *  设备号,所属表字段为t_consume.deviceId
     */
    private String deviceId;

    /**
     *  平台商id,所属表字段为t_consume.platform_id
     */
    private Integer platform_id;

    /**
     *  平台商产品id,所属表字段为t_consume.platformprod_id
     */
    private Integer platformprod_id;

    /**
     *  供应商id,所属表字段为t_consume.supply_id
     */
    private Integer supply_id;

    /**
     *  供应商产品id,所属表字段为t_consume.supplyprod_id
     */
    private Integer supplyprod_id;

    /**
     *  是否发生过冲正,所属表字段为t_consume.reverseStatus
     */
    private Boolean reverseStatus;

    /**
     *  ,所属表字段为t_consume.createTime
     */
    private Date createTime;

    /**
     * 序列化ID,t_consume
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_consume.id
     *
     * @return t_consume.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_consume.id
     *
     * @param id t_consume.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 凭证消费编号 字段:t_consume.consumeCode
     *
     * @return t_consume.consumeCode, 凭证消费编号
     */
    public String getConsumeCode() {
        return consumeCode;
    }

    /**
     * 设置 凭证消费编号 字段:t_consume.consumeCode
     *
     * @param consumeCode t_consume.consumeCode, 凭证消费编号
     */
    public void setConsumeCode(String consumeCode) {
        this.consumeCode = consumeCode == null ? null : consumeCode.trim();
    }

    /**
     * 获取 订单id 字段:t_consume.order_id
     *
     * @return t_consume.order_id, 订单id
     */
    public Integer getOrder_id() {
        return order_id;
    }

    /**
     * 设置 订单id 字段:t_consume.order_id
     *
     * @param order_id t_consume.order_id, 订单id
     */
    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    /**
     * 获取 订单号 字段:t_consume.order_code
     *
     * @return t_consume.order_code, 订单号
     */
    public String getOrder_code() {
        return order_code;
    }

    /**
     * 设置 订单号 字段:t_consume.order_code
     *
     * @param order_code t_consume.order_code, 订单号
     */
    public void setOrder_code(String order_code) {
        this.order_code = order_code == null ? null : order_code.trim();
    }

    /**
     * 获取 供应商消费id 字段:t_consume.supplierConsumeSeqId
     *
     * @return t_consume.supplierConsumeSeqId, 供应商消费id
     */
    public String getSupplierConsumeSeqId() {
        return supplierConsumeSeqId;
    }

    /**
     * 设置 供应商消费id 字段:t_consume.supplierConsumeSeqId
     *
     * @param supplierConsumeSeqId t_consume.supplierConsumeSeqId, 供应商消费id
     */
    public void setSupplierConsumeSeqId(String supplierConsumeSeqId) {
        this.supplierConsumeSeqId = supplierConsumeSeqId == null ? null : supplierConsumeSeqId.trim();
    }

    /**
     * 获取 消费前数量 字段:t_consume.prevNumber
     *
     * @return t_consume.prevNumber, 消费前数量
     */
    public Integer getPrevNumber() {
        return prevNumber;
    }

    /**
     * 设置 消费前数量 字段:t_consume.prevNumber
     *
     * @param prevNumber t_consume.prevNumber, 消费前数量
     */
    public void setPrevNumber(Integer prevNumber) {
        this.prevNumber = prevNumber;
    }

    /**
     * 获取 消费后数量 字段:t_consume.afterNumber
     *
     * @return t_consume.afterNumber, 消费后数量
     */
    public Integer getAfterNumber() {
        return afterNumber;
    }

    /**
     * 设置 消费后数量 字段:t_consume.afterNumber
     *
     * @param afterNumber t_consume.afterNumber, 消费后数量
     */
    public void setAfterNumber(Integer afterNumber) {
        this.afterNumber = afterNumber;
    }

    /**
     * 获取 消费时间 字段:t_consume.consumeTime
     *
     * @return t_consume.consumeTime, 消费时间
     */
    public Date getConsumeTime() {
        return consumeTime;
    }

    /**
     * 设置 消费时间 字段:t_consume.consumeTime
     *
     * @param consumeTime t_consume.consumeTime, 消费时间
     */
    public void setConsumeTime(Date consumeTime) {
        this.consumeTime = consumeTime;
    }

    /**
     * 获取 消费地点 字段:t_consume.address
     *
     * @return t_consume.address, 消费地点
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置 消费地点 字段:t_consume.address
     *
     * @param address t_consume.address, 消费地点
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * 获取 设备号 字段:t_consume.deviceId
     *
     * @return t_consume.deviceId, 设备号
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置 设备号 字段:t_consume.deviceId
     *
     * @param deviceId t_consume.deviceId, 设备号
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    /**
     * 获取 平台商id 字段:t_consume.platform_id
     *
     * @return t_consume.platform_id, 平台商id
     */
    public Integer getPlatform_id() {
        return platform_id;
    }

    /**
     * 设置 平台商id 字段:t_consume.platform_id
     *
     * @param platform_id t_consume.platform_id, 平台商id
     */
    public void setPlatform_id(Integer platform_id) {
        this.platform_id = platform_id;
    }

    /**
     * 获取 平台商产品id 字段:t_consume.platformprod_id
     *
     * @return t_consume.platformprod_id, 平台商产品id
     */
    public Integer getPlatformprod_id() {
        return platformprod_id;
    }

    /**
     * 设置 平台商产品id 字段:t_consume.platformprod_id
     *
     * @param platformprod_id t_consume.platformprod_id, 平台商产品id
     */
    public void setPlatformprod_id(Integer platformprod_id) {
        this.platformprod_id = platformprod_id;
    }

    /**
     * 获取 供应商id 字段:t_consume.supply_id
     *
     * @return t_consume.supply_id, 供应商id
     */
    public Integer getSupply_id() {
        return supply_id;
    }

    /**
     * 设置 供应商id 字段:t_consume.supply_id
     *
     * @param supply_id t_consume.supply_id, 供应商id
     */
    public void setSupply_id(Integer supply_id) {
        this.supply_id = supply_id;
    }

    /**
     * 获取 供应商产品id 字段:t_consume.supplyprod_id
     *
     * @return t_consume.supplyprod_id, 供应商产品id
     */
    public Integer getSupplyprod_id() {
        return supplyprod_id;
    }

    /**
     * 设置 供应商产品id 字段:t_consume.supplyprod_id
     *
     * @param supplyprod_id t_consume.supplyprod_id, 供应商产品id
     */
    public void setSupplyprod_id(Integer supplyprod_id) {
        this.supplyprod_id = supplyprod_id;
    }

    /**
     * 获取 是否发生过冲正 字段:t_consume.reverseStatus
     *
     * @return t_consume.reverseStatus, 是否发生过冲正
     */
    public Boolean getReverseStatus() {
        return reverseStatus;
    }

    /**
     * 设置 是否发生过冲正 字段:t_consume.reverseStatus
     *
     * @param reverseStatus t_consume.reverseStatus, 是否发生过冲正
     */
    public void setReverseStatus(Boolean reverseStatus) {
        this.reverseStatus = reverseStatus;
    }

    /**
     * 获取  字段:t_consume.createTime
     *
     * @return t_consume.createTime, 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置  字段:t_consume.createTime
     *
     * @param createTime t_consume.createTime, 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}