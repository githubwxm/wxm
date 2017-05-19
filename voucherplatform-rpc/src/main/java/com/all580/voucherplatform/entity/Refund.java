package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class Refund implements Serializable {
    /**
     *  ,所属表字段为t_refund.id
     */
    private Integer id;

    /**
     *  凭证退票订单号,所属表字段为t_refund.refundCode
     */
    private String refundCode;

    /**
     *  订单外键id,所属表字段为t_refund.order_id
     */
    private Integer order_id;

    /**
     *  订单外键订单编号,所属表字段为t_refund.order_code
     */
    private String order_code;

    /**
     *  平台商的退票订单编号,所属表字段为t_refund.platformRefId
     */
    private String platformRefId;

    /**
     *  退票数量,所属表字段为t_refund.refNumber
     */
    private Integer refNumber;

    /**
     *  退票申请时间,所属表字段为t_refund.refTime
     */
    private Date refTime;

    /**
     *  退票理由,所属表字段为t_refund.refCause
     */
    private String refCause;

    /**
     *  退票状态,所属表字段为t_refund.refStatus
     */
    private Integer refStatus;

    /**
     *  业务操作成功时间,所属表字段为t_refund.successTime
     */
    private Date successTime;

    /**
     *  票务退票流水号,所属表字段为t_refund.supplierRefSeqId
     */
    private String supplierRefSeqId;

    /**
     *  ,所属表字段为t_refund.platform_id
     */
    private Integer platform_id;

    /**
     *  ,所属表字段为t_refund.platformproduct_id
     */
    private Integer platformproduct_id;

    /**
     *  供应商id,所属表字段为t_refund.supplier_id
     */
    private Integer supplier_id;

    /**
     *  供应商产品id,所属表字段为t_refund.supplierproduct_id
     */
    private Integer supplierproduct_id;

    /**
     *  数据创建时间,所属表字段为t_refund.createTime
     */
    private Date createTime;

    /**
     * 序列化ID,t_refund
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_refund.id
     *
     * @return t_refund.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_refund.id
     *
     * @param id t_refund.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 凭证退票订单号 字段:t_refund.refundCode
     *
     * @return t_refund.refundCode, 凭证退票订单号
     */
    public String getRefundCode() {
        return refundCode;
    }

    /**
     * 设置 凭证退票订单号 字段:t_refund.refundCode
     *
     * @param refundCode t_refund.refundCode, 凭证退票订单号
     */
    public void setRefundCode(String refundCode) {
        this.refundCode = refundCode == null ? null : refundCode.trim();
    }

    /**
     * 获取 订单外键id 字段:t_refund.order_id
     *
     * @return t_refund.order_id, 订单外键id
     */
    public Integer getOrder_id() {
        return order_id;
    }

    /**
     * 设置 订单外键id 字段:t_refund.order_id
     *
     * @param order_id t_refund.order_id, 订单外键id
     */
    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    /**
     * 获取 订单外键订单编号 字段:t_refund.order_code
     *
     * @return t_refund.order_code, 订单外键订单编号
     */
    public String getOrder_code() {
        return order_code;
    }

    /**
     * 设置 订单外键订单编号 字段:t_refund.order_code
     *
     * @param order_code t_refund.order_code, 订单外键订单编号
     */
    public void setOrder_code(String order_code) {
        this.order_code = order_code == null ? null : order_code.trim();
    }

    /**
     * 获取 平台商的退票订单编号 字段:t_refund.platformRefId
     *
     * @return t_refund.platformRefId, 平台商的退票订单编号
     */
    public String getPlatformRefId() {
        return platformRefId;
    }

    /**
     * 设置 平台商的退票订单编号 字段:t_refund.platformRefId
     *
     * @param platformRefId t_refund.platformRefId, 平台商的退票订单编号
     */
    public void setPlatformRefId(String platformRefId) {
        this.platformRefId = platformRefId == null ? null : platformRefId.trim();
    }

    /**
     * 获取 退票数量 字段:t_refund.refNumber
     *
     * @return t_refund.refNumber, 退票数量
     */
    public Integer getRefNumber() {
        return refNumber;
    }

    /**
     * 设置 退票数量 字段:t_refund.refNumber
     *
     * @param refNumber t_refund.refNumber, 退票数量
     */
    public void setRefNumber(Integer refNumber) {
        this.refNumber = refNumber;
    }

    /**
     * 获取 退票申请时间 字段:t_refund.refTime
     *
     * @return t_refund.refTime, 退票申请时间
     */
    public Date getRefTime() {
        return refTime;
    }

    /**
     * 设置 退票申请时间 字段:t_refund.refTime
     *
     * @param refTime t_refund.refTime, 退票申请时间
     */
    public void setRefTime(Date refTime) {
        this.refTime = refTime;
    }

    /**
     * 获取 退票理由 字段:t_refund.refCause
     *
     * @return t_refund.refCause, 退票理由
     */
    public String getRefCause() {
        return refCause;
    }

    /**
     * 设置 退票理由 字段:t_refund.refCause
     *
     * @param refCause t_refund.refCause, 退票理由
     */
    public void setRefCause(String refCause) {
        this.refCause = refCause == null ? null : refCause.trim();
    }

    /**
     * 获取 退票状态 字段:t_refund.refStatus
     *
     * @return t_refund.refStatus, 退票状态
     */
    public Integer getRefStatus() {
        return refStatus;
    }

    /**
     * 设置 退票状态 字段:t_refund.refStatus
     *
     * @param refStatus t_refund.refStatus, 退票状态
     */
    public void setRefStatus(Integer refStatus) {
        this.refStatus = refStatus;
    }

    /**
     * 获取 业务操作成功时间 字段:t_refund.successTime
     *
     * @return t_refund.successTime, 业务操作成功时间
     */
    public Date getSuccessTime() {
        return successTime;
    }

    /**
     * 设置 业务操作成功时间 字段:t_refund.successTime
     *
     * @param successTime t_refund.successTime, 业务操作成功时间
     */
    public void setSuccessTime(Date successTime) {
        this.successTime = successTime;
    }

    /**
     * 获取 票务退票流水号 字段:t_refund.supplierRefSeqId
     *
     * @return t_refund.supplierRefSeqId, 票务退票流水号
     */
    public String getSupplierRefSeqId() {
        return supplierRefSeqId;
    }

    /**
     * 设置 票务退票流水号 字段:t_refund.supplierRefSeqId
     *
     * @param supplierRefSeqId t_refund.supplierRefSeqId, 票务退票流水号
     */
    public void setSupplierRefSeqId(String supplierRefSeqId) {
        this.supplierRefSeqId = supplierRefSeqId == null ? null : supplierRefSeqId.trim();
    }

    /**
     * 获取  字段:t_refund.platform_id
     *
     * @return t_refund.platform_id, 
     */
    public Integer getPlatform_id() {
        return platform_id;
    }

    /**
     * 设置  字段:t_refund.platform_id
     *
     * @param platform_id t_refund.platform_id, 
     */
    public void setPlatform_id(Integer platform_id) {
        this.platform_id = platform_id;
    }

    /**
     * 获取  字段:t_refund.platformproduct_id
     *
     * @return t_refund.platformproduct_id, 
     */
    public Integer getPlatformproduct_id() {
        return platformproduct_id;
    }

    /**
     * 设置  字段:t_refund.platformproduct_id
     *
     * @param platformproduct_id t_refund.platformproduct_id, 
     */
    public void setPlatformproduct_id(Integer platformproduct_id) {
        this.platformproduct_id = platformproduct_id;
    }

    /**
     * 获取 供应商id 字段:t_refund.supplier_id
     *
     * @return t_refund.supplier_id, 供应商id
     */
    public Integer getSupplier_id() {
        return supplier_id;
    }

    /**
     * 设置 供应商id 字段:t_refund.supplier_id
     *
     * @param supplier_id t_refund.supplier_id, 供应商id
     */
    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    /**
     * 获取 供应商产品id 字段:t_refund.supplierproduct_id
     *
     * @return t_refund.supplierproduct_id, 供应商产品id
     */
    public Integer getSupplierproduct_id() {
        return supplierproduct_id;
    }

    /**
     * 设置 供应商产品id 字段:t_refund.supplierproduct_id
     *
     * @param supplierproduct_id t_refund.supplierproduct_id, 供应商产品id
     */
    public void setSupplierproduct_id(Integer supplierproduct_id) {
        this.supplierproduct_id = supplierproduct_id;
    }

    /**
     * 获取 数据创建时间 字段:t_refund.createTime
     *
     * @return t_refund.createTime, 数据创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 数据创建时间 字段:t_refund.createTime
     *
     * @param createTime t_refund.createTime, 数据创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}