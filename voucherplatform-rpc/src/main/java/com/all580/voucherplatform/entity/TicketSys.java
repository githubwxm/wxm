package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class TicketSys implements Serializable {
    /**
     *  ,所属表字段为t_ticketsys.id
     */
    private Integer id;

    /**
     *  实现的票务系统名称,所属表字段为t_ticketsys.name
     */
    private String name;

    /**
     *  实现的票务系统版本,所属表字段为t_ticketsys.version
     */
    private String version;

    /**
     *  实现的票务系统包名,所属表字段为t_ticketsys.implPacket
     */
    private String implPacket;

    /**
     *  启用状态,所属表字段为t_ticketsys.status
     */
    private Boolean status;

    /**
     *  创建时间,所属表字段为t_ticketsys.createTime
     */
    private Date createTime;

    /**
     * 序列化ID,t_ticketsys
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_ticketsys.id
     *
     * @return t_ticketsys.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_ticketsys.id
     *
     * @param id t_ticketsys.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 实现的票务系统名称 字段:t_ticketsys.name
     *
     * @return t_ticketsys.name, 实现的票务系统名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 实现的票务系统名称 字段:t_ticketsys.name
     *
     * @param name t_ticketsys.name, 实现的票务系统名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 实现的票务系统版本 字段:t_ticketsys.version
     *
     * @return t_ticketsys.version, 实现的票务系统版本
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置 实现的票务系统版本 字段:t_ticketsys.version
     *
     * @param version t_ticketsys.version, 实现的票务系统版本
     */
    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    /**
     * 获取 实现的票务系统包名 字段:t_ticketsys.implPacket
     *
     * @return t_ticketsys.implPacket, 实现的票务系统包名
     */
    public String getImplPacket() {
        return implPacket;
    }

    /**
     * 设置 实现的票务系统包名 字段:t_ticketsys.implPacket
     *
     * @param implPacket t_ticketsys.implPacket, 实现的票务系统包名
     */
    public void setImplPacket(String implPacket) {
        this.implPacket = implPacket == null ? null : implPacket.trim();
    }

    /**
     * 获取 启用状态 字段:t_ticketsys.status
     *
     * @return t_ticketsys.status, 启用状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 启用状态 字段:t_ticketsys.status
     *
     * @param status t_ticketsys.status, 启用状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取 创建时间 字段:t_ticketsys.createTime
     *
     * @return t_ticketsys.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_ticketsys.createTime
     *
     * @param createTime t_ticketsys.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}