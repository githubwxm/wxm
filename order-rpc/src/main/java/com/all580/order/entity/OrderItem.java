package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderItem implements Serializable {
    /**
     *  ,所属表字段为t_order_item.id
     */
    private Integer id;

    /**
     *  ,所属表字段为t_order_item.number
     */
    private Long number;

    /**
     *  主订单id,所属表字段为t_order_item.order_id
     */
    private Integer order_id;

    /**
     *  子产品CODE,所属表字段为t_order_item.pro_sub_number
     */
    private Long pro_sub_number;

    /**
     *  子产品id,所属表字段为t_order_item.pro_sub_id
     */
    private Integer pro_sub_id;

    /**
     *  主产品名,所属表字段为t_order_item.pro_name
     */
    private String pro_name;

    /**
     *  子产品名,所属表字段为t_order_item.pro_sub_name
     */
    private String pro_sub_name;

    /**
     *  产品类型,所属表字段为t_order_item.pro_type
     */
    private Integer pro_type;

    /**
     *  子产品票种类型:5031 散客 5032 团队,所属表字段为t_order_item.pro_sub_ticket_type
     */
    private Integer pro_sub_ticket_type;

    /**
     *  票务产品ID,所属表字段为t_order_item.ma_product_id
     */
    private String ma_product_id;

    /**
     *  开始时间,所属表字段为t_order_item.start
     */
    private Date start;

    /**
     *  结束时间,所属表字段为t_order_item.end
     */
    private Date end;

    /**
     *  天数,所属表字段为t_order_item.days
     */
    private Integer days;

    /**
     *  数量,所属表字段为t_order_item.quantity
     */
    private Integer quantity;

    /**
     *  已用总张数,所属表字段为t_order_item.used_quantity
     */
    private Integer used_quantity;

    /**
     *  已退订总张数,所属表字段为t_order_item.refund_quantity
     */
    private Integer refund_quantity;

    /**
     *  1-待审核
2-审核通过
3-未出票
4-出票失败
5-出票中
6-已出票
7-已取消,所属表字段为t_order_item.status
     */
    private Integer status;

    /**
     *  出票调用记录id,所属表字段为t_order_item.send_ma_log_rec_id
     */
    private Integer send_ma_log_rec_id;

    /**
     *  出票时间,所属表字段为t_order_item.send_ma_time
     */
    private Date send_ma_time;

    /**
     *  团队id 0表示散客,所属表字段为t_order_item.group_id
     */
    private Integer group_id;

    /**
     *  1-预付
2-平台代收预付
3-到付,所属表字段为t_order_item.payment_flag
     */
    private Integer payment_flag;

    /**
     *  销售金额,所属表字段为t_order_item.sale_amount
     */
    private Integer sale_amount;

    /**
     *  票据类型,所属表字段为t_order_item.ticket_type
     */
    private Integer ticket_type;

    /**
     *  最底层供应商ID,所属表字段为t_order_item.supplier_ep_id
     */
    private Integer supplier_ep_id;

    /**
     *  供应商平台商ID,所属表字段为t_order_item.supplier_core_ep_id
     */
    private Integer supplier_core_ep_id;

    /**
     *  供应商短信通知手机号码,所属表字段为t_order_item.supplier_phone
     */
    private String supplier_phone;

    /**
     *  最低使用张数限制（团队票）,所属表字段为t_order_item.low_quantity
     */
    private Integer low_quantity;

    /**
     *  凭证商户关联ID t_ma.id,所属表字段为t_order_item.ep_ma_id
     */
    private Integer ep_ma_id;

    /**
     *  审核用户ID,所属表字段为t_order_item.audit_user_id
     */
    private Integer audit_user_id;

    /**
     *  审核用户名称,所属表字段为t_order_item.audit_user_name
     */
    private String audit_user_name;

    /**
     *  ,所属表字段为t_order_item.update_time
     */
    private String update_time;

    /**
     *  审核通过时间,所属表字段为t_order_item.audit_time
     */
    private Date audit_time;

    /**
     *  审核结果,所属表字段为t_order_item.audit
     */
    private Boolean audit;

    /**
     *  ,所属表字段为t_order_item.memo
     */
    private String memo;

    /**
     *  重发最大次数,所属表字段为t_order_item.resend_max_times
     */
    private Integer resend_max_times;

    /**
     *  重发次数,所属表字段为t_order_item.resend_times
     */
    private Integer resend_times;

    /**
     *  重发间隔(秒),所属表字段为t_order_item.resend_interval
     */
    private Integer resend_interval;

    /**
     *  最后重新出票时间,所属表字段为t_order_item.last_resend_time
     */
    private Date last_resend_time;

    /**
     *  凭证内容,所属表字段为t_order_item.voucher_msg
     */
    private String voucher_msg;

    /**
     *  凭证短信模板,所属表字段为t_order_item.voucher_template
     */
    private Integer voucher_template;

    /**
     *  小票内容,所属表字段为t_order_item.ticket_msg
     */
    private String ticket_msg;

    /**
     *  是否发送短信,所属表字段为t_order_item.send
     */
    private Boolean send;

    /**
     * 序列化ID,t_order_item
     */
    private static final long serialVersionUID = 1L;

}