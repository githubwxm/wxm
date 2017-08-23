package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RefundPackageAccount implements Serializable {
    /**
     *  ,所属表字段为t_refund_package_account.id
     */
    private Integer id;

    /**
     *  退订订单id,所属表字段为t_refund_package_account.refund_package_order_id
     */
    private Integer refund_package_order_id;

    /**
     *  分账金额,所属表字段为t_refund_package_account.money
     */
    private Integer money;

    /**
     *  利润,所属表字段为t_refund_package_account.profit
     */
    private Integer profit;

    /**
     *  分账企业id,所属表字段为t_refund_package_account.ep_id
     */
    private Integer ep_id;

    /**
     *  0-未结清 1-已结清,所属表字段为t_refund_package_account.status
     */
    private Integer status;

    /**
     *  分账企业余额托管平台商id,所属表字段为t_refund_package_account.core_ep_id
     */
    private Integer core_ep_id;

    /**
     *  json  每日数据,所属表字段为t_refund_package_account.data
     */
    private String data;

    /**
     *  ,所属表字段为t_refund_package_account.update_time
     */
    private Date update_time;

    /**
     * 序列化ID,t_refund_package_account
     */
    private static final long serialVersionUID = 1L;

}