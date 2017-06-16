package com.all580.base.adapter.push;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description:
 * @date 17-5-15 上午11:51
 */

import com.all580.base.adapter.push.qunar.request.*;
import com.all580.ep.api.conf.EpConstant;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.codec.TranscodeUtil;
import com.framework.common.net.HttpUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.util.JAXBContextSingleton;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(EpConstant.PUSH_ADAPTER + "QUNAR")
@Slf4j
public class QunarPushMsgAdapter extends GeneralPushMsgAdapter implements PushMsgAdapter, InitializingBean {
    private final Map<String, Object> MARSHALLER_PROPERTY = new HashMap<>();
    private static final String XSD = "com.all580.base.adapter.push.qunar.request";
    private static final String APPLICATION = "Qunar.Menpiao.Agent";
    private static final String PROCESSOR = "SupplierDataExchangeProcessor";
    private static final String VERSION = "v2.0.1";
    private Map<String, String> opCodeUrl = new HashMap<>();

    @Override
    public Map parseMsg(Map map, Map config, String msg) {
        super.parseMsg(map, config, msg);
        String opCode = CommonUtil.objectParseString(map.get("op_code"));
        if (StringUtils.isEmpty(opCode)) {
            throw new ApiException("消息推送OPCODE为空");
        }
        return map;
    }

    @Override
    public void push(String epId, String url, Map msg, Map originMsg, Map config) {
        String opCode = originMsg.get("op_code").toString();
        RequestBody body = makeRequestBody(opCode, msg);
        if (body == null) {
            log.warn("去哪儿不支持的OPCODE:{}", opCode);
            return;
        }
        if (!opCodeUrl.containsKey(opCode)) {
            log.warn("请配置去哪儿推送OPCODE:{}", opCode);
            return;
        }
        try {
            String configStr = CommonUtil.objectParseString(config.get("config"));
            if (!JSONUtils.mayBeJSON(configStr)) {
                throw new ApiException("请配置去哪儿推送配置CONFIG");
            }
            JSONObject configJson = JSONObject.fromObject(configStr);
            String user = configJson.getString("create_user");
            String identity = configJson.getString("supplier_identity");
            String key = configJson.getString("remote_access_key");
            String xml = parseRequestXml(body, user, identity);
            log.debug("推送去哪儿信息XML:{}", xml);
            JSONObject param = new JSONObject();
            String value = TranscodeUtil.strToBase64Str(xml);
            param.put("data", value);
            param.put("signed", DigestUtils.md5Hex(key + value));
            param.put("securityType", "MD5");
            Map<String, Object> map = new HashMap<>();
            map.put("method", opCodeUrl.get(opCode));
            map.put("requestParam", param.toString());
            String response = HttpUtils.post(url, map, "UTF-8");
            log.debug("推送去哪儿返回信息:{}", response);
            String data = JsonUtils.json2Map(response).get("data").toString();
            String resultXml = TranscodeUtil.base64StrToStr(data);
            log.debug("推送去哪儿返回XML:{}", resultXml);
            int startIndex = resultXml.indexOf("<code>");
            int endIndex = resultXml.indexOf("</code>", startIndex);
            if (startIndex > -1 && endIndex > -1) {
                String result = resultXml.substring(startIndex + 6, endIndex);
                if (result.equals("1000")) {
                    return;
                }
            }
            log.warn("推送信息URL:{} 推送失败:{}", new Object[]{url, resultXml});
            throw new ApiException(resultXml);
        } catch (Exception e) {
            log.warn("推送去哪儿请求失败", e);
            throw new ApiException("推送去哪儿请求失败", e);
        }
    }

    private RequestBody makeRequestBody(String opCode, Map map) {
        switch (opCode) {
            case "SENT":
                NoticeOrderEticketSendedRequestBody body = new NoticeOrderEticketSendedRequestBody();
                NoticeOrderEticketSendedRequestBody.OrderInfo orderInfo = new NoticeOrderEticketSendedRequestBody.OrderInfo();
                orderInfo.setPartnerorderId(map.get("number").toString());
                List maSendResponse = (List) map.get("ma_send_response");
                if (maSendResponse != null && !maSendResponse.isEmpty()) {
                    Map ma = (Map) maSendResponse.get(0);
                    orderInfo.setEticketNo(ma.get("image_url").toString());
                    orderInfo.setEticketSended(true);
                } else {
                    orderInfo.setEticketSended(false);
                }
                body.setOrderInfo(orderInfo);
                return body;
            case "CONSUME":
                NoticeOrderConsumedRequestBody consumedRequestBody = new NoticeOrderConsumedRequestBody();
                NoticeOrderConsumedRequestBody.OrderInfo consumeOrderInfo = new NoticeOrderConsumedRequestBody.OrderInfo();
                consumeOrderInfo.setPartnerorderId(map.get("number").toString());
                Map consumeInfo = (Map) map.get("consume_info");
                consumeOrderInfo.setConsumeInfo(String.format("本次消费%s张", consumeInfo.get("consume_amount")));
                consumeOrderInfo.setUseQuantity(Integer.parseInt(map.get("usd_qty").toString()));
                consumeOrderInfo.setOrderQuantity(Integer.parseInt(map.get("quantity").toString()));
                consumedRequestBody.setOrderInfo(consumeOrderInfo);
                return consumedRequestBody;
            case "REFUND":
                NoticeOrderRefundApproveResultRequestBody refundApproveResultRequestBody = new NoticeOrderRefundApproveResultRequestBody();
                NoticeOrderRefundApproveResultRequestBody.OrderInfo refundOrderInfo = new NoticeOrderRefundApproveResultRequestBody.OrderInfo();
                refundOrderInfo.setPartnerorderId(map.get("number").toString());
                refundOrderInfo.setOrderQuantity(Integer.parseInt(map.get("quantity").toString()));
                refundOrderInfo.setRefundQuantity(Integer.parseInt(map.get("refund_quantity").toString()));
                refundOrderInfo.setRefundSeq(map.get("refund_seq").toString());
                refundOrderInfo.setRefundResult(map.get("refund_result").toString());
                refundOrderInfo.setRefundJudgeMark(refundOrderInfo.getRefundResult().equals("APPROVE") ? "同意退订" : "不同意退订");
                refundApproveResultRequestBody.setOrderInfo(refundOrderInfo);
                return refundApproveResultRequestBody;
        }
        return null;
    }

    private String parseRequestXml(RequestBody body, String user, String identity) throws JAXBException {
        Request request = new Request();
        request.setBody(body);
        request.setHeader(new RequestHeader());
        request.getHeader().setApplication(APPLICATION);
        request.getHeader().setProcessor(PROCESSOR);
        request.getHeader().setVersion(VERSION);
        request.getHeader().setBodyType(
                request.getBody().getClass().getSimpleName());
        request.getHeader().setCreateTime(DateFormatUtils.parseDateToDatetimeString(new Date()));
        request.getHeader().setCreateUser(user);
        request.getHeader().setSupplierIdentity(identity);

        return JAXBContextSingleton.marshalString(request, XSD, MARSHALLER_PROPERTY);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MARSHALLER_PROPERTY.put(Marshaller.JAXB_SCHEMA_LOCATION, "http://piao.qunar.com/2013/QMenpiaoRequestSchema QMRequestDataSchema-2.0.1.xsd");
        opCodeUrl.put("CONSUME", "noticeOrderConsumed");
        opCodeUrl.put("REFUND", "noticeOrderRefundApproveResult");
        opCodeUrl.put("SENT", "noticeOrderEticketSended");
    }
}
