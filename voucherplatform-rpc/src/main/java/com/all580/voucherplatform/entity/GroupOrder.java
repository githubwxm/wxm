package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class GroupOrder implements Serializable {
    /**
     *  ,所属表字段为t_group_order.id
     */
    private Integer id;

    /**
     *  ,所属表字段为t_group_order.orderCode
     */
    private String orderCode;

    /**
     *  ,所属表字段为t_group_order.platform_id
     */
    private Integer platform_id;

    /**
     *  ,所属表字段为t_group_order.platformprod_id
     */
    private Integer platformprod_id;

    /**
     *  ,所属表字段为t_group_order.supply_id
     */
    private Integer supply_id;

    /**
     *  ,所属表字段为t_group_order.ticketsys_id
     */
    private Integer ticketsys_id;

    /**
     *  ,所属表字段为t_group_order.platformOrderId
     */
    private Integer platformOrderId;

    /**
     *  ,所属表字段为t_group_order.platformProdId
     */
    private Integer platformProdId;

    /**
     *  ,所属表字段为t_group_order.supplyOrderId
     */
    private Integer supplyOrderId;

    /**
     *  ,所属表字段为t_group_order.supplyProdId
     */
    private Integer supplyProdId;

    /**
     *  ,所属表字段为t_group_order.formAreaCode
     */
    private String formAreaCode;

    /**
     *  ,所属表字段为t_group_order.formAddr
     */
    private String formAddr;

    /**
     *  ,所属表字段为t_group_order.travelName
     */
    private String travelName;

    /**
     *  ,所属表字段为t_group_order.manager
     */
    private String manager;

    /**
     *  ,所属表字段为t_group_order.groupNumber
     */
    private Integer groupNumber;

    /**
     *  ,所属表字段为t_group_order.guideName
     */
    private String guideName;

    /**
     *  ,所属表字段为t_group_order.guideIdNumber
     */
    private Integer guideIdNumber;

    /**
     *  ,所属表字段为t_group_order.guideMobile
     */
    private String guideMobile;

    /**
     *  ,所属表字段为t_group_order.payment
     */
    private String payment;

    /**
     *  ,所属表字段为t_group_order.payTime
     */
    private Date payTime;

    /**
     *  ,所属表字段为t_group_order.sendType
     */
    private String sendType;

    /**
     *  ,所属表字段为t_group_order.validTime
     */
    private Date validTime;

    /**
     *  ,所属表字段为t_group_order.invalidTime
     */
    private Date invalidTime;

    /**
     *  ,所属表字段为t_group_order.activateNum
     */
    private Integer activateNum;

    /**
     *  ,所属表字段为t_group_order.activateStatus
     */
    private Boolean activateStatus;

    /**
     *  ,所属表字段为t_group_order.price
     */
    private BigDecimal price;

    /**
     *  ,所属表字段为t_group_order.number
     */
    private Integer number;

    /**
     *  ,所属表字段为t_group_order.createTime
     */
    private Date createTime;

    /**
     *  ,所属表字段为t_group_order.voucherNumber
     */
    private String voucherNumber;

    /**
     *  ,所属表字段为t_group_order.imgUrl
     */
    private String imgUrl;

    /**
     * 序列化ID,t_group_order
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_group_order.id
     *
     * @return t_group_order.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_group_order.id
     *
     * @param id t_group_order.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取  字段:t_group_order.orderCode
     *
     * @return t_group_order.orderCode, 
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * 设置  字段:t_group_order.orderCode
     *
     * @param orderCode t_group_order.orderCode, 
     */
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    /**
     * 获取  字段:t_group_order.platform_id
     *
     * @return t_group_order.platform_id, 
     */
    public Integer getPlatform_id() {
        return platform_id;
    }

    /**
     * 设置  字段:t_group_order.platform_id
     *
     * @param platform_id t_group_order.platform_id, 
     */
    public void setPlatform_id(Integer platform_id) {
        this.platform_id = platform_id;
    }

    /**
     * 获取  字段:t_group_order.platformprod_id
     *
     * @return t_group_order.platformprod_id, 
     */
    public Integer getPlatformprod_id() {
        return platformprod_id;
    }

    /**
     * 设置  字段:t_group_order.platformprod_id
     *
     * @param platformprod_id t_group_order.platformprod_id, 
     */
    public void setPlatformprod_id(Integer platformprod_id) {
        this.platformprod_id = platformprod_id;
    }

    /**
     * 获取  字段:t_group_order.supply_id
     *
     * @return t_group_order.supply_id, 
     */
    public Integer getSupply_id() {
        return supply_id;
    }

    /**
     * 设置  字段:t_group_order.supply_id
     *
     * @param supply_id t_group_order.supply_id, 
     */
    public void setSupply_id(Integer supply_id) {
        this.supply_id = supply_id;
    }

    /**
     * 获取  字段:t_group_order.ticketsys_id
     *
     * @return t_group_order.ticketsys_id, 
     */
    public Integer getTicketsys_id() {
        return ticketsys_id;
    }

    /**
     * 设置  字段:t_group_order.ticketsys_id
     *
     * @param ticketsys_id t_group_order.ticketsys_id, 
     */
    public void setTicketsys_id(Integer ticketsys_id) {
        this.ticketsys_id = ticketsys_id;
    }

    /**
     * 获取  字段:t_group_order.platformOrderId
     *
     * @return t_group_order.platformOrderId, 
     */
    public Integer getPlatformOrderId() {
        return platformOrderId;
    }

    /**
     * 设置  字段:t_group_order.platformOrderId
     *
     * @param platformOrderId t_group_order.platformOrderId, 
     */
    public void setPlatformOrderId(Integer platformOrderId) {
        this.platformOrderId = platformOrderId;
    }

    /**
     * 获取  字段:t_group_order.platformProdId
     *
     * @return t_group_order.platformProdId, 
     */
    public Integer getPlatformProdId() {
        return platformProdId;
    }

    /**
     * 设置  字段:t_group_order.platformProdId
     *
     * @param platformProdId t_group_order.platformProdId, 
     */
    public void setPlatformProdId(Integer platformProdId) {
        this.platformProdId = platformProdId;
    }

    /**
     * 获取  字段:t_group_order.supplyOrderId
     *
     * @return t_group_order.supplyOrderId, 
     */
    public Integer getSupplyOrderId() {
        return supplyOrderId;
    }

    /**
     * 设置  字段:t_group_order.supplyOrderId
     *
     * @param supplyOrderId t_group_order.supplyOrderId, 
     */
    public void setSupplyOrderId(Integer supplyOrderId) {
        this.supplyOrderId = supplyOrderId;
    }

    /**
     * 获取  字段:t_group_order.supplyProdId
     *
     * @return t_group_order.supplyProdId, 
     */
    public Integer getSupplyProdId() {
        return supplyProdId;
    }

    /**
     * 设置  字段:t_group_order.supplyProdId
     *
     * @param supplyProdId t_group_order.supplyProdId, 
     */
    public void setSupplyProdId(Integer supplyProdId) {
        this.supplyProdId = supplyProdId;
    }

    /**
     * 获取  字段:t_group_order.formAreaCode
     *
     * @return t_group_order.formAreaCode, 
     */
    public String getFormAreaCode() {
        return formAreaCode;
    }

    /**
     * 设置  字段:t_group_order.formAreaCode
     *
     * @param formAreaCode t_group_order.formAreaCode, 
     */
    public void setFormAreaCode(String formAreaCode) {
        this.formAreaCode = formAreaCode == null ? null : formAreaCode.trim();
    }

    /**
     * 获取  字段:t_group_order.formAddr
     *
     * @return t_group_order.formAddr, 
     */
    public String getFormAddr() {
        return formAddr;
    }

    /**
     * 设置  字段:t_group_order.formAddr
     *
     * @param formAddr t_group_order.formAddr, 
     */
    public void setFormAddr(String formAddr) {
        this.formAddr = formAddr == null ? null : formAddr.trim();
    }

    /**
     * 获取  字段:t_group_order.travelName
     *
     * @return t_group_order.travelName, 
     */
    public String getTravelName() {
        return travelName;
    }

    /**
     * 设置  字段:t_group_order.travelName
     *
     * @param travelName t_group_order.travelName, 
     */
    public void setTravelName(String travelName) {
        this.travelName = travelName == null ? null : travelName.trim();
    }

    /**
     * 获取  字段:t_group_order.manager
     *
     * @return t_group_order.manager, 
     */
    public String getManager() {
        return manager;
    }

    /**
     * 设置  字段:t_group_order.manager
     *
     * @param manager t_group_order.manager, 
     */
    public void setManager(String manager) {
        this.manager = manager == null ? null : manager.trim();
    }

    /**
     * 获取  字段:t_group_order.groupNumber
     *
     * @return t_group_order.groupNumber, 
     */
    public Integer getGroupNumber() {
        return groupNumber;
    }

    /**
     * 设置  字段:t_group_order.groupNumber
     *
     * @param groupNumber t_group_order.groupNumber, 
     */
    public void setGroupNumber(Integer groupNumber) {
        this.groupNumber = groupNumber;
    }

    /**
     * 获取  字段:t_group_order.guideName
     *
     * @return t_group_order.guideName, 
     */
    public String getGuideName() {
        return guideName;
    }

    /**
     * 设置  字段:t_group_order.guideName
     *
     * @param guideName t_group_order.guideName, 
     */
    public void setGuideName(String guideName) {
        this.guideName = guideName == null ? null : guideName.trim();
    }

    /**
     * 获取  字段:t_group_order.guideIdNumber
     *
     * @return t_group_order.guideIdNumber, 
     */
    public Integer getGuideIdNumber() {
        return guideIdNumber;
    }

    /**
     * 设置  字段:t_group_order.guideIdNumber
     *
     * @param guideIdNumber t_group_order.guideIdNumber, 
     */
    public void setGuideIdNumber(Integer guideIdNumber) {
        this.guideIdNumber = guideIdNumber;
    }

    /**
     * 获取  字段:t_group_order.guideMobile
     *
     * @return t_group_order.guideMobile, 
     */
    public String getGuideMobile() {
        return guideMobile;
    }

    /**
     * 设置  字段:t_group_order.guideMobile
     *
     * @param guideMobile t_group_order.guideMobile, 
     */
    public void setGuideMobile(String guideMobile) {
        this.guideMobile = guideMobile == null ? null : guideMobile.trim();
    }

    /**
     * 获取  字段:t_group_order.payment
     *
     * @return t_group_order.payment, 
     */
    public String getPayment() {
        return payment;
    }

    /**
     * 设置  字段:t_group_order.payment
     *
     * @param payment t_group_order.payment, 
     */
    public void setPayment(String payment) {
        this.payment = payment == null ? null : payment.trim();
    }

    /**
     * 获取  字段:t_group_order.payTime
     *
     * @return t_group_order.payTime, 
     */
    public Date getPayTime() {
        return payTime;
    }

    /**
     * 设置  字段:t_group_order.payTime
     *
     * @param payTime t_group_order.payTime, 
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    /**
     * 获取  字段:t_group_order.sendType
     *
     * @return t_group_order.sendType, 
     */
    public String getSendType() {
        return sendType;
    }

    /**
     * 设置  字段:t_group_order.sendType
     *
     * @param sendType t_group_order.sendType, 
     */
    public void setSendType(String sendType) {
        this.sendType = sendType == null ? null : sendType.trim();
    }

    /**
     * 获取  字段:t_group_order.validTime
     *
     * @return t_group_order.validTime, 
     */
    public Date getValidTime() {
        return validTime;
    }

    /**
     * 设置  字段:t_group_order.validTime
     *
     * @param validTime t_group_order.validTime, 
     */
    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    /**
     * 获取  字段:t_group_order.invalidTime
     *
     * @return t_group_order.invalidTime, 
     */
    public Date getInvalidTime() {
        return invalidTime;
    }

    /**
     * 设置  字段:t_group_order.invalidTime
     *
     * @param invalidTime t_group_order.invalidTime, 
     */
    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    /**
     * 获取  字段:t_group_order.activateNum
     *
     * @return t_group_order.activateNum, 
     */
    public Integer getActivateNum() {
        return activateNum;
    }

    /**
     * 设置  字段:t_group_order.activateNum
     *
     * @param activateNum t_group_order.activateNum, 
     */
    public void setActivateNum(Integer activateNum) {
        this.activateNum = activateNum;
    }

    /**
     * 获取  字段:t_group_order.activateStatus
     *
     * @return t_group_order.activateStatus, 
     */
    public Boolean getActivateStatus() {
        return activateStatus;
    }

    /**
     * 设置  字段:t_group_order.activateStatus
     *
     * @param activateStatus t_group_order.activateStatus, 
     */
    public void setActivateStatus(Boolean activateStatus) {
        this.activateStatus = activateStatus;
    }

    /**
     * 获取  字段:t_group_order.price
     *
     * @return t_group_order.price, 
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置  字段:t_group_order.price
     *
     * @param price t_group_order.price, 
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取  字段:t_group_order.number
     *
     * @return t_group_order.number, 
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * 设置  字段:t_group_order.number
     *
     * @param number t_group_order.number, 
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * 获取  字段:t_group_order.createTime
     *
     * @return t_group_order.createTime, 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置  字段:t_group_order.createTime
     *
     * @param createTime t_group_order.createTime, 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取  字段:t_group_order.voucherNumber
     *
     * @return t_group_order.voucherNumber, 
     */
    public String getVoucherNumber() {
        return voucherNumber;
    }

    /**
     * 设置  字段:t_group_order.voucherNumber
     *
     * @param voucherNumber t_group_order.voucherNumber, 
     */
    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber == null ? null : voucherNumber.trim();
    }

    /**
     * 获取  字段:t_group_order.imgUrl
     *
     * @return t_group_order.imgUrl, 
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * 设置  字段:t_group_order.imgUrl
     *
     * @param imgUrl t_group_order.imgUrl, 
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }
}