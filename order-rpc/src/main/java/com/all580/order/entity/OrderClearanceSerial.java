package com.all580.order.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderClearanceSerial implements Serializable {
    /**
     * 所属表字段为 t_order_clearance_serial.id
     */
    private Integer id;

    /**
     * 第三方流水号
     * 所属表字段为 t_order_clearance_serial.serial_no
     */
    private String serialNo;

    /**
     * 子订单id
     * 所属表字段为 t_order_clearance_serial.order_item_id
     */
    private Integer orderItemId;

    /**
     * 核销数量
     * 所属表字段为 t_order_clearance_serial.quantity
     */
    private Integer quantity;

    /**
     * 哪一天的票
     * 所属表字段为 t_order_clearance_serial.day
     */
    private Integer day;

    /**
     * 核销时间
     * 所属表字段为 t_order_clearance_serial.clearance_time
     */
    private Date clearanceTime;

    /**
     * 创建时间
     * 所属表字段为 t_order_clearance_serial.create_time
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_order_clearance_serial
     *
     * @mbggenerated Thu Sep 29 13:58:49 CST 2016
     */
    private static final long serialVersionUID = 1L;

}