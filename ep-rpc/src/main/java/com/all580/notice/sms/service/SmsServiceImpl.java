package com.all580.notice.sms.service;

import com.all580.notice.api.service.SmsService;
import com.all580.notice.dao.SmsAccountConfMapper;
import com.all580.notice.dao.SmsTmplMapper;
import com.all580.notice.entity.SmsAccountConf;
import com.all580.notice.entity.SmsTmpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 短信服务
 *
 * @author panyi on 2016/10/18.
 * @since V0.0.1
 */
public class SmsServiceImpl implements SmsService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private static String FORMAT = "json";
    private static String SMS_TYPE = "normal";

    @Autowired
    private SmsTmplMapper smsTmplMapper;
    @Autowired
    private SmsAccountConfMapper smsAccountConfMapper;

    @Override
    public boolean send(String destPhoneNum, Integer smsType, Integer epId, Map<String, String> params) {
        SmsTmpl smsTmpl = smsTmplMapper.selectByEpIdAndType(epId, smsType);
        SmsAccountConf smsAccountConf = smsAccountConfMapper.selectByEpId(epId);
        return send(destPhoneNum, params, smsTmpl.getOutSmsTplId(), smsAccountConf);
    }

    /**
     * 发送短信
     *
     * @param destPhoneNum   目的手机号码
     * @param params         模板参数
     * @param outTplId       外部模板ID
     * @param smsAccountConf 账号配置
     * @return 发送是否成功
     */
    private boolean send(String destPhoneNum, Map<String, String> params, String outTplId,
                         SmsAccountConf smsAccountConf) {
        String url = smsAccountConf.getUrl();
        String appKey = smsAccountConf.getAppid();
        String secret = smsAccountConf.getApppwd();
        String epSignName = smsAccountConf.getSign(); // 企业签名

        TaobaoClient client = new DefaultTaobaoClient(url, appKey, secret, FORMAT);

        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType(SMS_TYPE);
        req.setRecNum(destPhoneNum);//号码
        req.setSmsTemplateCode(outTplId);//短信模板

        AlibabaAliqinFcSmsNumSendResponse rsp = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            req.setSmsParamString(mapper.writeValueAsString(params));
            req.setSmsFreeSignName(new String(epSignName.getBytes("ISO-8859-1")));//短信签名
            rsp = client.execute(req);
            logger.debug("result:" + rsp.getBody());
            Map<String, Map<String, Map>> resultMap = mapper.readValue(rsp.getBody(), Map.class);

            if (resultMap.containsKey("alibaba_aliqin_fc_sms_num_send_response")) {
                Object result = resultMap.get("alibaba_aliqin_fc_sms_num_send_response").get("result").get("success");
                if (result != null && Boolean.TRUE.equals(result)) {
                    return true;
                }
            } else if (resultMap.containsKey("error_response")) {
                String code = String.valueOf(resultMap.get("error_response").get("code")) + ":" +
                        String.valueOf(resultMap.get("error_response").get("sub_msg"));
                return true;// TODO panyi  返回报错信息
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
