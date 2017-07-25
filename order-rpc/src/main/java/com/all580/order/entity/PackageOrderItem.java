package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xiangzw on 2017/7/13.
 */
@Data
public class PackageOrderItem implements Serializable{
    private Integer id;
    private Long order_number;
    private Integer product_sub_id;
    private Long product_sub_code;
    private String product_sub_name;
    private String product_name;
    private String cust_refund_rule;
    private String saler_refund_rule;
    private Date start;
    private Integer quantity;
    private Integer used_quantity;
    private Integer refund_quantity;
    private Integer pay_amount;
    private Integer sale_amount;
    private Integer payment_flag;
    private Integer supply_price;
    private Integer sale_price;
    private Integer settle_price;
    private Integer ep_id;
    private Integer core_ep_id;
    private Date update_time;
    private Date audit_time;
    private Integer audit;
}
