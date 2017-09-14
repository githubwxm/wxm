package com.all580.base.adapter.push;

import com.all580.ep.api.conf.EpConstant;
import com.framework.common.lang.JsonUtils;
import com.framework.common.net.HttpUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/4/6 9:15
 */
@Component(EpConstant.PUSH_ADAPTER + "GENERAL")
@Slf4j
public class GeneralPushMsgAdapter implements PushMsgAdapter {
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

    /**
     * 生成签名
     *
     * @param epId      企业ID
     * @param url       推送地址
     * @param msg       消息
     * @param originMsg 原始消息
     * @param config    推送配置信息
     * @return
     */
    @Override
    public String sign(String epId, String url, Map msg, Map originMsg, Map config) {
        String accessKey = CommonUtil.objectParseString(config.get("access_key"));
        if (!StringUtils.isEmpty(accessKey)) {
            log.warn("推送信息:{},没有企业:{}的access_key配置签名失败", url, epId);
            return null;
        }
        String data = JsonUtils.toJson(msg);
        data = data.replace("null", "").replaceAll("[\"\\\\\\[\\]\\{\\}]","");
        return CommonUtil.signForData(accessKey,data);
    }
}