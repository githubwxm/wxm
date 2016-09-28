package com.all580.order.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItemAccount implements Serializable {
    /**
     * 所属表字段为 t_order_item_account.id
     */
    private Long id;

    /**
     * 子订单id
     * 所属表字段为 t_order_item_account.order_item_id
     */
    private Long orderItemId;

    /**
     * 应分账余额
     * 所属表字段为 t_order_item_account.money
     */
    private BigDecimal money;

    /**
     * 总利润
     * 所属表字段为 t_order_item_account.profit
     */
    private BigDecimal profit;

    /**
     * 已分账余额
     * 所属表字段为 t_order_item_account.settled_money
     */
    private BigDecimal settledMoney;

    /**
     * 分账企业id
     * 所属表字段为 t_order_item_account.ep_id
     */
    private Long epId;

    /**
     * 0-未结清 1-已结清
     * 所属表字段为 t_order_item_account.status
     */
    private Integer status;

    /**
     * 分账企业余额托管平台商id
     * 所属表字段为 t_order_item_account.core_ep_id
     */
    private Long coreEpId;

    /**
     * json 每日出货单价/进货单价/利润
     * 所属表字段为 t_order_item_account.data
     */
    private String data;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_order_item_account
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    private static final long serialVersionUID = 1L;

}