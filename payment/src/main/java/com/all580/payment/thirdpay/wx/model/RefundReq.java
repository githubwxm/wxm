package com.all580.payment.thirdpay.wx.model;

import lombok.Data;

/**
 * @包名称：com.hentre.all580.pay.model
 * @创建人：YQ
 * @创建时间：2015/3/16 15:28
 */
@Data
public class RefundReq extends CommonsReq {
    //    设备号	device_info	否	String(32)	1.3467E+13	微信支付分配的终端设备号，与下单一致
    private String device_info;
    //    签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
    private String sign;
    //    微信订单号	transaction_id	是	String(28)	1.21775E+27	微信订单号
    private String transaction_id;
    //    商户订单号	out_trade_no	是	String(32)	1.21775E+27	商户系统内部的订单号,
    private String out_trade_no;
    //    transaction_id、out_trade_no二选一，如果同时存在优先级：transaction_id> out_trade_no
//    商户退款单号	out_refund_no	是	String(32)	1.21775E+27	商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
    private String out_refund_no;
    //    总金额	total_fee	是	Int	100	订单总金额，单位为分，只能为整数，详见支付金额
    private Integer total_fee;
    //    退款金额	refund_fee	是	Int	100	退款总金额，订单总金额，单位为分，只能为整数，详见支付金额
    private Integer refund_fee;
    //    货币种类	refund_fee_type	否	String(8)	CNY	货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
    private String refund_fee_type;
    //    操作员	op_user_id	是	String(32)	1900000109	操作员帐号, 默认为商户号
    private String op_user_id;

















}
