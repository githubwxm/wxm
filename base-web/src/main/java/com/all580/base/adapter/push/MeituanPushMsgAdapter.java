package com.all580.base.adapter.push;

import com.all580.base.util.BasicAuthorizationUtils;
import com.all580.ep.api.conf.EpConstant;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alone on 17-4-7.
 */
@Component(EpConstant.PUSH_ADAPTER + "MEITUAN")
@Slf4j
public class MeituanPushMsgAdapter extends GeneralPushMsgAdapter {
    @Override
    public String parseMsg(Map map, String msg) {
        msg = super.parseMsg(map, msg);
        String opCode = CommonUtil.objectParseString(map.get("op_code"));
        if (StringUtils.isEmpty(opCode)) {
            throw new ApiException("消息推送OPCODE为空");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("partnerId", 0);
        Map<String, Object> body = new HashMap<>();
        switch (opCode) {
            case "CONSUME":
                body.put("orderId", 11);
                body.put("partnerOrderId", map.get("number"));
                body.put("quantity", map.get("quantity"));
                body.put("usedQuantity", map.get("total_usd_qty"));
                body.put("refundedQuantity", map.get("rfd_qty"));
        }
        return msg;
    }

    @Override
    public void push(String epId, String url, String msg, Map config) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        JSONObject res = null;
        HttpPost request = new HttpPost(url);
        try {
            StringEntity postingString = new StringEntity(msg);// json传递
            request.setEntity(postingString);
            request.setHeader("Content-type", "application/json");
            BasicAuthorizationUtils.generateAuthAndDateHeader(request, "123", "123");
            HttpResponse response = httpClient.execute(request);
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            res = JSONObject.fromObject(responseContent);
        } catch (Exception e) {
            log.warn("推送美团请求失败", e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                log.warn("关闭美团HTTP异常", e);
            }
        }

        if (res == null || res.isNullObject() || res.getInt("code") != 200) {
            log.warn("推送信息URL:{} 推送失败:{}", new Object[]{url, res});
            throw new ApiException(res == null ? "null" : res.toString());
        }
    }
}
