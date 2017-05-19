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
     *  供应商id,所属表字段为t_template.supplier_id
     */
    private Integer supplier_id;

    /**
     *  供应商产品id,所属表字段为t_template.supplierproduct_id
     */
    private Integer supplierproduct_id;

    /**
     *  ,所属表字段为t_template.status
     */
    private Boolean status;

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
     * 获取 供应商id 字段:t_template.supplier_id
     *
     * @return t_template.supplier_id, 供应商id
     */
    public Integer getSupplier_id() {
        return supplier_id;
    }

    /**
     * 设置 供应商id 字段:t_template.supplier_id
     *
     * @param supplier_id t_template.supplier_id, 供应商id
     */
    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    /**
     * 获取 供应商产品id 字段:t_template.supplierproduct_id
     *
     * @return t_template.supplierproduct_id, 供应商产品id
     */
    public Integer getSupplierproduct_id() {
        return supplierproduct_id;
    }

    /**
     * 设置 供应商产品id 字段:t_template.supplierproduct_id
     *
     * @param supplierproduct_id t_template.supplierproduct_id, 供应商产品id
     */
    public void setSupplierproduct_id(Integer supplierproduct_id) {
        this.supplierproduct_id = supplierproduct_id;
    }

    /**
     * 获取  字段:t_template.status
     *
     * @return t_template.status, 
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置  字段:t_template.status
     *
     * @param status t_template.status, 
     */
    public void setStatus(Boolean status) {
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