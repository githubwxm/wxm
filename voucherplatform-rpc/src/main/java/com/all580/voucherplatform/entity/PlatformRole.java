package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class PlatformRole implements Serializable {
    /**
     *  ,所属表字段为t_platformrole.id
     */
    private Integer id;

    /**
     *  商户的自定义名称,所属表字段为t_platformrole.name
     */
    private String name;

    /**
     *  ,所属表字段为t_platformrole.authId
     */
    private Integer authId;

    /**
     *  ,所属表字段为t_platformrole.authKey
     */
    private String authKey;

    /**
     *  平台id,所属表字段为t_platformrole.platform_id
     */
    private Integer platform_id;

    /**
     *  所授权的供应商id,所属表字段为t_platformrole.supplier_id
     */
    private Integer supplier_id;

    /**
     *  创建时间,所属表字段为t_platformrole.createTime
     */
    private Date createTime;

    /**
     *  认证时间,所属表字段为t_platformrole.authTime
     */
    private Date authTime;

    /**
     *  外部的商户id,所属表字段为t_platformrole.merchantCode
     */
    private String merchantCode;

    /**
     *  修改时间,所属表字段为t_platformrole.modifyTime
     */
    private Date modifyTime;

    /**
     * 序列化ID,t_platformrole
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_platformrole.id
     *
     * @return t_platformrole.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_platformrole.id
     *
     * @param id t_platformrole.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 商户的自定义名称 字段:t_platformrole.name
     *
     * @return t_platformrole.name, 商户的自定义名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 商户的自定义名称 字段:t_platformrole.name
     *
     * @param name t_platformrole.name, 商户的自定义名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取  字段:t_platformrole.authId
     *
     * @return t_platformrole.authId, 
     */
    public Integer getAuthId() {
        return authId;
    }

    /**
     * 设置  字段:t_platformrole.authId
     *
     * @param authId t_platformrole.authId, 
     */
    public void setAuthId(Integer authId) {
        this.authId = authId;
    }

    /**
     * 获取  字段:t_platformrole.authKey
     *
     * @return t_platformrole.authKey, 
     */
    public String getAuthKey() {
        return authKey;
    }

    /**
     * 设置  字段:t_platformrole.authKey
     *
     * @param authKey t_platformrole.authKey, 
     */
    public void setAuthKey(String authKey) {
        this.authKey = authKey == null ? null : authKey.trim();
    }

    /**
     * 获取 平台id 字段:t_platformrole.platform_id
     *
     * @return t_platformrole.platform_id, 平台id
     */
    public Integer getPlatform_id() {
        return platform_id;
    }

    /**
     * 设置 平台id 字段:t_platformrole.platform_id
     *
     * @param platform_id t_platformrole.platform_id, 平台id
     */
    public void setPlatform_id(Integer platform_id) {
        this.platform_id = platform_id;
    }

    /**
     * 获取 所授权的供应商id 字段:t_platformrole.supplier_id
     *
     * @return t_platformrole.supplier_id, 所授权的供应商id
     */
    public Integer getSupplier_id() {
        return supplier_id;
    }

    /**
     * 设置 所授权的供应商id 字段:t_platformrole.supplier_id
     *
     * @param supplier_id t_platformrole.supplier_id, 所授权的供应商id
     */
    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    /**
     * 获取 创建时间 字段:t_platformrole.createTime
     *
     * @return t_platformrole.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_platformrole.createTime
     *
     * @param createTime t_platformrole.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 认证时间 字段:t_platformrole.authTime
     *
     * @return t_platformrole.authTime, 认证时间
     */
    public Date getAuthTime() {
        return authTime;
    }

    /**
     * 设置 认证时间 字段:t_platformrole.authTime
     *
     * @param authTime t_platformrole.authTime, 认证时间
     */
    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }

    /**
     * 获取 外部的商户id 字段:t_platformrole.merchantCode
     *
     * @return t_platformrole.merchantCode, 外部的商户id
     */
    public String getMerchantCode() {
        return merchantCode;
    }

    /**
     * 设置 外部的商户id 字段:t_platformrole.merchantCode
     *
     * @param merchantCode t_platformrole.merchantCode, 外部的商户id
     */
    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode == null ? null : merchantCode.trim();
    }

    /**
     * 获取 修改时间 字段:t_platformrole.modifyTime
     *
     * @return t_platformrole.modifyTime, 修改时间
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置 修改时间 字段:t_platformrole.modifyTime
     *
     * @param modifyTime t_platformrole.modifyTime, 修改时间
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}