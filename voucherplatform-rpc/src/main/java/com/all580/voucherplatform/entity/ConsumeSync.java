package com.all580.voucherplatform.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
@Data
public class ConsumeSync implements Serializable {
    private Integer id;

    /**
     * 商户
     */
    private Integer supply;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 状态:1-已创建;2-已发送;3-发送失败;4-已接收
     */
    private Integer status;

    private static final long serialVersionUID = 1L;

}