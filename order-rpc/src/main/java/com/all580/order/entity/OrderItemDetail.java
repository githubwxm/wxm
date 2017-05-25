package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderItemDetail implements Serializable {
    /**
     *  ,所属表字段为t_order_item_detail.id
     */
    private Integer id;

    /**
     *  子订单id,所属表字段为t_order_item_detail.order_item_id
     */
    private Integer order_item_id;

    /**
     *  日期,所属表字段为t_order_item_detail.day
     */
    private Date day;

    /**
     *  数量,所属表字段为t_order_item_detail.quantity
     */
    private Integer quantity;

    /**
     *  核销数量,所属表字段为t_order_item_detail.used_quantity
     */
    private Integer used_quantity;

    /**
     *  退订数量,所属表字段为t_order_item_detail.refund_quantity
     */
    private Integer refund_quantity;

    /**
     *  销售侧退款规则,所属表字段为t_order_item_detail.cust_refund_rule
     */
    private String cust_refund_rule;

    /**
     *  供应侧退款规则,所属表字段为t_order_item_detail.saler_refund_rule
     */
    private String saler_refund_rule;

    /**
     *  验票规则,所属表字段为t_order_item_detail.clearance_rule
     */
    private String clearance_rule;

    /**
     *  是否超买,所属表字段为t_order_item_detail.oversell
     */
    private Boolean oversell;

    /**
     *  使用时间限制(多少小时后才能使用),所属表字段为t_order_item_detail.use_hours_limit
     */
    private Integer use_hours_limit;

    /**
     *  过期时间,所属表字段为t_order_item_detail.expiry_date
     */
    private Date expiry_date;

    /**
     *  禁用星期,所属表字段为t_order_item_detail.disable_week
     */
    private String disable_week;

    /**
     *  禁用日期,所属表字段为t_order_item_detail.disable_day
     */
    private String disable_day;

    /**
     *  生效时间,所属表字段为t_order_item_detail.effective_date
     */
    private Date effective_date;

    /**
     *  最低使用张数(团队),所属表字段为t_order_item_detail.low_quantity
     */
    private Integer low_quantity;

    /**
     *  供应价,所属表字段为t_order_item_detail.supply_price
     */
    private Integer supply_price;

    /**
     *  售卖价,所属表字段为t_order_item_detail.sale_price
     */
    private Integer sale_price;

    /**
     *  结算价,所属表字段为t_order_item_detail.settle_price
     */
    private Integer settle_price;

    /**
     *  ,所属表字段为t_order_item_detail.create_time
     */
    private Date create_time;

    /**
     *  ,所属表字段为t_order_item_detail.update_time
     */
    private String update_time;

    /**
     * 序列化ID,t_order_item_detail
     */
    private static final long serialVersionUID = 1L;

}