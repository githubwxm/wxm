package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PackageOrderItemSalesChain implements Serializable {
    /**
     *  ,所属表字段为t_package_order_item_sales_chain.id
     */
    private Integer id;

    /**
     *  子订单ID,所属表字段为t_package_order_item_sales_chain.package_order_item_id
     */
    private Integer package_order_item_id;

    /**
     *  游玩日期,所属表字段为t_package_order_item_sales_chain.day
     */
    private Date day;

    /**
     *  企业ID,所属表字段为t_package_order_item_sales_chain.ep_id
     */
    private Integer ep_id;

    /**
     *  卖家企业ID,所属表字段为t_package_order_item_sales_chain.sale_ep_id
     */
    private Integer sale_ep_id;

    /**
     *  平台商ID,所属表字段为t_package_order_item_sales_chain.core_ep_id
     */
    private Integer core_ep_id;

    /**
     *  卖家平台商ID,所属表字段为t_package_order_item_sales_chain.sale_core_ep_id
     */
    private Integer sale_core_ep_id;

    /**
     *  进货价,所属表字段为t_package_order_item_sales_chain.in_price
     */
    private Integer in_price;

    /**
     *  出货价,所属表字段为t_package_order_item_sales_chain.out_price
     */
    private Integer out_price;

    /**
     * 序列化ID,t_package_order_item_sales_chain
     */
    private static final long serialVersionUID = 1L;

}