package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class PlatformProduct implements Serializable {
    /**
     *  ,所属表字段为t_platformproduct.id
     */
    private Integer id;

    /**
     *  外部平台的产品号,所属表字段为t_platformproduct.code
     */
    private String code;

    /**
     *  外部平台的产品名称,所属表字段为t_platformproduct.name
     */
    private String name;

    /**
     *  产品类型,所属表字段为t_platformproduct.producttype_id
     */
    private Integer producttype_id;

    /**
     *  所属的平台的id,所属表字段为t_platformproduct.platform_id
     */
    private Integer platform_id;

    /**
     *  所属的平台的授权id,所属表字段为t_platformproduct.platformrole_id
     */
    private Integer platformrole_id;

    /**
     *  产品对应的供应商id,所属表字段为t_platformproduct.supply_id
     */
    private Integer supply_id;

    /**
     *  产品对应的供应商产品id,所属表字段为t_platformproduct.supplyprod_id
     */
    private Integer supplyprod_id;

    /**
     *  启用状态,所属表字段为t_platformproduct.status
     */
    private Boolean status;

    /**
     *  创建时间,所属表字段为t_platformproduct.createTime
     */
    private Date createTime;

    /**
     *  修改时间,所属表字段为t_platformproduct.modifyTime
     */
    private Date modifyTime;

    /**
     * 序列化ID,t_platformproduct
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_platformproduct.id
     *
     * @return t_platformproduct.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_platformproduct.id
     *
     * @param id t_platformproduct.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 外部平台的产品号 字段:t_platformproduct.code
     *
     * @return t_platformproduct.code, 外部平台的产品号
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置 外部平台的产品号 字段:t_platformproduct.code
     *
     * @param code t_platformproduct.code, 外部平台的产品号
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取 外部平台的产品名称 字段:t_platformproduct.name
     *
     * @return t_platformproduct.name, 外部平台的产品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 外部平台的产品名称 字段:t_platformproduct.name
     *
     * @param name t_platformproduct.name, 外部平台的产品名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 产品类型 字段:t_platformproduct.producttype_id
     *
     * @return t_platformproduct.producttype_id, 产品类型
     */
    public Integer getProducttype_id() {
        return producttype_id;
    }

    /**
     * 设置 产品类型 字段:t_platformproduct.producttype_id
     *
     * @param producttype_id t_platformproduct.producttype_id, 产品类型
     */
    public void setProducttype_id(Integer producttype_id) {
        this.producttype_id = producttype_id;
    }

    /**
     * 获取 所属的平台的id 字段:t_platformproduct.platform_id
     *
     * @return t_platformproduct.platform_id, 所属的平台的id
     */
    public Integer getPlatform_id() {
        return platform_id;
    }

    /**
     * 设置 所属的平台的id 字段:t_platformproduct.platform_id
     *
     * @param platform_id t_platformproduct.platform_id, 所属的平台的id
     */
    public void setPlatform_id(Integer platform_id) {
        this.platform_id = platform_id;
    }

    /**
     * 获取 所属的平台的授权id 字段:t_platformproduct.platformrole_id
     *
     * @return t_platformproduct.platformrole_id, 所属的平台的授权id
     */
    public Integer getPlatformrole_id() {
        return platformrole_id;
    }

    /**
     * 设置 所属的平台的授权id 字段:t_platformproduct.platformrole_id
     *
     * @param platformrole_id t_platformproduct.platformrole_id, 所属的平台的授权id
     */
    public void setPlatformrole_id(Integer platformrole_id) {
        this.platformrole_id = platformrole_id;
    }

    /**
     * 获取 产品对应的供应商id 字段:t_platformproduct.supply_id
     *
     * @return t_platformproduct.supply_id, 产品对应的供应商id
     */
    public Integer getSupply_id() {
        return supply_id;
    }

    /**
     * 设置 产品对应的供应商id 字段:t_platformproduct.supply_id
     *
     * @param supply_id t_platformproduct.supply_id, 产品对应的供应商id
     */
    public void setSupply_id(Integer supply_id) {
        this.supply_id = supply_id;
    }

    /**
     * 获取 产品对应的供应商产品id 字段:t_platformproduct.supplyprod_id
     *
     * @return t_platformproduct.supplyprod_id, 产品对应的供应商产品id
     */
    public Integer getSupplyprod_id() {
        return supplyprod_id;
    }

    /**
     * 设置 产品对应的供应商产品id 字段:t_platformproduct.supplyprod_id
     *
     * @param supplyprod_id t_platformproduct.supplyprod_id, 产品对应的供应商产品id
     */
    public void setSupplyprod_id(Integer supplyprod_id) {
        this.supplyprod_id = supplyprod_id;
    }

    /**
     * 获取 启用状态 字段:t_platformproduct.status
     *
     * @return t_platformproduct.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_platformproduct.status
     *
     * @param status t_platformproduct.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 创建时间 字段:t_platformproduct.createTime
     *
     * @return t_platformproduct.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_platformproduct.createTime
     *
     * @param createTime t_platformproduct.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 修改时间 字段:t_platformproduct.modifyTime
     *
     * @return t_platformproduct.modifyTime, 修改时间
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置 修改时间 字段:t_platformproduct.modifyTime
     *
     * @param modifyTime t_platformproduct.modifyTime, 修改时间
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}