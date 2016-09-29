package com.all580.order.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
public class RefundSerial implements Serializable {
    /**
     * 所属表字段为 t_refund_serial.id
     */
    private Integer id;

    /**
     * 本地交易流水
     * 所属表字段为 t_refund_serial.local_serial_no
     */
    private Integer localSerialNo;

    /**
     * 第三方交易流水
     * 所属表字段为 t_refund_serial.remote_serial_no
     */
    private String remoteSerialNo;

    /**
     * 数量
     * 所属表字段为 t_refund_serial.quantity
     */
    private Integer quantity;

    /**
     * json 按日期存每天退票数
     * 所属表字段为 t_refund_serial.data
     */
    private String data;

    /**
     * 退订订单id
     * 所属表字段为 t_refund_serial.refund_order_id
     */
    private Integer refundOrderId;

    /**
     * 退票时间
     * 所属表字段为 t_refund_serial.refund_time
     */
    private Date refundTime;

    /**
     * 创建时间
     * 所属表字段为 t_refund_serial.create_time
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_refund_serial
     *
     * @mbggenerated Thu Sep 29 13:58:49 CST 2016
     */
    private static final long serialVersionUID = 1L;

}