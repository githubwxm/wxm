package com.all580.order.entity;

import lombok.*;

import java.io.Serializable;

@Data
public class RefundAccount implements Serializable {
    /**
     * 所属表字段为 t_refund_account.id
     */
    private Integer id;

    /**
     * 退订订单id
     * 所属表字段为 t_refund_account.refund_order_id
     */
    private Integer refundOrderId;

    /**
     * 分账金额
     * 所属表字段为 t_refund_account.money
     */
    private Integer money;

    /**
     * 利润
     * 所属表字段为 t_refund_account.profit
     */
    private Integer profit;

    /**
     * 分账企业id
     * 所属表字段为 t_refund_account.ep_id
     */
    private Integer epId;

    /**
     * 0-未结清 1-已结清
     * 所属表字段为 t_refund_account.status
     */
    private Integer status;

    /**
     * 分账企业余额托管平台商id
     * 所属表字段为 t_refund_account.core_ep_id
     */
    private Integer coreEpId;

    /**
     * json  每日数据
     * 所属表字段为 t_refund_account.data
     */
    private String data;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_refund_account
     *
     * @mbggenerated Fri Sep 30 15:22:08 CST 2016
     */
    private static final long serialVersionUID = 1L;

}