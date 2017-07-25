package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by xiangzw on 2017/7/24.
 */
@Data
public class PackageOrderItemAccount implements Serializable{
    /**
     * 所属表字段为 t_order_item_account.id
     */
    private Integer id;

    /**
     * 子订单id
     * 所属表字段为 t_order_item_account.package_order_item_id
     */
    private Integer package_order_item_id;

    /**
     * 应分账余额
     * 所属表字段为 t_order_item_account.money
     */
    private Integer money;

    /**
     * 总利润
     * 所属表字段为 t_order_item_account.profit
     */
    private Integer profit;

    /**
     * 已分账余额
     * 所属表字段为 t_order_item_account.settled_money
     */
    private Integer settled_money;

    /**
     * 分账企业id
     * 所属表字段为 t_order_item_account.ep_id
     */
    private Integer ep_id;

    /**
     * 卖家企业ID
     * 所属表字段为 t_order_item_account.sale_ep_id
     */
    private Integer sale_ep_id;

    /**
     * 0-未结清 1-已结清
     * 所属表字段为 t_order_item_account.status
     */
    private Integer status;

    /**
     * 分账企业余额托管平台商id
     * 所属表字段为 t_order_item_account.core_ep_id
     */
    private Integer core_ep_id;

    /**
     * json 每日出货单价/进货单价/利润
     * 所属表字段为 t_order_item_account.data
     */
    private String data;

    /**
     * 所属表字段为 t_order_item_account.update_time
     */
    private String update_time;
}
