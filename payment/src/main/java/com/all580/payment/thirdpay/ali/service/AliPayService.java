package com.all580.payment.thirdpay.ali.service;

import com.all580.payment.entity.EpPaymentConf;
import com.all580.payment.thirdpay.ali.config.AlipayConfig;
import com.all580.payment.thirdpay.ali.util.AlipayNotify;
import com.all580.payment.thirdpay.ali.util.AlipaySubmit;
import com.all580.payment.thirdpay.wx.model.WxProperties;
import com.all580.payment.vo.PayAttachVO;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import org.apache.commons.httpclient.util.DateUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
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
    @Autowired
    private AlipayProperties alipayProperties;

    public Map<String, String> paymentQuery(long ordCode) {
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "single_trade_query");
        sParaTemp.put("partner", alipayProperties.getPartner());
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("trade_no", "");
        sParaTemp.put("out_trade_no", String.valueOf(ordCode));

        // 建立请求
        Map<String, String> rsp = new HashMap<>();
        try {
            String res = AlipaySubmit.buildRequest("", "", sParaTemp, alipayProperties.getKey());
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

    public String reqPay(long ordCode, int coreEpId, Map<String, Object> params, String confData) {
        AlipayProperties alipayProperties = JsonUtils.fromJson(confData, AlipayProperties.class);
        String notify_url = alipayProperties.getPay_notify_url();

        // 把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<>();
        sParaTemp.put("service", AlipayConfig.INF_NAME_PAY);
        sParaTemp.put("partner", alipayProperties.getPartner());
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);

        sParaTemp.put("payment_type", AlipayConfig.PayType.PROD_PURCHASE.toString());
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("return_url", String.valueOf(params.get("returnUrl")));
        sParaTemp.put("seller_email", alipayProperties.getSeller_email());
        sParaTemp.put("out_trade_no", String.valueOf(ordCode));
        sParaTemp.put("subject", String.valueOf(params.get("prodName")));
        sParaTemp.put("total_fee", String.valueOf(params.get("totalFee")));
        // sParaTemp.put("body", null);
        sParaTemp.put("paymethod", "");
        sParaTemp.put("defaultbank", "");
        // sParaTemp.put("show_url", null);
        sParaTemp.put("anti_phishing_key", "");
        sParaTemp.put("exter_invoke_ip", "");

        PayAttachVO attachVO = new PayAttachVO("" + coreEpId, String.valueOf(params.get("serialNum")));
        sParaTemp.put("extra_common_param", JsonUtils.toJson(attachVO));

        // 建立请求
        String sHtmlText = AlipaySubmit.buildRequest(alipayProperties.getKey(), sParaTemp, "get", "确认");

        return sHtmlText;
    }

    public String reqRefund(Map<String, Object> params, String confData) {
        AlipayProperties alipayProperties = JsonUtils.fromJson(confData, AlipayProperties.class);
        String notify_url = alipayProperties.getRecharge_notify_url();

        // 把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", AlipayConfig.INF_NAME_REFUND);
        sParaTemp.put("partner", alipayProperties.getPartner());
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("notify_url", alipayProperties.getRefund_notify_url());
        sParaTemp.put("seller_email", alipayProperties.getSeller_email());
        sParaTemp.put("refund_date", DateFormatUtils.converToStringDate(new Date()));
        sParaTemp.put("batch_no", DateFormatUtils.getToday()+params.get("serialNum"));
        sParaTemp.put("batch_num", "1");
        String detail = params.get("outTransId")+"^"+params.get("")+"^"+"协议退款";
        sParaTemp.put("detail_data", detail);

        // 建立请求
        String sHtmlText = AlipaySubmit.buildRequest(alipayProperties.getKey(), sParaTemp, "get", "确认");
        return sHtmlText;
    }

    public boolean refundCallback(Map<String, String> params, EpPaymentConf epPaymentConf) {
        AlipayProperties alipayProperties = JsonUtils.fromJson(epPaymentConf.getConfData(), AlipayProperties.class);
        return AlipayNotify.verify(params, alipayProperties.getPartner(), alipayProperties.getKey());//验证成功

    }

    public boolean payCallback(Map<String, String> params, EpPaymentConf epPaymentConf) {
        AlipayProperties alipayProperties = JsonUtils.fromJson(epPaymentConf.getConfData(), AlipayProperties.class);
        return AlipayNotify.verify(params, alipayProperties.getPartner(), alipayProperties.getKey());//验证成功

    }
}
