package com.all580.order.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
public class ShippingModify implements Serializable {
    /**
     *  ,所属表字段为t_shipping_modify.id
     */
    private Integer id;

    /**
     *  联系人ID,所属表字段为t_shipping_modify.shipping_id
     */
    private Integer shipping_id;

    /**
     *  联系人名称,所属表字段为t_shipping_modify.shipping_name
     */
    private String shipping_name;

    /**
     *  联系人手机,所属表字段为t_shipping_modify.shipping_phone
     */
    private String shipping_phone;

    /**
     *  联系人身份证,所属表字段为t_shipping_modify.shipping_sid
     */
    private String shipping_sid;

    /**
     *  订单ID,所属表字段为t_shipping_modify.order_id
     */
    private Integer order_id;

    /**
     *  0 未修改 1 已修改,所属表字段为t_shipping_modify.status
     */
    private Boolean status;

    /**
     *  ,所属表字段为t_shipping_modify.create_time
     */
    private Date create_time;

    /**
     * 序列化ID,t_shipping_modify
     */
    private static final long serialVersionUID = 1L;

}