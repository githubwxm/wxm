package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class Template implements Serializable {
    /**
     *  ,所属表字段为t_template.id
     */
    private Integer id;

    /**
     *  模板名字,所属表字段为t_template.name
     */
    private String name;

    /**
     *  短信内容,所属表字段为t_template.sms
     */
    private String sms;

    /**
     *  打印内容,所属表字段为t_template.printText
     */
    private String printText;

    /**
     *  供应商id,所属表字段为t_template.supply_id
     */
    private Integer supply_id;

    /**
     *  供应商产品id,所属表字段为t_template.supplyprod_id
     */
    private Integer supplyprod_id;

    /**
     *  ,所属表字段为t_template.status
     */
    private Integer status;

    /**
     *  ,所属表字段为t_template.createTime
     */
    private Date createTime;

    /**
     *  ,所属表字段为t_template.defaultOption
     */
    private Boolean defaultOption;

    /**
     * 序列化ID,t_template
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_template.id
     *
     * @return t_template.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_template.id
     *
     * @param id t_template.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 模板名字 字段:t_template.name
     *
     * @return t_template.name, 模板名字
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 模板名字 字段:t_template.name
     *
     * @param name t_template.name, 模板名字
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 短信内容 字段:t_template.sms
     *
     * @return t_template.sms, 短信内容
     */
    public String getSms() {
        return sms;
    }

    /**
     * 设置 短信内容 字段:t_template.sms
     *
     * @param sms t_template.sms, 短信内容
     */
    public void setSms(String sms) {
        this.sms = sms == null ? null : sms.trim();
    }

    /**
     * 获取 打印内容 字段:t_template.printText
     *
     * @return t_template.printText, 打印内容
     */
    public String getPrintText() {
        return printText;
    }

    /**
     * 设置 打印内容 字段:t_template.printText
     *
     * @param printText t_template.printText, 打印内容
     */
    public void setPrintText(String printText) {
        this.printText = printText == null ? null : printText.trim();
    }

    /**
     * 获取 供应商id 字段:t_template.supply_id
     *
     * @return t_template.supply_id, 供应商id
     */
    public Integer getSupply_id() {
        return supply_id;
    }

    /**
     * 设置 供应商id 字段:t_template.supply_id
     *
     * @param supply_id t_template.supply_id, 供应商id
     */
    public void setSupply_id(Integer supply_id) {
        this.supply_id = supply_id;
    }

    /**
     * 获取 供应商产品id 字段:t_template.supplyprod_id
     *
     * @return t_template.supplyprod_id, 供应商产品id
     */
    public Integer getSupplyprod_id() {
        return supplyprod_id;
    }

    /**
     * 设置 供应商产品id 字段:t_template.supplyprod_id
     *
     * @param supplyprod_id t_template.supplyprod_id, 供应商产品id
     */
    public void setSupplyprod_id(Integer supplyprod_id) {
        this.supplyprod_id = supplyprod_id;
    }

    /**
     * 获取  字段:t_template.status
     *
     * @return t_template.status, 
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置  字段:t_template.status
     *
     * @param status t_template.status, 
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取  字段:t_template.createTime
     *
     * @return t_template.createTime, 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置  字段:t_template.createTime
     *
     * @param createTime t_template.createTime, 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取  字段:t_template.defaultOption
     *
     * @return t_template.defaultOption, 
     */
    public Boolean getDefaultOption() {
        return defaultOption;
    }

    /**
     * 设置  字段:t_template.defaultOption
     *
     * @param defaultOption t_template.defaultOption, 
     */
    public void setDefaultOption(Boolean defaultOption) {
        this.defaultOption = defaultOption;
    }
}