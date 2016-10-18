package com.all580.payment.thirdpay.wx.model;

/**
 * 统一下单请求对象
 *
 * @author panyi on 2016/10/13.
 * @since V0.0.1
 */
public class UnifiedOrderReq extends CommonsReq {
    //    设备号	device_info	否	String(32)	1.3467E+13	微信支付分配的终端设备号，商户自定义
    private String device_info;
    //    签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
    private String sign;
    //    商品描述	body	是	String(32)	Ipad mini  16G  白色	商品或支付单简要描述
    private String body;
    //    商品详情	detail	否	String(8192)	Ipad mini  16G  白色	商品名称明细列表
    private String detail;
    //    附加数据	attach	否	String(127)	说明	附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
    private String attach;
    //    商户订单号	out_trade_no	是	String(32)	1.21775E+27	商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
    private String out_trade_no;
    //    货币类型	fee_type	否	String(16)	CNY	符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
    private String fee_type;
    //    总金额	total_fee	是	Int	888	订单总金额，只能为整数，详见支付金额
    private String total_fee;
    //    终端IP	spbill_create_ip	是	String(16)	8.8.8.8	APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
    private String spbill_create_ip;
    //    交易起始时间	time_start	否	String(14)	2.00912E+13	订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
    private String time_start;
    //    交易结束时间	time_expire	否	String(14)	2.00912E+13	订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则
    private String time_expire;
    //    商品标记	goods_tag	否	String(32)	WXG	商品标记，代金券或立减优惠功能的参数，说明详见代金券或立减优惠
    private String goods_tag;
    //    通知地址	notify_url	是	String(256)	http://www.baidu.com/	接收微信支付异步通知回调地址
    private String notify_url;
    //    交易类型	trade_type	是	String(16)	JSAPI	取值如下：JSAPI，NATIVE，APP，详细说明见参数规定
    private String trade_type;
    //    商品ID	product_id	否	String(32)	1.22354E+22	trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。
    private String product_id;
    //    用户标识	openid	否	String(128)	oUpF8uMuAJO_M2pxb1Q9zNjWeS6o	trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。下单前需要调用【网页授权获取用户信息】接口获取到用户的Openid。
    private String openid;


    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(String time_expire) {
        this.time_expire = time_expire;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public void setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return "UnifiedOrderReq [" +
                "appid='" + appid + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", device_info='" + device_info + '\'' +
                ", nonce_str='" + nonce_str + '\'' +
                ", sign='" + sign + '\'' +
                ", body='" + body + '\'' +
                ", detail='" + detail + '\'' +
                ", attach='" + attach + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", fee_type='" + fee_type + '\'' +
                ", total_fee='" + total_fee + '\'' +
                ", spbill_create_ip='" + spbill_create_ip + '\'' +
                ", time_start='" + time_start + '\'' +
                ", time_expire='" + time_expire + '\'' +
                ", goods_tag='" + goods_tag + '\'' +
                ", notify_url='" + notify_url + '\'' +
                ", trade_type='" + trade_type + '\'' +
                ", product_id='" + product_id + '\'' +
                ", openid='" + openid + '\'' +
                ']';
    }
}
