package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PackageOrderItem implements Serializable {
    /**
     *  主键,所属表字段为t_package_order_item.id
     */
    private Integer id;

    /**
     *  套餐主订单编号,所属表字段为t_package_order_item.order_number
     */
    private Long order_number;

    /**
     *  子产品id,所属表字段为t_package_order_item.product_sub_id
     */
    private Integer product_sub_id;

    /**
     *  子产品number,所属表字段为t_package_order_item.product_sub_code
     */
    private Long product_sub_code;

    /**
     *  子产品name,所属表字段为t_package_order_item.product_sub_name
     */
    private String product_sub_name;

    /**
     *  主产品name,所属表字段为t_package_order_item.product_name
     */
    private String product_name;

    /**
     *  销售侧退款规则,所属表字段为t_package_order_item.cust_refund_rule
     */
    private String cust_refund_rule;

    /**
     *  供应侧退款规则,所属表字段为t_package_order_item.saler_refund_rule
     */
    private String saler_refund_rule;

    /**
     *  开始时间,所属表字段为t_package_order_item.start
     */
    private Date start;

    /**
     *  数量,所属表字段为t_package_order_item.quantity
     */
    private Integer quantity;

    /**
     *  已用总张数,所属表字段为t_package_order_item.used_quantity
     */
    private Integer used_quantity;

    /**
     *  已退订总张数,所属表字段为t_package_order_item.refund_quantity
     */
    private Integer refund_quantity;

    /**
     *  元素订单支付金额总和,所属表字段为t_package_order_item.pay_amount
     */
    private Integer pay_amount;

    /**
     *  元素订单销售金额总和,所属表字段为t_package_order_item.sale_amount
     */
    private Integer sale_amount;

    /**
     *  支付方式,所属表字段为t_package_order_item.payment_flag
     */
    private Integer payment_flag;

    /**
     *  供应价,所属表字段为t_package_order_item.supply_price
     */
    private Integer supply_price;

    /**
     *  售卖价,所属表字段为t_package_order_item.sale_price
     */
    private Integer sale_price;

    /**
     *  结算价,所属表字段为t_package_order_item.settle_price
     */
    private Integer settle_price;

    /**
     *  企业id,所属表字段为t_package_order_item.ep_id
     */
    private Integer ep_id;

    /**
     *  平台商id,所属表字段为t_package_order_item.core_ep_id
     */
    private Integer core_ep_id;

    /**
     *  ,所属表字段为t_package_order_item.update_time
     */
    private Date update_time;

    /**
     *  审核时间,所属表字段为t_package_order_item.audit_time
     */
    private Date audit_time;

    /**
     *  审核结果,所属表字段为t_package_order_item.audit
     */
    private Integer audit;

    /**
     * 序列化ID,t_package_order_item
     */
    private static final long serialVersionUID = 1L;

}