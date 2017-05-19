package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    /**
     *  ,所属表字段为t_order.id
     */
    private Integer id;

    /**
     *  凭证平台的订单号,所属表字段为t_order.orderCode
     */
    private String orderCode;

    /**
     *  下订单的平台id,所属表字段为t_order.platform_id
     */
    private Integer platform_id;

    /**
     *  下订单的平台的产品id,所属表字段为t_order.platformprod_id
     */
    private Integer platformprod_id;

    /**
     *  所属供应商的id,所属表字段为t_order.supplier_id
     */
    private Integer supplier_id;

    /**
     *  所对应票务系统的id,所属表字段为t_order.ticketsys_id
     */
    private Integer ticketsys_id;

    /**
     *  订单来源平台的订单号码,所属表字段为t_order.platformOrderId
     */
    private String platformOrderId;

    /**
     *  订单来源平台的产品号,所属表字段为t_order.platformProdId
     */
    private String platformProdId;

    /**
     *  供应商的订单号,所属表字段为t_order.supplierOrderId
     */
    private String supplierOrderId;

    /**
     *  供应商的产品号,所属表字段为t_order.supplierProdId
     */
    private String supplierProdId;

    /**
     *  凭证类型 "0"-不限,"1"-身份证，"2"-二维码，不大于1个字符,所属表字段为t_order.voucherType
     */
    private Integer voucherType;

    /**
     *  0-按次消费_可多刷(可多次消费)，2-按次消费_一码一刷(一次消费完), 3-包具体时长的消费 (不限消费次数)，不大于1个字符,所属表字段为t_order.consumeType
     */
    private Integer consumeType;

    /**
     *  游客姓名,所属表字段为t_order.customName
     */
    private String customName;

    /**
     *  游客手机号码,所属表字段为t_order.mobile
     */
    private String mobile;

    /**
     *  游客身份证号码,所属表字段为t_order.idNumber
     */
    private String idNumber;

    /**
     *  总数量,所属表字段为t_order.number
     */
    private Integer number;

    /**
     *  凭证号,所属表字段为t_order.voucherNumber
     */
    private String voucherNumber;

    /**
     *  凭证生效时间,所属表字段为t_order.validTime
     */
    private Date validTime;

    /**
     *  凭证失效时间,所属表字段为t_order.invalidTime
     */
    private Date invalidTime;

    /**
     *  星期几有效(格式,7位数字，从星期一开始，用1和0代表可用和不可用),所属表字段为t_order.validWeek
     */
    private String validWeek;

    /**
     *  不可用日期,所属表字段为t_order.invalidDate
     */
    private String invalidDate;

    /**
     *  是否要发送短信,所属表字段为t_order.sendType
     */
    private Integer sendType;

    /**
     *  订单创建时间,所属表字段为t_order.createTime
     */
    private Date createTime;

    /**
     *  游客流水Id,所属表字段为t_order.seqId
     */
    private String seqId;

    /**
     *  订单支付时间,所属表字段为t_order.payTime
     */
    private Date payTime;

    /**
     *  短信内容,所属表字段为t_order.sms
     */
    private String sms;

    /**
     *  打印小票内容,所属表字段为t_order.printText
     */
    private String printText;

    /**
     *  ,所属表字段为t_order.imgUrl
     */
    private String imgUrl;

    /**
     * 序列化ID,t_order
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_order.id
     *
     * @return t_order.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_order.id
     *
     * @param id t_order.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 凭证平台的订单号 字段:t_order.orderCode
     *
     * @return t_order.orderCode, 凭证平台的订单号
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * 设置 凭证平台的订单号 字段:t_order.orderCode
     *
     * @param orderCode t_order.orderCode, 凭证平台的订单号
     */
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    /**
     * 获取 下订单的平台id 字段:t_order.platform_id
     *
     * @return t_order.platform_id, 下订单的平台id
     */
    public Integer getPlatform_id() {
        return platform_id;
    }

    /**
     * 设置 下订单的平台id 字段:t_order.platform_id
     *
     * @param platform_id t_order.platform_id, 下订单的平台id
     */
    public void setPlatform_id(Integer platform_id) {
        this.platform_id = platform_id;
    }

    /**
     * 获取 下订单的平台的产品id 字段:t_order.platformprod_id
     *
     * @return t_order.platformprod_id, 下订单的平台的产品id
     */
    public Integer getPlatformprod_id() {
        return platformprod_id;
    }

    /**
     * 设置 下订单的平台的产品id 字段:t_order.platformprod_id
     *
     * @param platformprod_id t_order.platformprod_id, 下订单的平台的产品id
     */
    public void setPlatformprod_id(Integer platformprod_id) {
        this.platformprod_id = platformprod_id;
    }

    /**
     * 获取 所属供应商的id 字段:t_order.supplier_id
     *
     * @return t_order.supplier_id, 所属供应商的id
     */
    public Integer getSupplier_id() {
        return supplier_id;
    }

    /**
     * 设置 所属供应商的id 字段:t_order.supplier_id
     *
     * @param supplier_id t_order.supplier_id, 所属供应商的id
     */
    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    /**
     * 获取 所对应票务系统的id 字段:t_order.ticketsys_id
     *
     * @return t_order.ticketsys_id, 所对应票务系统的id
     */
    public Integer getTicketsys_id() {
        return ticketsys_id;
    }

    /**
     * 设置 所对应票务系统的id 字段:t_order.ticketsys_id
     *
     * @param ticketsys_id t_order.ticketsys_id, 所对应票务系统的id
     */
    public void setTicketsys_id(Integer ticketsys_id) {
        this.ticketsys_id = ticketsys_id;
    }

    /**
     * 获取 订单来源平台的订单号码 字段:t_order.platformOrderId
     *
     * @return t_order.platformOrderId, 订单来源平台的订单号码
     */
    public String getPlatformOrderId() {
        return platformOrderId;
    }

    /**
     * 设置 订单来源平台的订单号码 字段:t_order.platformOrderId
     *
     * @param platformOrderId t_order.platformOrderId, 订单来源平台的订单号码
     */
    public void setPlatformOrderId(String platformOrderId) {
        this.platformOrderId = platformOrderId == null ? null : platformOrderId.trim();
    }

    /**
     * 获取 订单来源平台的产品号 字段:t_order.platformProdId
     *
     * @return t_order.platformProdId, 订单来源平台的产品号
     */
    public String getPlatformProdId() {
        return platformProdId;
    }

    /**
     * 设置 订单来源平台的产品号 字段:t_order.platformProdId
     *
     * @param platformProdId t_order.platformProdId, 订单来源平台的产品号
     */
    public void setPlatformProdId(String platformProdId) {
        this.platformProdId = platformProdId == null ? null : platformProdId.trim();
    }

    /**
     * 获取 供应商的订单号 字段:t_order.supplierOrderId
     *
     * @return t_order.supplierOrderId, 供应商的订单号
     */
    public String getSupplierOrderId() {
        return supplierOrderId;
    }

    /**
     * 设置 供应商的订单号 字段:t_order.supplierOrderId
     *
     * @param supplierOrderId t_order.supplierOrderId, 供应商的订单号
     */
    public void setSupplierOrderId(String supplierOrderId) {
        this.supplierOrderId = supplierOrderId == null ? null : supplierOrderId.trim();
    }

    /**
     * 获取 供应商的产品号 字段:t_order.supplierProdId
     *
     * @return t_order.supplierProdId, 供应商的产品号
     */
    public String getSupplierProdId() {
        return supplierProdId;
    }

    /**
     * 设置 供应商的产品号 字段:t_order.supplierProdId
     *
     * @param supplierProdId t_order.supplierProdId, 供应商的产品号
     */
    public void setSupplierProdId(String supplierProdId) {
        this.supplierProdId = supplierProdId == null ? null : supplierProdId.trim();
    }

    /**
     * 获取 凭证类型 "0"-不限,"1"-身份证，"2"-二维码，不大于1个字符 字段:t_order.voucherType
     *
     * @return t_order.voucherType, 凭证类型 "0"-不限,"1"-身份证，"2"-二维码，不大于1个字符
     */
    public Integer getVoucherType() {
        return voucherType;
    }

    /**
     * 设置 凭证类型 "0"-不限,"1"-身份证，"2"-二维码，不大于1个字符 字段:t_order.voucherType
     *
     * @param voucherType t_order.voucherType, 凭证类型 "0"-不限,"1"-身份证，"2"-二维码，不大于1个字符
     */
    public void setVoucherType(Integer voucherType) {
        this.voucherType = voucherType;
    }

    /**
     * 获取 0-按次消费_可多刷(可多次消费)，2-按次消费_一码一刷(一次消费完), 3-包具体时长的消费 (不限消费次数)，不大于1个字符 字段:t_order.consumeType
     *
     * @return t_order.consumeType, 0-按次消费_可多刷(可多次消费)，2-按次消费_一码一刷(一次消费完), 3-包具体时长的消费 (不限消费次数)，不大于1个字符
     */
    public Integer getConsumeType() {
        return consumeType;
    }

    /**
     * 设置 0-按次消费_可多刷(可多次消费)，2-按次消费_一码一刷(一次消费完), 3-包具体时长的消费 (不限消费次数)，不大于1个字符 字段:t_order.consumeType
     *
     * @param consumeType t_order.consumeType, 0-按次消费_可多刷(可多次消费)，2-按次消费_一码一刷(一次消费完), 3-包具体时长的消费 (不限消费次数)，不大于1个字符
     */
    public void setConsumeType(Integer consumeType) {
        this.consumeType = consumeType;
    }

    /**
     * 获取 游客姓名 字段:t_order.customName
     *
     * @return t_order.customName, 游客姓名
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * 设置 游客姓名 字段:t_order.customName
     *
     * @param customName t_order.customName, 游客姓名
     */
    public void setCustomName(String customName) {
        this.customName = customName == null ? null : customName.trim();
    }

    /**
     * 获取 游客手机号码 字段:t_order.mobile
     *
     * @return t_order.mobile, 游客手机号码
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置 游客手机号码 字段:t_order.mobile
     *
     * @param mobile t_order.mobile, 游客手机号码
     */
    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    /**
     * 获取 游客身份证号码 字段:t_order.idNumber
     *
     * @return t_order.idNumber, 游客身份证号码
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * 设置 游客身份证号码 字段:t_order.idNumber
     *
     * @param idNumber t_order.idNumber, 游客身份证号码
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber == null ? null : idNumber.trim();
    }

    /**
     * 获取 总数量 字段:t_order.number
     *
     * @return t_order.number, 总数量
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * 设置 总数量 字段:t_order.number
     *
     * @param number t_order.number, 总数量
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * 获取 凭证号 字段:t_order.voucherNumber
     *
     * @return t_order.voucherNumber, 凭证号
     */
    public String getVoucherNumber() {
        return voucherNumber;
    }

    /**
     * 设置 凭证号 字段:t_order.voucherNumber
     *
     * @param voucherNumber t_order.voucherNumber, 凭证号
     */
    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber == null ? null : voucherNumber.trim();
    }

    /**
     * 获取 凭证生效时间 字段:t_order.validTime
     *
     * @return t_order.validTime, 凭证生效时间
     */
    public Date getValidTime() {
        return validTime;
    }

    /**
     * 设置 凭证生效时间 字段:t_order.validTime
     *
     * @param validTime t_order.validTime, 凭证生效时间
     */
    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    /**
     * 获取 凭证失效时间 字段:t_order.invalidTime
     *
     * @return t_order.invalidTime, 凭证失效时间
     */
    public Date getInvalidTime() {
        return invalidTime;
    }

    /**
     * 设置 凭证失效时间 字段:t_order.invalidTime
     *
     * @param invalidTime t_order.invalidTime, 凭证失效时间
     */
    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    /**
     * 获取 星期几有效(格式,7位数字，从星期一开始，用1和0代表可用和不可用) 字段:t_order.validWeek
     *
     * @return t_order.validWeek, 星期几有效(格式,7位数字，从星期一开始，用1和0代表可用和不可用)
     */
    public String getValidWeek() {
        return validWeek;
    }

    /**
     * 设置 星期几有效(格式,7位数字，从星期一开始，用1和0代表可用和不可用) 字段:t_order.validWeek
     *
     * @param validWeek t_order.validWeek, 星期几有效(格式,7位数字，从星期一开始，用1和0代表可用和不可用)
     */
    public void setValidWeek(String validWeek) {
        this.validWeek = validWeek == null ? null : validWeek.trim();
    }

    /**
     * 获取 不可用日期 字段:t_order.invalidDate
     *
     * @return t_order.invalidDate, 不可用日期
     */
    public String getInvalidDate() {
        return invalidDate;
    }

    /**
     * 设置 不可用日期 字段:t_order.invalidDate
     *
     * @param invalidDate t_order.invalidDate, 不可用日期
     */
    public void setInvalidDate(String invalidDate) {
        this.invalidDate = invalidDate == null ? null : invalidDate.trim();
    }

    /**
     * 获取 是否要发送短信 字段:t_order.sendType
     *
     * @return t_order.sendType, 是否要发送短信
     */
    public Integer getSendType() {
        return sendType;
    }

    /**
     * 设置 是否要发送短信 字段:t_order.sendType
     *
     * @param sendType t_order.sendType, 是否要发送短信
     */
    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    /**
     * 获取 订单创建时间 字段:t_order.createTime
     *
     * @return t_order.createTime, 订单创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 订单创建时间 字段:t_order.createTime
     *
     * @param createTime t_order.createTime, 订单创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 游客流水Id 字段:t_order.seqId
     *
     * @return t_order.seqId, 游客流水Id
     */
    public String getSeqId() {
        return seqId;
    }

    /**
     * 设置 游客流水Id 字段:t_order.seqId
     *
     * @param seqId t_order.seqId, 游客流水Id
     */
    public void setSeqId(String seqId) {
        this.seqId = seqId == null ? null : seqId.trim();
    }

    /**
     * 获取 订单支付时间 字段:t_order.payTime
     *
     * @return t_order.payTime, 订单支付时间
     */
    public Date getPayTime() {
        return payTime;
    }

    /**
     * 设置 订单支付时间 字段:t_order.payTime
     *
     * @param payTime t_order.payTime, 订单支付时间
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    /**
     * 获取 短信内容 字段:t_order.sms
     *
     * @return t_order.sms, 短信内容
     */
    public String getSms() {
        return sms;
    }

    /**
     * 设置 短信内容 字段:t_order.sms
     *
     * @param sms t_order.sms, 短信内容
     */
    public void setSms(String sms) {
        this.sms = sms == null ? null : sms.trim();
    }

    /**
     * 获取 打印小票内容 字段:t_order.printText
     *
     * @return t_order.printText, 打印小票内容
     */
    public String getPrintText() {
        return printText;
    }

    /**
     * 设置 打印小票内容 字段:t_order.printText
     *
     * @param printText t_order.printText, 打印小票内容
     */
    public void setPrintText(String printText) {
        this.printText = printText == null ? null : printText.trim();
    }

    /**
     * 获取  字段:t_order.imgUrl
     *
     * @return t_order.imgUrl, 
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * 设置  字段:t_order.imgUrl
     *
     * @param imgUrl t_order.imgUrl, 
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }
}