package com.all580.base.adapter.push;

import com.all580.ep.api.conf.EpConstant;
import com.framework.common.lang.JsonUtils;
import com.framework.common.net.HttpUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/7/27 0027.
 */
@Component(EpConstant.PUSH_ADAPTER + "B2C")
@Slf4j
public class B2CPushMsgAdapter extends GeneralPushMsgAdapter implements InitializingBean {
    private Map<String, String> opCodeUrl = new HashMap<>();


    /**
     * 解析消息
     *
     * @param msg 消息
     * @return
     */
    @Override
    public Map parseMsg(Map map, Map config, String msg) {
        String opCode = CommonUtil.objectParseString(map.get("op_code"));
        String refundResult = CommonUtil.objectParseString(map.get("refund_result"));
        if (opCode != null && "REFUND".equals(opCode) && refundResult != null && "REJECT".equals(refundResult)) {
            map.put("op_code", "REFUND_FAIL");
        }
        return map;
    }

    /**
     * 推送消息
     *
     * @param epId   企业ID
     * @param url    推送地址
     * @param msg    消息
     * @param config
     */
    @Override
    public void push(String epId, String url, Map msg, Map originMsg, Map config) {
        String res = HttpUtils.postJson(url, JsonUtils.toJson(msg), "UTF-8");
        if (!res.equalsIgnoreCase("ok")) {
            log.warn("推送信息URL:{} 推送失败:{}", new Object[]{url, res});
            throw new ApiException(res);
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        opCodeUrl.put("CONSUME", "/rhone/mtp/api/order/consume/notice");
        opCodeUrl.put("REFUND_FAIL", "/rhone/mtp/api/order/refund/notice");
        opCodeUrl.put("REFUND", "/rhone/mtp/api/order/refund/notice");
        opCodeUrl.put("SENT", "/rhone/mtp/api/order/pay/notice");
    }
}
