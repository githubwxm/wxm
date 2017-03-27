package com.all580.payment.thirdpay.wx.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author panyi on 2016/10/13.
 * @since V0.0.1
 */
@Data
public class UnifiedOrderRsp extends CommonsRsp implements Serializable {

    //    公众账号ID	appid	是	String(32)	wx8888888888888888	调用接口提交的公众账号ID
    private String appid;
    //    商户号	mch_id	是	String(32)	1900000109	调用接口提交的商户号
    private String mch_id;
    //    设备号	device_info	否	String(32)	1.3467E+13	调用接口提交的终端设备号，
    private String device_info;
    //    随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	微信返回的随机字符串
    private String nonce_str;
    //    签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	微信返回的签名，详见签名算法
    private String sign;
    //    业务结果	result_code	是	String(16)	SUCCESS	SUCCESS/FAIL
    private String result_code;
    //    错误代码	err_code	否	String(32)	SYSTEMERROR	详细参见第6节错误列表
    private String err_code;
    //    错误代码描述	err_code_des	否	String(128)	系统错误	错误返回的信息描述
    private String err_code_des;

    private String transaction_id;
    //    交易类型	trade_type	是	String(16)	JSAPI	调用接口提交的交易类型，取值如下：JSAPI，NATIVE，APP，详细说明见参数规定
    private String trade_type;
    //    预支付交易会话标识	prepay_id	是	String(64)	wx201410272009395522657a690389285100	微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
    private String prepay_id;
    //
//    二维码链接	code_url	否	String(64)	URl：	trade_type为NATIVE是有返回，可将该参数值生成二维码展示出来进行扫码支付
//    weixin://wxpay/s/An4baqw
    private String code_url;

    //    交易状态	trade_state	是	String(32)	SUCCESS	SUCCESS—支付成功
//    REFUND—转入退款
//    NOTPAY—未支付
//    CLOSED—已关闭
//    REVOKED—已撤销
//    USERPAYING--用户支付中
//    PAYERROR--支付失败(其他原因，如银行返回失败)
    private String trade_state;

    private String key;



}
