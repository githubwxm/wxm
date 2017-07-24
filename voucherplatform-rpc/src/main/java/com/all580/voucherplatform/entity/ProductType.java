package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class ProductType implements Serializable {
    /**
     *  ,所属表字段为t_producttype.id
     */
    private Integer id;

    /**
     *  分类名称,所属表字段为t_producttype.name
     */
    private String name;

    /**
     *  分类描述,所属表字段为t_producttype.description
     */
    private String description;

    /**
     *  创建时间,所属表字段为t_producttype.createTime
     */
    private Date createTime;

    /**
     *  是否为默认值,所属表字段为t_producttype.defaultOption
     */
    private Boolean defaultOption;

    /**
     *  启用状态,所属表字段为t_producttype.status
     */
    private Boolean status;

    /**
     * 序列化ID,t_producttype
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_producttype.id
     *
     * @return t_producttype.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_producttype.id
     *
     * @param id t_producttype.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 分类名称 字段:t_producttype.name
     *
     * @return t_producttype.name, 分类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 分类名称 字段:t_producttype.name
     *
     * @param name t_producttype.name, 分类名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 分类描述 字段:t_producttype.description
     *
     * @return t_producttype.description, 分类描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 分类描述 字段:t_producttype.description
     *
     * @param description t_producttype.description, 分类描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取 创建时间 字段:t_producttype.createTime
     *
     * @return t_producttype.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_producttype.createTime
     *
     * @param createTime t_producttype.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 是否为默认值 字段:t_producttype.defaultOption
     *
     * @return t_producttype.defaultOption, 是否为默认值
     */
    public Boolean getDefaultOption() {
        return defaultOption;
    }

    /**
     * 设置 是否为默认值 字段:t_producttype.defaultOption
     *
     * @param defaultOption t_producttype.defaultOption, 是否为默认值
     */
    public void setDefaultOption(Boolean defaultOption) {
        this.defaultOption = defaultOption;
    }

    /**
     * 获取 启用状态 字段:t_producttype.status
     *
     * @return t_producttype.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_producttype.status
     *
     * @param status t_producttype.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }
}