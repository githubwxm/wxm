package com.all580.payment.thirdpay.wx.model;

/**
 * @包名称：com.hentre.all580.pay.model
 * @创建人：YQ
 * @创建时间：2015/3/16 15:28
 */
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

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public Integer getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(Integer refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getRefund_fee_type() {
        return refund_fee_type;
    }

    public void setRefund_fee_type(String refund_fee_type) {
        this.refund_fee_type = refund_fee_type;
    }

    public Integer getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(Integer total_fee) {
        this.total_fee = total_fee;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public Integer getCash_fee() {
        return cash_fee;
    }

    public void setCash_fee(Integer cash_fee) {
        this.cash_fee = cash_fee;
    }

    public String getCash_fee_type() {
        return cash_fee_type;
    }

    public void setCash_fee_type(String cash_fee_type) {
        this.cash_fee_type = cash_fee_type;
    }

    public Integer getCash_refund_fee() {
        return cash_refund_fee;
    }

    public void setCash_refund_fee(Integer cash_refund_fee) {
        this.cash_refund_fee = cash_refund_fee;
    }

    public String getCash_refund_fee_type() {
        return cash_refund_fee_type;
    }

    public void setCash_refund_fee_type(String cash_refund_fee_type) {
        this.cash_refund_fee_type = cash_refund_fee_type;
    }

    public Integer getCoupon_refund_fee() {
        return coupon_refund_fee;
    }

    public void setCoupon_refund_fee(Integer coupon_refund_fee) {
        this.coupon_refund_fee = coupon_refund_fee;
    }

    public Integer getCoupon_count() {
        return coupon_count;
    }

    public void setCoupon_count(Integer coupon_count) {
        this.coupon_count = coupon_count;
    }

    public String getCoupon_batch_id_$n() {
        return coupon_batch_id_$n;
    }

    public void setCoupon_batch_id_$n(String coupon_batch_id_$n) {
        this.coupon_batch_id_$n = coupon_batch_id_$n;
    }

    public String getCoupon_id_$n() {
        return coupon_id_$n;
    }

    public void setCoupon_id_$n(String coupon_id_$n) {
        this.coupon_id_$n = coupon_id_$n;
    }

    public Integer getCoupon_fee_$n() {
        return coupon_fee_$n;
    }

    public void setCoupon_fee_$n(Integer coupon_fee_$n) {
        this.coupon_fee_$n = coupon_fee_$n;
    }

    @Override
    public String toString() {
        return "RefundRsp [" +
                "return_code='" + return_code + '\'' +
                ", return_msg='" + return_msg + '\'' +
                ", result_code='" + result_code + '\'' +
                ", err_code='" + err_code + '\'' +
                ", err_code_des='" + err_code_des + '\'' +
                ", appid='" + appid + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", device_info='" + device_info + '\'' +
                ", nonce_str='" + nonce_str + '\'' +
                ", sign='" + sign + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", out_refund_no='" + out_refund_no + '\'' +
                ", refund_id='" + refund_id + '\'' +
                ", refund_fee=" + refund_fee +
                ", refund_fee_type='" + refund_fee_type + '\'' +
                ", total_fee=" + total_fee +
                ", fee_type='" + fee_type + '\'' +
                ", cash_fee=" + cash_fee +
                ", cash_fee_type='" + cash_fee_type + '\'' +
                ", cash_refund_fee=" + cash_refund_fee +
                ", cash_refund_fee_type='" + cash_refund_fee_type + '\'' +
                ", coupon_refund_fee=" + coupon_refund_fee +
                ", coupon_count=" + coupon_count +
                ", coupon_batch_id_$n='" + coupon_batch_id_$n + '\'' +
                ", coupon_id_$n='" + coupon_id_$n + '\'' +
                ", coupon_fee_$n=" + coupon_fee_$n +
                ']';
    }
}
