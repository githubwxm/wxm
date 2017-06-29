package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class Reverse implements Serializable {
    /**
     *  ,所属表字段为t_reverse.id
     */
    private Integer id;

    /**
     *  ,所属表字段为t_reverse.order_id
     */
    private Integer order_id;

    /**
     *  ,所属表字段为t_reverse.order_code
     */
    private String order_code;

    /**
     *  ,所属表字段为t_reverse.consume_id
     */
    private Integer consume_id;

    /**
     *  ,所属表字段为t_reverse.consume_code
     */
    private String consume_code;

    /**
     *  ,所属表字段为t_reverse.reverseSeqId
     */
    private String reverseSeqId;

    /**
     *  ,所属表字段为t_reverse.reverseTime
     */
    private Date reverseTime;

    /**
     *  ,所属表字段为t_reverse.createTime
     */
    private Date createTime;

    /**
     *  ,所属表字段为t_reverse.platform_id
     */
    private Integer platform_id;

    /**
     *  ,所属表字段为t_reverse.platformprod_id
     */
    private Integer platformprod_id;

    /**
     *  ,所属表字段为t_reverse.supply_id
     */
    private Integer supply_id;

    /**
     *  ,所属表字段为t_reverse.supplyprod_id
     */
    private Integer supplyprod_id;

    /**
     * 序列化ID,t_reverse
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_reverse.id
     *
     * @return t_reverse.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_reverse.id
     *
     * @param id t_reverse.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取  字段:t_reverse.order_id
     *
     * @return t_reverse.order_id, 
     */
    public Integer getOrder_id() {
        return order_id;
    }

    /**
     * 设置  字段:t_reverse.order_id
     *
     * @param order_id t_reverse.order_id, 
     */
    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    /**
     * 获取  字段:t_reverse.order_code
     *
     * @return t_reverse.order_code, 
     */
    public String getOrder_code() {
        return order_code;
    }

    /**
     * 设置  字段:t_reverse.order_code
     *
     * @param order_code t_reverse.order_code, 
     */
    public void setOrder_code(String order_code) {
        this.order_code = order_code == null ? null : order_code.trim();
    }

    /**
     * 获取  字段:t_reverse.consume_id
     *
     * @return t_reverse.consume_id, 
     */
    public Integer getConsume_id() {
        return consume_id;
    }

    /**
     * 设置  字段:t_reverse.consume_id
     *
     * @param consume_id t_reverse.consume_id, 
     */
    public void setConsume_id(Integer consume_id) {
        this.consume_id = consume_id;
    }

    /**
     * 获取  字段:t_reverse.consume_code
     *
     * @return t_reverse.consume_code, 
     */
    public String getConsume_code() {
        return consume_code;
    }

    /**
     * 设置  字段:t_reverse.consume_code
     *
     * @param consume_code t_reverse.consume_code, 
     */
    public void setConsume_code(String consume_code) {
        this.consume_code = consume_code == null ? null : consume_code.trim();
    }

    /**
     * 获取  字段:t_reverse.reverseSeqId
     *
     * @return t_reverse.reverseSeqId, 
     */
    public String getReverseSeqId() {
        return reverseSeqId;
    }

    /**
     * 设置  字段:t_reverse.reverseSeqId
     *
     * @param reverseSeqId t_reverse.reverseSeqId, 
     */
    public void setReverseSeqId(String reverseSeqId) {
        this.reverseSeqId = reverseSeqId == null ? null : reverseSeqId.trim();
    }

    /**
     * 获取  字段:t_reverse.reverseTime
     *
     * @return t_reverse.reverseTime, 
     */
    public Date getReverseTime() {
        return reverseTime;
    }

    /**
     * 设置  字段:t_reverse.reverseTime
     *
     * @param reverseTime t_reverse.reverseTime, 
     */
    public void setReverseTime(Date reverseTime) {
        this.reverseTime = reverseTime;
    }

    /**
     * 获取  字段:t_reverse.createTime
     *
     * @return t_reverse.createTime, 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置  字段:t_reverse.createTime
     *
     * @param createTime t_reverse.createTime, 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取  字段:t_reverse.platform_id
     *
     * @return t_reverse.platform_id, 
     */
    public Integer getPlatform_id() {
        return platform_id;
    }

    /**
     * 设置  字段:t_reverse.platform_id
     *
     * @param platform_id t_reverse.platform_id, 
     */
    public void setPlatform_id(Integer platform_id) {
        this.platform_id = platform_id;
    }

    /**
     * 获取  字段:t_reverse.platformprod_id
     *
     * @return t_reverse.platformprod_id, 
     */
    public Integer getPlatformprod_id() {
        return platformprod_id;
    }

    /**
     * 设置  字段:t_reverse.platformprod_id
     *
     * @param platformprod_id t_reverse.platformprod_id, 
     */
    public void setPlatformprod_id(Integer platformprod_id) {
        this.platformprod_id = platformprod_id;
    }

    /**
     * 获取  字段:t_reverse.supply_id
     *
     * @return t_reverse.supply_id, 
     */
    public Integer getSupply_id() {
        return supply_id;
    }

    /**
     * 设置  字段:t_reverse.supply_id
     *
     * @param supply_id t_reverse.supply_id, 
     */
    public void setSupply_id(Integer supply_id) {
        this.supply_id = supply_id;
    }

    /**
     * 获取  字段:t_reverse.supplyprod_id
     *
     * @return t_reverse.supplyprod_id, 
     */
    public Integer getSupplyprod_id() {
        return supplyprod_id;
    }

    /**
     * 设置  字段:t_reverse.supplyprod_id
     *
     * @param supplyprod_id t_reverse.supplyprod_id, 
     */
    public void setSupplyprod_id(Integer supplyprod_id) {
        this.supplyprod_id = supplyprod_id;
    }
}