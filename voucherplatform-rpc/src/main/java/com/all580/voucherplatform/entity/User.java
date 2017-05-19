package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    /**
     *  ,所属表字段为t_user.id
     */
    private Integer id;

    /**
     *  用户名,所属表字段为t_user.name
     */
    private String name;

    /**
     *  密码,所属表字段为t_user.password
     */
    private String password;

    /**
     *  用户类型,所属表字段为t_user.type
     */
    private Integer type;

    /**
     *  用户启用状态,所属表字段为t_user.status
     */
    private Boolean status;

    /**
     *  创建时间,所属表字段为t_user.createTime
     */
    private Date createTime;

    /**
     * 序列化ID,t_user
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_user.id
     *
     * @return t_user.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_user.id
     *
     * @param id t_user.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 用户名 字段:t_user.name
     *
     * @return t_user.name, 用户名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 用户名 字段:t_user.name
     *
     * @param name t_user.name, 用户名
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 密码 字段:t_user.password
     *
     * @return t_user.password, 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置 密码 字段:t_user.password
     *
     * @param password t_user.password, 密码
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * 获取 用户类型 字段:t_user.type
     *
     * @return t_user.type, 用户类型
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置 用户类型 字段:t_user.type
     *
     * @param type t_user.type, 用户类型
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取 用户启用状态 字段:t_user.status
     *
     * @return t_user.status, 用户启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 用户启用状态 字段:t_user.status
     *
     * @param status t_user.status, 用户启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 创建时间 字段:t_user.createTime
     *
     * @return t_user.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_user.createTime
     *
     * @param createTime t_user.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}