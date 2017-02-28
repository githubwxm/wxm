package com.all580.payment.thirdpay.wx.model;

import lombok.Data;

/**
 * @包名称：com.hentre.all580.pay.model
 * @创建人：YQ
 * @创建时间：2015/3/16 15:28
 */
@Data
public class RefundRsp extends CommonsRsp {


    //    业务结果	result_code	是	String(16)	SUCCESS	SUCCESS/FAIL
//    SUCCESS退款申请接收成功，结果通过退款查询接口查询
//    FAIL 提交业务失败
    private String result_code;
    //    错误代码	err_code	否	String(32)	SYSTEMERROR	列表详见第6节
    private String err_code;
    //    错误代码描述	err_code_des	否	String(128)	系统超时	结果信息描述
    private String err_code_des;
    //    公众账号ID	appid	是	String(32)	wx8888888888888888	微信分配的公众账号ID
    private String appid;
    //    商户号	mch_id	是	String(32)	1900000109	微信支付分配的商户号
    private String mch_id;
    //    设备号	device_info	否	String(32)	1.3467E+13	微信支付分配的终端设备号，与下单一致
    private String device_info;
    //    随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位
    private String nonce_str;
    //    签名	sign	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	签名，详见签名算法
    private String sign;
    //    微信订单号	transaction_id	是	String(28)	1.21775E+27	微信订单号
    private String transaction_id;
    //    商户订单号	out_trade_no	是	String(32)	1.21775E+27	商户系统内部的订单号
    private String out_trade_no;
    //    商户退款单号	out_refund_no	是	String(32)	1.21775E+27	商户退款单号
    private String out_refund_no;
    //    微信退款单号	refund_id	是	String(28)	1.21775E+27	微信退款单号
    private String refund_id;
    //    退款金额	refund_fee	是	Int	100	退款总金额,单位为分,可以做部分退款
    private Integer refund_fee;
    //    退款货币种类	refund_fee_type	否	String(8)	CNY	退款货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
    private String refund_fee_type;
    //    订单总金额	total_fee	是	Int	100	订单总金额，单位为分，只能为整数，详见支付金额
    private Integer total_fee;
    //    订单金额货币种类	fee_type	否	String(8)	CNY	订单金额货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
    private String fee_type;
    //    现金支付金额	cash_fee	是	Int	100	现金支付金额，单位为分，只能为整数，详见支付金额
    private Integer cash_fee;
    //    货币种类	cash_fee_type	否	String(8)	CNY	货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
    private String cash_fee_type;
    //    现金退款金额	cash_refund_fee	否	Int	100	现金退款金额，单位为分，只能为整数，详见支付金额
    private Integer cash_refund_fee;
    //    现金退款货币类型	cash_refund_fee_type	否	String(8)	CNY	货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
    private String cash_refund_fee_type;
    //    代金券或立减优惠退款金额	coupon_refund_fee	否	Int	100	代金券或立减优惠退款金额=订单金额-现金退款金额，注意：立减优惠金额不会退回
    private Integer coupon_refund_fee;
    //    代金券或立减优惠使用数量	coupon_count	否	Int	1	代金券或立减优惠使用数量
    private Integer coupon_count;
    //    代金券或立减优惠批次ID	coupon_batch_id_$n	否	String(20)	100	代金券或立减优惠批次ID ,$n为下标，从1开始编号
    private String coupon_batch_id_$n;
    //    代金券或立减优惠ID	coupon_id_$n	否	String(20)	10000 	代金券或立减优惠ID, $n为下标，从1开始编号
    private String coupon_id_$n;
    //    单个代金券或立减优惠支付金额	coupon_fee_$n	否	Int	100	单个代金券或立减优惠支付金额, $n为下标，从1开始编号
    private Integer coupon_fee_$n;

    /*退款状态：
SUCCES—退款成功
FAIL—退款失败
PROCESSING—退款处理中
NOTSURE—未确定，需要商户
原退款单号重新发起
CHANGE—转入代发，退款到
银行发现用户的卡作废或者
冻结了，导致原路退款银行
卡失败，资金回流到商户的
现金帐号，需要商户人工干
预，通过线下或者财付通转
账的方式进行退款。
* */
    private String refund_status_0;


}
