package com.all580.voucherplatform.entity;

import java.io.Serializable;
import java.util.Date;

public class SmsHistory implements Serializable {
    /**
     *  ,所属表字段为t_smshistory.Id
     */
    private Integer id;

    /**
     *  手机号,所属表字段为t_smshistory.mobile
     */
    private String mobile;

    /**
     *  短信内容,所属表字段为t_smshistory.content
     */
    private String content;

    /**
     *  发送状态,所属表字段为t_smshistory.sendStatus
     */
    private String sendStatus;

    /**
     *  最后一次发送时间,所属表字段为t_smshistory.lastSendTime
     */
    private Date lastSendTime;

    /**
     *  创建时间,所属表字段为t_smshistory.createTime
     */
    private Date createTime;

    /**
     *  发送成功时间,所属表字段为t_smshistory.successTime
     */
    private Date successTime;

    /**
     *  发送次数,所属表字段为t_smshistory.sendTotal
     */
    private Integer sendTotal;

    /**
     * 序列化ID,t_smshistory
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取  字段:t_smshistory.Id
     *
     * @return t_smshistory.Id, 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置  字段:t_smshistory.Id
     *
     * @param id t_smshistory.Id, 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 手机号 字段:t_smshistory.mobile
     *
     * @return t_smshistory.mobile, 手机号
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置 手机号 字段:t_smshistory.mobile
     *
     * @param mobile t_smshistory.mobile, 手机号
     */
    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    /**
     * 获取 短信内容 字段:t_smshistory.content
     *
     * @return t_smshistory.content, 短信内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置 短信内容 字段:t_smshistory.content
     *
     * @param content t_smshistory.content, 短信内容
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    /**
     * 获取 发送状态 字段:t_smshistory.sendStatus
     *
     * @return t_smshistory.sendStatus, 发送状态
     */
    public String getSendStatus() {
        return sendStatus;
    }

    /**
     * 设置 发送状态 字段:t_smshistory.sendStatus
     *
     * @param sendStatus t_smshistory.sendStatus, 发送状态
     */
    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus == null ? null : sendStatus.trim();
    }

    /**
     * 获取 最后一次发送时间 字段:t_smshistory.lastSendTime
     *
     * @return t_smshistory.lastSendTime, 最后一次发送时间
     */
    public Date getLastSendTime() {
        return lastSendTime;
    }

    /**
     * 设置 最后一次发送时间 字段:t_smshistory.lastSendTime
     *
     * @param lastSendTime t_smshistory.lastSendTime, 最后一次发送时间
     */
    public void setLastSendTime(Date lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    /**
     * 获取 创建时间 字段:t_smshistory.createTime
     *
     * @return t_smshistory.createTime, 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置 创建时间 字段:t_smshistory.createTime
     *
     * @param createTime t_smshistory.createTime, 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取 发送成功时间 字段:t_smshistory.successTime
     *
     * @return t_smshistory.successTime, 发送成功时间
     */
    public Date getSuccessTime() {
        return successTime;
    }

    /**
     * 设置 发送成功时间 字段:t_smshistory.successTime
     *
     * @param successTime t_smshistory.successTime, 发送成功时间
     */
    public void setSuccessTime(Date successTime) {
        this.successTime = successTime;
    }

    /**
     * 获取 发送次数 字段:t_smshistory.sendTotal
     *
     * @return t_smshistory.sendTotal, 发送次数
     */
    public Integer getSendTotal() {
        return sendTotal;
    }

    /**
     * 设置 发送次数 字段:t_smshistory.sendTotal
     *
     * @param sendTotal t_smshistory.sendTotal, 发送次数
     */
    public void setSendTotal(Integer sendTotal) {
        this.sendTotal = sendTotal;
    }
}