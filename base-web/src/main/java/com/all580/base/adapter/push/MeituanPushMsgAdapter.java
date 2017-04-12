package com.all580.base.adapter.push;

import com.all580.base.manager.PushMsgManager;
import com.all580.base.util.BasicAuthorizationUtils;
import com.all580.ep.api.conf.EpConstant;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.JobClient;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alone on 17-4-7.
 */
@Component(EpConstant.PUSH_ADAPTER + "MEITUAN")
@Slf4j
public class MeituanPushMsgAdapter extends GeneralPushMsgAdapter implements InitializingBean {
    @Autowired
    private PushMsgManager pushMsgManager;
    @Autowired
    private JobClient jobClient;
    private Map<String, String> opCodeUrl = new HashMap<>();

    @Override
    public Map parseMsg(Map map, Map config, String msg) {
        super.parseMsg(map, config, msg);
        String opCode = CommonUtil.objectParseString(map.get("op_code"));
        if (StringUtils.isEmpty(opCode)) {
            throw new ApiException("消息推送OPCODE为空");
        }
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        switch (opCode) {
            case "CONSUME":
                body.put("orderId", map.get("outer_id"));
                body.put("partnerOrderId", map.get("number"));
                body.put("quantity", map.get("quantity"));
                body.put("usedQuantity", map.get("total_usd_qty"));
                body.put("refundedQuantity", map.get("rfd_qty"));
                break;
            case "REFUND_FAIL":
            case "REFUND":
                params.put("code", opCode.equals("REFUND_FAIL") ? 606 : 200);
                params.put("describe", opCode.equals("REFUND_FAIL") ? "余票不足或拒绝退票" : "退款成功");
                body.put("orderId", map.get("outer_id"));
                body.put("refundId", map.get("refund_outer_id"));
                body.put("partnerOrderId", map.get("number"));
                body.put("requestTime", map.get("apply_time"));
                body.put("responseTime", map.get("audit_time"));
                break;
            case "SEND":
                params.put("issueType", 0);
                body.put("orderId", map.get("outer_id"));
                body.put("partnerOrderId", map.get("number"));
                body.put("voucherType", 2);
                List maSendResponse = (List) map.get("ma_send_response");
                String[] vouchers = new String[maSendResponse.size()];
                String[] voucherPics = new String[maSendResponse.size()];
                int i = 0;
                for (Object o : maSendResponse) {
                    Map ma = (Map) o;
                    vouchers[i] = (String) ma.get("voucher_value");
                    voucherPics[i] = (String) ma.get("image_url");
                    i++;
                }
                body.put("vouchers", vouchers);
                body.put("voucherPics", voucherPics);
                break;
        }
        params.put("body", body);
        return params;
    }

    @Override
    public void push(String epId, String url, Map msg, Map originMsg, Map config) {
        String opCode = originMsg.get("op_code").toString();
        if (!opCodeUrl.containsKey(opCode)) {
            throw new ApiException("请配置美团推送OPCODE");
        }
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        JSONObject res = null;
        HttpPost request = new HttpPost(url + opCodeUrl.get(opCode));
        try {
            String configStr = CommonUtil.objectParseString(config.get("config"));
            if (!JSONUtils.mayBeJSON(configStr)) {
                throw new ApiException("请配置美团推送配置CONFIG");
            }
            JSONObject configJson = JSONObject.fromObject(configStr);
            String clientId = configJson.getString("clientId");
            String clientSecret = configJson.getString("clientSecret");
            String partnerId = configJson.getString("partnerId");
            msg.put("partnerId", partnerId);
            StringEntity postingString = new StringEntity(JsonUtils.toJson(msg));// json传递
            request.setEntity(postingString);
            request.setHeader("Content-type", "application/json");
            BasicAuthorizationUtils.generateAuthAndDateHeader(request, clientId, clientSecret);
            HttpResponse response = httpClient.execute(request);
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            res = JSONObject.fromObject(responseContent);
        } catch (Exception e) {
            log.warn("推送美团请求失败", e);
            throw new ApiException("推送美团请求失败", e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                log.warn("关闭美团HTTP异常", e);
            }
        }

        if (res == null || res.isNullObject() || res.getInt("code") != 200) {
            log.warn("推送信息URL:{} 推送失败:{}", new Object[]{url, res});
            if (res != null && opCode.equals("SEND")) {
                log.info("美团出票推送返回失败,申请退票");
                // 请求运营平台申请退订
                String clientUrl = config.get("client_url").toString();
                String accessId = config.get("access_id").toString();
                Map<String, Object> params = new HashMap<>();
                params.put("access_id", accessId);
                params.put("order_item_sn", originMsg.get("number"));
                params.put("apply_from", ProductConstants.RefundEqType.SELLER);
                params.put("cause", "meituan");
                params.put("outer_id", originMsg.get("outer_id"));
                String content = JsonUtils.toJson(params);
                String sign = DigestUtils.md5Hex(content);
                try {
                    pushMsgManager.postClient(clientUrl + "/service/remote/core/order/refund/ota/apply", content, sign);
                } catch (Exception e) {
                    log.warn("美团退票申请异常", e);
                    jobClient.submitJob(pushMsgManager.addClientJob(clientUrl, content, sign));
                }
                return;
            }
            throw new ApiException(res == null ? "null" : res.toString());
        }

        if (opCode.equals("SEND")) {
            // 推送发码信息
            Map<String, Object> params = new HashMap<>();
            params.put("op_code", "VOUCHER");
            params.put("code", 200);
            params.put("describe", "出票成功");
            params.put("body", msg.get("body"));
            List<Job> jobs = new ArrayList<>();
            pushMsgManager.pushForAddJob("MEITUAN_VOUCHER_SEND", epId, url, "MEITUAN", JsonUtils.toJson(params), params, config, jobs);
            if (!jobs.isEmpty() && jobs.size() > 0) {
                jobClient.submitJob(jobs);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        opCodeUrl.put("CONSUME", "/rhone/mtp/api/order/consume/notice");
        opCodeUrl.put("REFUND_FAIL", "/rhone/mtp/api/order/refund/notice");
        opCodeUrl.put("REFUND", "/rhone/mtp/api/order/refund/notice");
        opCodeUrl.put("SEND", "/rhone/mtp/api/order/pay/notice");
        opCodeUrl.put("VOUCHER", "/rhone/mtp/api/order/vouchers/notice");
    }
}
