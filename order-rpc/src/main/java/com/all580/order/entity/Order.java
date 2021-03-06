package com.all580.order.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
public class Order implements Serializable {
    /**
     *  ,所属表字段为t_order.id
     */
    private Integer id;

    /**
     *  订单编号,所属表字段为t_order.number
     */
    private Long number;

    /**
     *  支付金额,所属表字段为t_order.pay_amount
     */
    private Integer pay_amount;

    /**
     *  1-待审核
2-待支付
3-支付中
4-支付失败
5-已支付,处理中
6-已支付,交易成功
7-已取消
,所属表字段为t_order.status
     */
    private Integer status;

    /**
     *  下单企业,所属表字段为t_order.buy_ep_id
     */
    private Integer buy_ep_id;

    /**
     *  下单企业名称,所属表字段为t_order.buy_ep_name
     */
    private String buy_ep_name;

    /**
     *  销售员id 0表示接口下单,所属表字段为t_order.buy_operator_id
     */
    private Integer buy_operator_id;

    /**
     *  销售员姓名,所属表字段为t_order.buy_operator_name
     */
    private String buy_operator_name;

    /**
     *  收款方ID,所属表字段为t_order.payee_ep_id
     */
    private Integer payee_ep_id;

    /**
     *  创建时间,所属表字段为t_order.create_time
     */
    private Date create_time;

    /**
     *  1-余额
2-支付宝扫码
3-微信扫码
4-银联NOCARD,所属表字段为t_order.payment_type
     */
    private Integer payment_type;

    /**
     *  支付操作交易流水,所属表字段为t_order.local_payment_serial_no
     */
    private String local_payment_serial_no;

    /**
     *  第三方交易号,所属表字段为t_order.third_serial_no
     */
    private String third_serial_no;

    /**
     *  支付时间,所属表字段为t_order.pay_time
     */
    private Date pay_time;

    /**
     *  销售金额,所属表字段为t_order.sale_amount
     */
    private Integer sale_amount;

    /**
     *  来源 0-平台下单 1-接口下单,所属表字段为t_order.from_type
     */
    private Integer from_type;

    /**
     *  第三方订单号,所属表字段为t_order.outer_id
     */
    private String outer_id;

    /**
     *  备注,所属表字段为t_order.remark
     */
    private String remark;

    /**
     *  ,所属表字段为t_order.update_time
     */
    private String update_time;

    /**
     *  全部审核通过时间,所属表字段为t_order.audit_time
     */
    private Date audit_time;

    /**
     *  下单来源 3010 B2C  3011  网站,所属表字段为t_order.source
     */
    private Integer source;

    /**
     *  支付企业ID,所属表字段为t_order.payment_ep_id
     */
    private Integer payment_ep_id;

    /**
     *  支付企业名称,所属表字段为t_order.payment_ep_name
     */
    private String payment_ep_name;

    /**
     *  支付操作人ID,所属表字段为t_order.payment_operator_id
     */
    private Integer payment_operator_id;

    /**
     *  支付操作人名称,所属表字段为t_order.payment_operator_name
     */
    private String payment_operator_name;

    /**
     * 序列化ID,t_order
     */
    private static final long serialVersionUID = 1L;
}