package com.all580.payment.thirdpay.ali.service;

import com.all580.payment.thirdpay.ali.config.AlipayConfig;
import com.all580.payment.thirdpay.ali.util.AlipaySubmit;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付服务
 *
 * @author panyi on 2016/10/15.
 * @since V0.0.1
 */
public class AliPayService {
    @Autowired
    private AlipayProperties alipayProperties;

    @Override
    public BasePayObj paymentQuery(BasePayObj obj) throws PaymentQueryException {
        QueryReq req = (QueryReq) obj;
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "single_trade_query");
        sParaTemp.put("partner", alipayProperties.getPartner());
        sParaTemp.put("_input_charset", alipayProperties.get_input_charset());
        sParaTemp.put("trade_no", req.getTrade_no());
        sParaTemp.put("out_trade_no", req.getOrderId());

        // 建立请求
        AlipayQueryResponse rsp = new AlipayQueryResponse();
        try {
            String res = AlipaySubmit.buildRequest("", "", sParaTemp, alipayProperties.getKey());
            Document doc = DocumentHelper.parseText(res);
            Element rootElement = doc.getRootElement();
            rsp.setResult(res);
            rsp.setIs_success(rootElement.selectSingleNode("//alipay/is_success").getText());
            if ("T".equals(rsp.getIs_success())) {
                rsp.setOut_trade_no(rootElement.selectSingleNode("//alipay/response/trade/out_trade_no").getText());
                rsp.setTrade_no(rootElement.selectSingleNode("//alipay/response/trade/trade_no").getText());
                rsp.setTrade_status(rootElement.selectSingleNode("//alipay/response/trade/trade_status").getText());
                rsp.setSign(rootElement.selectSingleNode("//alipay/sign").getText());
            } else {
                rsp.setSign(rootElement.selectSingleNode("//alipay/error").getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new PaymentQueryException("请求失败");
        }
        return rsp;
    }

    public String reqPay(long ordCode, Map<String, Object> params, String confData) throws Exception {
        String notify_url = notify_url = alipayProperties.getPay_notify_url();

        // 把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<>();
        sParaTemp.put("service", AlipayConfig.INF_NAME_PAY);
        sParaTemp.put("partner", alipayProperties.getPartner());
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);

        sParaTemp.put("payment_type", AlipayConfig.PayType.PROD_PURCHASE.toString());
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("return_url", req.getReturn_url());
        sParaTemp.put("seller_email", alipayProperties.getSeller_email());
        sParaTemp.put("out_trade_no", req.getOut_trade_no());
        sParaTemp.put("subject", req.getSubject());
        sParaTemp.put("total_fee", req.getTotal_fee());
        sParaTemp.put("body", req.getBody());
        sParaTemp.put("paymethod", req.getPaymethod());
        sParaTemp.put("defaultbank", req.getDefaultbank());
        sParaTemp.put("show_url", req.getShow_url());
        sParaTemp.put("anti_phishing_key", req.getAnti_phishing_key());
        sParaTemp.put("exter_invoke_ip", req.getExter_invoke_ip());
        sParaTemp.put("extra_common_param", req.getExtra_common_param());

        // 建立请求
        String sHtmlText = AlipaySubmit.buildRequest(alipayProperties.getKey(), sParaTemp, "get", "确认");

        return sHtmlText;
    }

    public void reqRefund() {
        String notify_url = alipayProperties.getRecharge_notify_url();
    }
}
