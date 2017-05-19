package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class QrRule implements Serializable {
    /**
     *  ,所属表字段为t_qrrule.id
     */
    private Integer id;

    /**
     *  名称,所属表字段为t_qrrule.name
     */
    private String name;

    /**
     *  长度,所属表字段为t_qrrule.len
     */
    private Integer len;

    /**
     *  前缀,所属表字段为t_qrrule.prefix
     */
    private String prefix;

    /**
     *  后缀,所属表字段为t_qrrule.postfix
     */
    private String postfix;

    /**
     *  容错率,所属表字段为t_qrrule.errorRate
     */
    private String errorRate;

    /**
     *  图片大小，建议200,所属表字段为t_qrrule.size
     */
    private Integer size;

    /**
     *  前景色,所属表字段为t_qrrule.foreColor
     */
    private String foreColor;

    /**
     *  供应商id,所属表字段为t_qrrule.supply_id
     */
    private Integer supply_id;

    /**
     *  供应商产品id,所属表字段为t_qrrule.supplyprod_id
     */
    private Integer supplyprod_id;

    /**
     *  创建时间,所属表字段为t_qrrule.createTime
     */
    private Date createTime;

    /**
     *  启用状态,所属表字段为t_qrrule.status
     */
    private Boolean status;

    /**
     *  是否为默认值,所属表字段为t_qrrule.defaultOption
     */
    private Boolean defaultOption;

    /**
     * 序列化ID,t_qrrule
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_qrrule.id
     *
     * @return t_qrrule.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_qrrule.id
     *
     * @param id t_qrrule.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 名称 字段:t_qrrule.name
     *
     * @return t_qrrule.name, 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 名称 字段:t_qrrule.name
     *
     * @param name t_qrrule.name, 名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 长度 字段:t_qrrule.len
     *
     * @return t_qrrule.len, 长度
     */
    public Integer getLen() {
        return len;
    }

    /**
     * 设置 长度 字段:t_qrrule.len
     *
     * @param len t_qrrule.len, 长度
     */
    public void setLen(Integer len) {
        this.len = len;
    }

    /**
     * 获取 前缀 字段:t_qrrule.prefix
     *
     * @return t_qrrule.prefix, 前缀
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置 前缀 字段:t_qrrule.prefix
     *
     * @param prefix t_qrrule.prefix, 前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? null : prefix.trim();
    }

    /**
     * 获取 后缀 字段:t_qrrule.postfix
     *
     * @return t_qrrule.postfix, 后缀
     */
    public String getPostfix() {
        return postfix;
    }

    /**
     * 设置 后缀 字段:t_qrrule.postfix
     *
     * @param postfix t_qrrule.postfix, 后缀
     */
    public void setPostfix(String postfix) {
        this.postfix = postfix == null ? null : postfix.trim();
    }

    /**
     * 获取 容错率 字段:t_qrrule.errorRate
     *
     * @return t_qrrule.errorRate, 容错率
     */
    public String getErrorRate() {
        return errorRate;
    }

    /**
     * 设置 容错率 字段:t_qrrule.errorRate
     *
     * @param errorRate t_qrrule.errorRate, 容错率
     */
    public void setErrorRate(String errorRate) {
        this.errorRate = errorRate == null ? null : errorRate.trim();
    }

    /**
     * 获取 图片大小，建议200 字段:t_qrrule.size
     *
     * @return t_qrrule.size, 图片大小，建议200
     */
    public Integer getSize() {
        return size;
    }

    /**
     * 设置 图片大小，建议200 字段:t_qrrule.size
     *
     * @param size t_qrrule.size, 图片大小，建议200
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * 获取 前景色 字段:t_qrrule.foreColor
     *
     * @return t_qrrule.foreColor, 前景色
     */
    public String getForeColor() {
        return foreColor;
    }

    /**
     * 设置 前景色 字段:t_qrrule.foreColor
     *
     * @param foreColor t_qrrule.foreColor, 前景色
     */
    public void setForeColor(String foreColor) {
        this.foreColor = foreColor == null ? null : foreColor.trim();
    }

    /**
     * 获取 供应商id 字段:t_qrrule.supply_id
     *
     * @return t_qrrule.supply_id, 供应商id
     */
    public Integer getSupply_id() {
        return supply_id;
    }

    /**
     * 设置 供应商id 字段:t_qrrule.supply_id
     *
     * @param supply_id t_qrrule.supply_id, 供应商id
     */
    public void setSupply_id(Integer supply_id) {
        this.supply_id = supply_id;
    }

    /**
     * 获取 供应商产品id 字段:t_qrrule.supplyprod_id
     *
     * @return t_qrrule.supplyprod_id, 供应商产品id
     */
    public Integer getSupplyprod_id() {
        return supplyprod_id;
    }

    /**
     * 设置 供应商产品id 字段:t_qrrule.supplyprod_id
     *
     * @param supplyprod_id t_qrrule.supplyprod_id, 供应商产品id
     */
    public void setSupplyprod_id(Integer supplyprod_id) {
        this.supplyprod_id = supplyprod_id;
    }

    /**
     * 获取 创建时间 字段:t_qrrule.createTime
     *
     * @return t_qrrule.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_qrrule.createTime
     *
     * @param createTime t_qrrule.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 启用状态 字段:t_qrrule.status
     *
     * @return t_qrrule.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_qrrule.status
     *
     * @param status t_qrrule.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 是否为默认值 字段:t_qrrule.defaultOption
     *
     * @return t_qrrule.defaultOption, 是否为默认值
     */
    public Boolean getDefaultOption() {
        return defaultOption;
    }

    /**
     * 设置 是否为默认值 字段:t_qrrule.defaultOption
     *
     * @param defaultOption t_qrrule.defaultOption, 是否为默认值
     */
    public void setDefaultOption(Boolean defaultOption) {
        this.defaultOption = defaultOption;
    }
}