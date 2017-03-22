package com.all580.payment.thirdpay.ali.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.all580.payment.thirdpay.ali.config.AlipayConfig;
import com.all580.payment.thirdpay.ali.util.AlipayCore;
import com.all580.payment.thirdpay.ali.util.AlipayNotify;
import com.all580.payment.thirdpay.ali.util.AlipaySubmit;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付服务
 *
 * @author panyi on 2016/10/15.
 * @since V0.0.1
 */
@Service("aliPayService")
public class AliPayService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<Integer, AliPayProperties> alipayPropertiesMap = new HashMap<>();
    @Value("${base.url}")
    private String domain;
    @Value("${alipay.payment.callback.url}")
    private String pay_notify_url;//支付宝服务器主动通知商户网站里指定的页面http路径
    @Value("${alipay.recharge.callback.url}")
    private String recharge_notify_url;//支付宝服务器主动通知商户网站里指定的页面http路径
    @Value("${alipay.refund.callback.url}")
    private String refund_notify_url;//支付宝服务器主动通知商户网站里指定的页面http路径
    @Value("${alipay.phonePayment.callback.url}")
    private String phonepay_notify_url;//支付宝服务器主动通知商户网站里指定的页面http路径

    public static final String HEAD = "<head><meta charset=\"UTF-8\"><meta content=\"text/html\"></head>";
    public static final String API_URL = "https://openapi.alipay.com/gateway.do";

    public boolean isPropertiesInit(int coreEpId) {
        return alipayPropertiesMap.containsKey(coreEpId);
    }

    public synchronized void initProperties(int coreEpId, String confData) {
        AliPayProperties aliPayProperties = JsonUtils.fromJson(confData, AliPayProperties.class);
        alipayPropertiesMap.put(coreEpId, aliPayProperties);
    }

    public Map<String, String> paymentQuery(long ordCode, int coreEpId,String trade_no) {
        AliPayProperties aliPayProperties = alipayPropertiesMap.get(coreEpId);
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "single_trade_query");
        sParaTemp.put("partner", aliPayProperties.getPartner());
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("trade_no", trade_no);
        sParaTemp.put("out_trade_no", String.valueOf(ordCode));

        // 建立请求
        Map<String, String> rsp = new HashMap<>();
        try {
            String res = AlipaySubmit.buildRequest("", "", sParaTemp, aliPayProperties.getKey());
            Document doc = DocumentHelper.parseText(res);
            Element rootElement = doc.getRootElement();
            String isSuccess = rootElement.selectSingleNode("//alipay/is_success").getText();
            rsp.put("result", res);
            rsp.put("is_success", isSuccess);
            if ("T".equals(isSuccess)) {
                rsp.put("out_trade_no", rootElement.selectSingleNode("//alipay/response/trade/out_trade_no").getText());
                rsp.put("trade_no", rootElement.selectSingleNode("//alipay/response/trade/trade_no").getText());
                rsp.put("trade_status", rootElement.selectSingleNode("//alipay/response/trade/trade_status").getText());
                rsp.put("sign", rootElement.selectSingleNode("//alipay/sign").getText());
            } else {
                rsp.put("sign", rootElement.selectSingleNode("//alipay/error").getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return rsp;
    }

    public String reqPay(long ordCode, int coreEpId, Map<String, Object> params) {
        AliPayProperties aliPayProperties = alipayPropertiesMap.get(coreEpId);
        String notify_url = domain + pay_notify_url;
        // "http://core.py.ngrok.wendal.cn/no_auth_api/callback/ali/payment";

        // 把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<>();
        sParaTemp.put("service", AlipayConfig.INF_NAME_PAY);
        sParaTemp.put("partner", aliPayProperties.getPartner());
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("payment_type", AlipayConfig.PayType.PROD_PURCHASE.toString());
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("return_url", CommonUtil.objectParseString(params.get("return_url")));
        sParaTemp.put("seller_email", "admin@all580.com");
        sParaTemp.put("out_trade_no", String.valueOf(ordCode));
        sParaTemp.put("subject", String.valueOf(params.get("prodName")));
        int totalFee = (Integer) params.get("totalFee");
        sParaTemp.put("total_fee", String.valueOf(totalFee / 100.0));
        // sParaTemp.put("body", null);
        // sParaTemp.put("paymethod", "");
        // sParaTemp.put("defaultbank", "");
        // sParaTemp.put("show_url", null);
        // sParaTemp.put("anti_phishing_key", "");
        // sParaTemp.put("exter_invoke_ip", "");
        sParaTemp.put("extra_common_param", "" + coreEpId);

        // 建立请求
        String sHtmlText = AlipaySubmit.buildRequest(aliPayProperties.getKey(), sParaTemp, "get", "确认");

        return HEAD + sHtmlText;
    }

    public String reqRefund(Map<String, Object> params, int coreEpId) {
        AliPayProperties aliPayProperties = alipayPropertiesMap.get(coreEpId);
        String notify_url = domain + refund_notify_url;
        // "http://core.py.ngrok.wendal.cn/no_auth_api/callback/ali/refund";
        // 把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", AlipayConfig.INF_NAME_REFUND);
        sParaTemp.put("partner", aliPayProperties.getPartner());
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("seller_email", aliPayProperties.getSeller_email());
        sParaTemp.put("refund_date", DateFormatUtils.parseDateToDatetimeString(new Date()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sParaTemp.put("batch_no", sdf.format(new Date()) + params.get("serialNum"));
        sParaTemp.put("batch_num", "1");
        int fee = (Integer) params.get("refundFee");
        String detail = params.get("outTransId") + "^" + String.valueOf(fee / 100.0) + "^" + "协议退款";
        sParaTemp.put("detail_data", detail);

        // 建立请求
        String sHtmlText = AlipaySubmit.buildRequest(aliPayProperties.getKey(), sParaTemp, "get", "确认");
        return HEAD + sHtmlText;
    }

    public boolean refundCallback(Map<String, String> params, int coreEpId) {
        AliPayProperties aliPayProperties = alipayPropertiesMap.get(coreEpId);
        // 访问支付宝进行校验
        return AlipayNotify.verify(params, aliPayProperties.getPartner(), aliPayProperties.getKey());//验证成功

    }

    /**
     * 支付回调
     *
     * @param params
     * @param coreEpId
     * @return
     */
    public Result<Map<String, String>> payCallback(Map<String, String> params, int coreEpId) {
        Result<Map<String, String>> result = new Result<>();
        AliPayProperties aliPayProperties = alipayPropertiesMap.get(coreEpId);
        boolean isSuccess = AlipayNotify.verify(params, aliPayProperties.getPartner(), aliPayProperties.getKey());
        if (isSuccess) {
            result.setSuccess();
        } else {
            String linkString = AlipayCore.createLinkString(params);
            logger.error("支付宝退款失败，信息：" + linkString);
            result.setFail();
            result.setError("支付宝退款失败，信息：" + linkString);
        }
        return result;//验证成功

    }

    public String wapPay(long ordCode, int coreEpId, Map<String, Object> params) throws AlipayApiException {
        AliPayProperties aliPayProperties = alipayPropertiesMap.get(coreEpId);
        if (aliPayProperties.getAlipayClient() == null) {
            AlipayClient alipayClient = new DefaultAlipayClient(API_URL, aliPayProperties.getApp_id(), aliPayProperties.getPrivate_key(), "json", AlipayConfig.input_charset, aliPayProperties.getAlipay_public_key(), "RSA2"); //获得初始化的AlipayClient
            aliPayProperties.setAlipayClient(alipayClient);
        }
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(CommonUtil.objectParseString(params.get("return_url")));
        alipayRequest.setNotifyUrl(domain + pay_notify_url);//在公共参数中设置回跳和通知地址
        // 把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<>();
        sParaTemp.put("product_code", "QUICK_WAP_PAY");
        sParaTemp.put("out_trade_no", String.valueOf(ordCode));
        sParaTemp.put("subject", String.valueOf(params.get("prodName")));
        int totalFee = (Integer) params.get("totalFee");
        sParaTemp.put("total_amount", String.valueOf(totalFee / 100.0));
        sParaTemp.put("passback_params", String.valueOf(coreEpId));
        alipayRequest.setBizContent(JsonUtils.toJson(sParaTemp));//填充业务参数
        return aliPayProperties.getAlipayClient().pageExecute(alipayRequest).getBody(); //调用SDK生成表单
    }
}
