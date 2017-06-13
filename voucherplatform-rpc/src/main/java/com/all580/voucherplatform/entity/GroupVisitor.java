package com.all580.voucherplatform.entity;

import java.io.Serializable;

public class GroupVisitor implements Serializable {
    /**
     *  ,所属表字段为t_group_visitor.id
     */
    private Integer id;

    /**
     *  订单号,所属表字段为t_group_visitor.group_order_id
     */
    private Integer group_order_id;

    /**
     *  订单号,所属表字段为t_group_visitor.group_order_code
     */
    private String group_order_code;

    /**
     *  游客流水Id,所属表字段为t_group_visitor.seqId
     */
    private String seqId;

    /**
     *  游客姓名,所属表字段为t_group_visitor.customName
     */
    private String customName;

    /**
     *  游客手机号,所属表字段为t_group_visitor.mobile
     */
    private String mobile;

    /**
     *  游客证件类型  370(身份证)   371(其他),所属表字段为t_group_visitor.idType
     */
    private String idType;

    /**
     *  证件号码,所属表字段为t_group_visitor.idNumber
     */
    private String idNumber;

    /**
     *  是否使用,所属表字段为t_group_visitor.activate
     */
    private Boolean activate;

    /**
     * 序列化ID,t_group_visitor
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_group_visitor.id
     *
     * @return t_group_visitor.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_group_visitor.id
     *
     * @param id t_group_visitor.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 订单号 字段:t_group_visitor.group_order_id
     *
     * @return t_group_visitor.group_order_id, 订单号
     */
    public Integer getGroup_order_id() {
        return group_order_id;
    }

    /**
     * 设置 订单号 字段:t_group_visitor.group_order_id
     *
     * @param group_order_id t_group_visitor.group_order_id, 订单号
     */
    public void setGroup_order_id(Integer group_order_id) {
        this.group_order_id = group_order_id;
    }

    /**
     * 获取 订单号 字段:t_group_visitor.group_order_code
     *
     * @return t_group_visitor.group_order_code, 订单号
     */
    public String getGroup_order_code() {
        return group_order_code;
    }

    /**
     * 设置 订单号 字段:t_group_visitor.group_order_code
     *
     * @param group_order_code t_group_visitor.group_order_code, 订单号
     */
    public void setGroup_order_code(String group_order_code) {
        this.group_order_code = group_order_code == null ? null : group_order_code.trim();
    }

    /**
     * 获取 游客流水Id 字段:t_group_visitor.seqId
     *
     * @return t_group_visitor.seqId, 游客流水Id
     */
    public String getSeqId() {
        return seqId;
    }

    /**
     * 设置 游客流水Id 字段:t_group_visitor.seqId
     *
     * @param seqId t_group_visitor.seqId, 游客流水Id
     */
    public void setSeqId(String seqId) {
        this.seqId = seqId == null ? null : seqId.trim();
    }

    /**
     * 获取 游客姓名 字段:t_group_visitor.customName
     *
     * @return t_group_visitor.customName, 游客姓名
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * 设置 游客姓名 字段:t_group_visitor.customName
     *
     * @param customName t_group_visitor.customName, 游客姓名
     */
    public void setCustomName(String customName) {
        this.customName = customName == null ? null : customName.trim();
    }

    /**
     * 获取 游客手机号 字段:t_group_visitor.mobile
     *
     * @return t_group_visitor.mobile, 游客手机号
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置 游客手机号 字段:t_group_visitor.mobile
     *
     * @param mobile t_group_visitor.mobile, 游客手机号
     */
    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    /**
     * 获取 游客证件类型  370(身份证)   371(其他) 字段:t_group_visitor.idType
     *
     * @return t_group_visitor.idType, 游客证件类型  370(身份证)   371(其他)
     */
    public String getIdType() {
        return idType;
    }

    /**
     * 设置 游客证件类型  370(身份证)   371(其他) 字段:t_group_visitor.idType
     *
     * @param idType t_group_visitor.idType, 游客证件类型  370(身份证)   371(其他)
     */
    public void setIdType(String idType) {
        this.idType = idType == null ? null : idType.trim();
    }

    /**
     * 获取 证件号码 字段:t_group_visitor.idNumber
     *
     * @return t_group_visitor.idNumber, 证件号码
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * 设置 证件号码 字段:t_group_visitor.idNumber
     *
     * @param idNumber t_group_visitor.idNumber, 证件号码
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber == null ? null : idNumber.trim();
    }

    /**
     * 获取 是否使用 字段:t_group_visitor.activate
     *
     * @return t_group_visitor.activate, 是否使用
     */
    public Boolean getActivate() {
        return activate;
    }

    /**
     * 设置 是否使用 字段:t_group_visitor.activate
     *
     * @param activate t_group_visitor.activate, 是否使用
     */
    public void setActivate(Boolean activate) {
        this.activate = activate;
    }
}