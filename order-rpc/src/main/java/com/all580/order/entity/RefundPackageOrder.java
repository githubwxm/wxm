package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RefundPackageOrder implements Serializable {
    /**
     *  ,所属表字段为t_refund_package_order.id
     */
    private Integer id;

    /**
     *  流水编号,所属表字段为t_refund_package_order.number
     */
    private Long number;

    /**
     *  子订单id,所属表字段为t_refund_package_order.package_order_item_id
     */
    private Integer package_order_item_id;

    /**
     *  数量,所属表字段为t_refund_package_order.quantity
     */
    private Integer quantity;

    /**
     *  退支付者金额,所属表字段为t_refund_package_order.money
     */
    private Integer money;

    /**
     *  手续费,所属表字段为t_refund_package_order.fee
     */
    private Integer fee;

    /**
     *  通道费率,所属表字段为t_refund_package_order.channel_fee
     */
    private Integer channel_fee;

    /**
     *  ,所属表字段为t_refund_package_order.create_time
     */
    private Date create_time;

    /**
     *  退订原因,所属表字段为t_refund_package_order.cause
     */
    private String cause;

    /**
     *  申请退订用户ID,所属表字段为t_refund_package_order.apply_user_id
     */
    private Integer apply_user_id;

    /**
     *  申请退订用户名,所属表字段为t_refund_package_order.apply_user_name
     */
    private String apply_user_name;

    /**
     *  外部订单号(退订),所属表字段为t_refund_package_order.outer_id
     */
    private String outer_id;

    /**
     * 序列化ID,t_refund_package_order
     */
    private static final long serialVersionUID = 1L;

}