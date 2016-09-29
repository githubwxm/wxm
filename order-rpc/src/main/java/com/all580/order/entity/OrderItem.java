package com.all580.order.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItem implements Serializable {
    /**
     * 所属表字段为 t_order_item.id
     */
    private Integer id;

    /**
     * 所属表字段为 t_order_item.number
     */
    private Long number;

    /**
     * 主订单id
     * 所属表字段为 t_order_item.order_id
     */
    private Integer orderId;

    /**
     * 子产品id
     * 所属表字段为 t_order_item.pro_sub_id
     */
    private Integer proSubId;

    /**
     * 主产品名
     * 所属表字段为 t_order_item.pro_name
     */
    private String proName;

    /**
     * 子产品名
     * 所属表字段为 t_order_item.pro_sub_name
     */
    private String proSubName;

    /**
     * 开始时间
     * 所属表字段为 t_order_item.start
     */
    private Date start;

    /**
     * 结束时间
     * 所属表字段为 t_order_item.end
     */
    private Date end;

    /**
     * 天数
     * 所属表字段为 t_order_item.days
     */
    private Integer days;

    /**
     * 数量
     * 所属表字段为 t_order_item.quantity
     */
    private Integer quantity;

    /**
     * 1-待审核
2-审核通过
3-未出票
4-出票失败
5-出票中
6-已出票
7-已取消
     * 所属表字段为 t_order_item.status
     */
    private Integer status;

    /**
     * 出票调用记录id
     * 所属表字段为 t_order_item.send_ma_log_rec_id
     */
    private Integer sendMaLogRecId;

    /**
     * 出票时间
     * 所属表字段为 t_order_item.send_ma_time
     */
    private Date sendMaTime;

    /**
     * 团队id 0表示散客
     * 所属表字段为 t_order_item.group_id
     */
    private Integer groupId;

    /**
     * 1-预付
2-平台代收预付
3-到付
     * 所属表字段为 t_order_item.payment_flag
     */
    private Integer paymentFlag;

    /**
     * 销售金额
     * 所属表字段为 t_order_item.sale_amount
     */
    private BigDecimal saleAmount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_order_item
     *
     * @mbggenerated Thu Sep 29 13:58:49 CST 2016
     */
    private static final long serialVersionUID = 1L;

}