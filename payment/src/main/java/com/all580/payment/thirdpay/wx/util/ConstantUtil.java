package com.all580.payment.thirdpay.wx.util;

/**
 * @author panyi on 2016/10/13.
 * @since V0.0.1
 */
public class ConstantUtil {
    // 初始化
    public static String APP_ID = "wx8de3fa23129fcb3f";// 微信开发平台应用id
    public static String APP_SECRET = "3d3dc293f1d6308d0ff13915526e3a55";// 应用对应的凭证
    public static String PARTNER = "1228533602";
    public static String PARTNER_KEY = "ECall58020150318WeiXinPayServers";// 商户号对应的密钥
    // 应用对应的密钥
    public static String APP_KEY = "sLWUCGyfSd1uKEVEsyrZZUJevenFseBN6NacIXmJxoyoPMHAV8wvMns6RxFhTrDM66FdeFNVJxRfzIvVtnXUSWMIiN910G3NY6OmtPsFBdoZn26FmZQgMTzlajqwokie";
    public static String TOKENURL = "https://api.weixin.qq.com/cgi-bin/token";// 获取access_token对应的url
    public static String GRANT_TYPE = "client_credential";// 常量固定值
    public static String EXPIRE_ERRCODE = "42001";// access_token失效后请求返回的errcode
    public static String FAIL_ERRCODE = "40001";// 重复获取导致上一次获取的access_token失效,返回错误码
    public static String GATEURL = "https://api.weixin.qq.com/pay/genprepay?access_token=";// 获取预支付id的接口url
    public static String ACCESS_TOKEN = "access_token";// access_token常量值
    public static String ERRORCODE = "errcode";// 用来判断access_token是否失效的值
    public static String SIGN_METHOD = "sha1";// 签名算法常量值
    public static String WX_NOTIFY_URL = "http://www.yoomid.com/moneymoney/wxpay/payNotify";
    //packageValue常量值
    public static String packageValue = "bank_type=WX&body=%B2%E2%CA%D4&fee_type=1&input_charset=GBK&notify_url=http%3A%2F%2F127.0.0.1%3A8180%2Ftenpay_api_b2c%2FpayNotifyUrl.jsp&out_trade_no=2051571832&partner=1900000109&sign=10DA99BCB3F63EF23E4981B331B0A3EF&spbill_create_ip=127.0.0.1&time_expire=20131222091010&total_fee=1";
    public static String testUser = "test001";
    public static String NATIVE_TRADE_TYPE = "NATIVE";
    public static String JSAPI_TRADE_TYPE = "JSAPI";

    public static String RETURN_CODE = "return_code";
    public static String RESULT_CODE = "result_code";
    public static String RETURN_MSG = "return_msg";
    public static String ERR_CODE = "err_code";
    public static String ERR_CODE_DES = "err_code_des";
    public static String SUCCESS = "SUCCESS";
    public static String FAIL = "FAIL";

    // 认证第一步：用户同意授权，获取code
    public static String ACCESS_TOKEN_FIRST = "https://open.weixin.qq.com/connect/oauth2/authorize";
    //    第二步：通过code换取网页授权access_token
    public static String ACCESS_TOKEN_SECOND = "https://api.weixin.qq.com/sns/oauth2/access_token";
    //    第三步：刷新access_token（如果需要）
    public static String ACCESS_TOKEN_THIRD = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    //    第四步：拉取用户信息(需scope为 snsapi_userinfo)
    public static String ACCESS_TOKEN_FOURTH = "https://api.weixin.qq.com/sns/userinfo";
    //    附：检验授权凭证（access_token）是否有效
    public static String ACCESS_TOKEN_FIFTH = "https://api.weixin.qq.com/sns/auth";

    // 统一下单
    public static String UNIFIEDORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    // 查询订单
    public static String ORDERQUERY = "https://api.mch.weixin.qq.com/pay/orderquery";
    // 关闭订单
    public static String CLOSEORDER = "https://api.mch.weixin.qq.com/pay/closeorder";
    // 申请退款
    public static String REFUND = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    // 查询退款
    public static String REFUNDQUERY = "https://api.mch.weixin.qq.com/pay/refundquery";
    // 下载对账单
    public static String DOWNLOADBILL = "https://api.mch.weixin.qq.com/pay/downloadbill";
    // 转换短链接
    public static String SHORTURL = "https://api.mch.weixin.qq.com/tools/shorturl";
    // 测速上报
    public static String REPORT = "https://api.mch.weixin.qq.com/payitil/report";

    // 微信错误码解析
//    public static String NOAUTH = "商户无此接口权限";//	商户未开通此接口权限	请商户前往申请此接口权限
//    public static String NOTENOUGH = "余额不足";//	用户帐号余额不足	用户帐号余额不足，请用户充值或更换支付卡后再支付
//    public static String ORDERPAID = "商户订单已支付";//	商户订单已支付，无需重复操作	商户订单已支付，无需更多操作
//    public static String ORDERCLOSED = "订单已关闭";//	当前订单已关闭，无法支付	当前订单已关闭，请重新下单
//    public static String SYSTEMERROR = "系统错误";//	系统超时	系统异常，请用相同参数重新调用
//    public static String APPID_NOT_EXIST = "APPID不存在";//	参数中缺少APPID	请检查APPID是否正确
//    public static String MCHID_NOT_EXIST = "MCHID不存在";//	参数中缺少MCHID	请检查MCHID是否正确
//    public static String APPID_MCHID_NOT_MATCH = "appid和mch_id不匹配";//	请确认appid和mch_id是否匹配
//    public static String LACK_PARAMS = "缺少参数";//	缺少必要的请求参数	请检查参数是否齐全
//    public static String OUT_TRADE_NO_USED = "商户订单号重复";//	同一笔交易不能多次提交	请核实商户订单号是否重复提交
//    public static String SIGNERROR = "签名错误";//	参数签名结果不正确	请检查签名参数和方法是否都符合签名算法要求
//    public static String XML_FORMAT_ERROR = "XML格式错误	XML格式错误";//	请检查XML参数格式是否正确
//    public static String REQUIRE_POST_METHOD = "请使用post方法";//	未使用post传递参数 	请检查请求参数是否通过post方法提交
//    public static String POST_DATA_EMPTY = "post数据为空";//	post数据不能为空	请检查post数据是否为空
//    public static String NOT_UTF8 = "编码格式错误";//	未使用指定编码格式	请使用NOT_UTF8编码格式
//    public static String ORDERNOTEXIST = "此交易订单号不存在";//	查询系统中不存在此交易订单号	该API只能查提交支付交易返回成功的订单，请商户检查需要查询的订单号是否正确
//    public static String INVALID_TRANSACTIONID = "无效transaction_id	请求参数未按指引进行填写";//	参数错误，请重新检查
//    public static String PARAM_ERROR = "参数错误";//	请求参数未按指引进行填写	参数错误，请重新检查
}
