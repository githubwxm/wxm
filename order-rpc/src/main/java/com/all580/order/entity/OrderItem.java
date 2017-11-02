package com.all580.order.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
@Data
public class OrderItem implements Serializable {
    private Integer id;

    private Long number;

    /**
     * 主订单id
     */
    private Integer order_id;

    /**
     * 子产品CODE
     */
    private Long pro_sub_number;

    /**
     * 子产品id
     */
    private Integer pro_sub_id;

    /**
     * 主产品名
     */
    private String pro_name;

    /**
     * 子产品名
     */
    private String pro_sub_name;

    /**
     * 产品类型
     */
    private Integer pro_type;

    /**
     * 子产品票种类型:5031 散客 5032 团队
     */
    private Integer pro_sub_ticket_type;

    /**
     * 票务产品ID
     */
    private String ma_product_id;

    /**
     * 开始时间
     */
    private Date start;

    /**
     * 结束时间
     */
    private Date end;

    /**
     * 天数
     */
    private Integer days;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 已用总张数
     */
    private Integer used_quantity;

    /**
     * 已退订总张数
     */
    private Integer refund_quantity;

    /**
     * 1-待审核
2-审核通过
3-未出票
4-出票失败
5-出票中
6-已出票
7-已取消
     */
    private Integer status;

    /**
     * 出票调用记录id
     */
    private Integer send_ma_log_rec_id;

    /**
     * 出票时间
     */
    private Date send_ma_time;

    /**
     * 团队id 0表示散客
     */
    private Integer group_id;

    /**
     * 1-预付
2-平台代收预付
3-到付
     */
    private Integer payment_flag;

    /**
     * 销售金额
     */
    private Integer sale_amount;

    /**
     * 票据类型
     */
    private Integer ticket_type;

    /**
     * 最底层供应商ID
     */
    private Integer supplier_ep_id;

    /**
     * 供应商平台商ID
     */
    private Integer supplier_core_ep_id;

    /**
     * 供应商短信通知手机号码
     */
    private String supplier_phone;

    /**
     * 最低使用张数限制（团队票）
     */
    private Integer low_quantity;

    /**
     * 凭证商户关联ID t_ma.id
     */
    private Integer ep_ma_id;

    /**
     * 审核用户ID
     */
    private Integer audit_user_id;

    /**
     * 审核用户名称
     */
    private String audit_user_name;

    private Date update_time;

    /**
     * 审核通过时间
     */
    private Date audit_time;

    /**
     * 审核结果
     */
    private Boolean audit;

    private String memo;

    /**
     * 重发最大次数
     */
    private Integer resend_max_times;

    /**
     * 重发次数
     */
    private Integer resend_times;

    /**
     * 重发间隔(秒)
     */
    private Integer resend_interval;

    /**
     * 最后重新出票时间
     */
    private Date last_resend_time;

    /**
     * 凭证内容
     */
    private String voucher_msg;

    /**
     * 凭证短信模板
     */
    private Integer voucher_template;

    /**
     * 小票内容
     */
    private String ticket_msg;

    /**
     * 是否发送短信
     */
    private Boolean send;

    /**
     * 最小生效日期
     */
    private Date min_effective_date;

    /**
     * 最大失效日期
     */
    private Date max_expiry_date;

    /**
     * 最后一个退订ID
     */
    private Integer last_refund_id;

    /**
     * 预退订数量(不包含已退)
     */
    private Integer pre_refund_quantity;

    /**
     * 团队修改状态:3280-修改中;3281-已修改;3282-修改失败
     */
    private Integer modify_status;

    private static final long serialVersionUID = 1L;

}