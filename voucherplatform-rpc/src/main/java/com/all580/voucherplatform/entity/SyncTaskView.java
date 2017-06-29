package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class SyncTaskView implements Serializable {
    /**
     *  ,所属表字段为t_synctaskview.id
     */
    private Integer id;

    /**
     *  ,所属表字段为t_synctaskview.busType
     */
    private Integer busType;

    /**
     *  ,所属表字段为t_synctaskview.busName
     */
    private String busName;

    /**
     *  ,所属表字段为t_synctaskview.syncStatus
     */
    private Boolean syncStatus;

    /**
     *  ,所属表字段为t_synctaskview.syncTime
     */
    private Date syncTime;

    /**
     *  ,所属表字段为t_synctaskview.createTime
     */
    private Date createTime;

    /**
     *  ,所属表字段为t_synctaskview.busId1
     */
    private String busId1;

    /**
     *  ,所属表字段为t_synctaskview.busId2
     */
    private String busId2;

    /**
     *  ,所属表字段为t_synctaskview.busId3
     */
    private String busId3;

    /**
     *  ,所属表字段为t_synctaskview.busId4
     */
    private String busId4;

    /**
     *  ,所属表字段为t_synctaskview.busId5
     */
    private String busId5;

    /**
     * 序列化ID,t_synctaskview
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_synctaskview.id
     *
     * @return t_synctaskview.id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_synctaskview.id
     *
     * @param id t_synctaskview.id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取  字段:t_synctaskview.busType
     *
     * @return t_synctaskview.busType, 
     */
    public Integer getBusType() {
        return busType;
    }

    /**
     * 设置  字段:t_synctaskview.busType
     *
     * @param busType t_synctaskview.busType, 
     */
    public void setBusType(Integer busType) {
        this.busType = busType;
    }

    /**
     * 获取  字段:t_synctaskview.busName
     *
     * @return t_synctaskview.busName, 
     */
    public String getBusName() {
        return busName;
    }

    /**
     * 设置  字段:t_synctaskview.busName
     *
     * @param busName t_synctaskview.busName, 
     */
    public void setBusName(String busName) {
        this.busName = busName == null ? null : busName.trim();
    }

    /**
     * 获取  字段:t_synctaskview.syncStatus
     *
     * @return t_synctaskview.syncStatus, 
     */
    public Boolean getSyncStatus() {
        return syncStatus;
    }

    /**
     * 设置  字段:t_synctaskview.syncStatus
     *
     * @param syncStatus t_synctaskview.syncStatus, 
     */
    public void setSyncStatus(Boolean syncStatus) {
        this.syncStatus = syncStatus;
    }

    /**
     * 获取  字段:t_synctaskview.syncTime
     *
     * @return t_synctaskview.syncTime, 
     */
    public Date getSyncTime() {
        return syncTime;
    }

    /**
     * 设置  字段:t_synctaskview.syncTime
     *
     * @param syncTime t_synctaskview.syncTime, 
     */
    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    /**
     * 获取  字段:t_synctaskview.createTime
     *
     * @return t_synctaskview.createTime, 
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置  字段:t_synctaskview.createTime
     *
     * @param createTime t_synctaskview.createTime, 
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取  字段:t_synctaskview.busId1
     *
     * @return t_synctaskview.busId1, 
     */
    public String getBusId1() {
        return busId1;
    }

    /**
     * 设置  字段:t_synctaskview.busId1
     *
     * @param busId1 t_synctaskview.busId1, 
     */
    public void setBusId1(String busId1) {
        this.busId1 = busId1 == null ? null : busId1.trim();
    }

    /**
     * 获取  字段:t_synctaskview.busId2
     *
     * @return t_synctaskview.busId2, 
     */
    public String getBusId2() {
        return busId2;
    }

    /**
     * 设置  字段:t_synctaskview.busId2
     *
     * @param busId2 t_synctaskview.busId2, 
     */
    public void setBusId2(String busId2) {
        this.busId2 = busId2 == null ? null : busId2.trim();
    }

    /**
     * 获取  字段:t_synctaskview.busId3
     *
     * @return t_synctaskview.busId3, 
     */
    public String getBusId3() {
        return busId3;
    }

    /**
     * 设置  字段:t_synctaskview.busId3
     *
     * @param busId3 t_synctaskview.busId3, 
     */
    public void setBusId3(String busId3) {
        this.busId3 = busId3 == null ? null : busId3.trim();
    }

    /**
     * 获取  字段:t_synctaskview.busId4
     *
     * @return t_synctaskview.busId4, 
     */
    public String getBusId4() {
        return busId4;
    }

    /**
     * 设置  字段:t_synctaskview.busId4
     *
     * @param busId4 t_synctaskview.busId4, 
     */
    public void setBusId4(String busId4) {
        this.busId4 = busId4 == null ? null : busId4.trim();
    }

    /**
     * 获取  字段:t_synctaskview.busId5
     *
     * @return t_synctaskview.busId5, 
     */
    public String getBusId5() {
        return busId5;
    }

    /**
     * 设置  字段:t_synctaskview.busId5
     *
     * @param busId5 t_synctaskview.busId5, 
     */
    public void setBusId5(String busId5) {
        this.busId5 = busId5 == null ? null : busId5.trim();
    }
}