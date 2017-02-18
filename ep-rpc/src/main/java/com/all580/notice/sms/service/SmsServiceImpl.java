package com.all580.notice.sms.service;

import com.all580.notice.api.service.SmsService;
import com.all580.notice.dao.SmsAccountConfMapper;
import com.all580.notice.dao.SmsTmplMapper;
import com.all580.notice.entity.SmsAccountConf;
import com.all580.notice.entity.SmsTmpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信服务
 *
 * @author panyi on 2016/10/18.
 * @since V0.0.1
 */
@Service("smsServiceImpl")
public class SmsServiceImpl implements SmsService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private static String FORMAT = "json";
    private static String SMS_TYPE = "normal";

    @Autowired
    private SmsTmplMapper smsTmplMapper;
    @Autowired
    private SmsAccountConfMapper smsAccountConfMapper;

    @Value("${sms.send}")
    private String smsSend = "1";

    @Override
    public Result send(String destPhoneNum, Integer smsType, Integer epId, Map<String, String> params) {
        Assert.notNull(destPhoneNum, "参数【destPhoneNum】不能为空");
        Assert.notNull(smsType, "参数【smsType】不能为空");
        Assert.notNull(epId, "参数【epId】不能为空");
        if(smsSend.equals("0")){// TODO: 2017/1/12 0012
            return   new Result(true);
        }
        SmsTmpl smsTmpl = smsTmplMapper.selectByEpIdAndType(epId, smsType);
        Assert.notNull(smsTmpl, MessageFormat.format("找不到短信模板:epId={0}|smsType={1}", epId, smsType));
        SmsAccountConf smsAccountConf = smsAccountConfMapper.selectByEpId(epId);
        Assert.notNull(smsAccountConf, MessageFormat.format("找不到短信账号配置:epId={0}", epId));
        return doSend(destPhoneNum, params, smsTmpl.getOut_sms_tpl_id(), smsAccountConf);
    }

    @Override
    public Result createConf(Integer epId, Map<String, String> conf) {
        Assert.notNull(epId, "参数【epId】不能为空");
        ParamsMapValidate.validate(conf, genRulesOfCreateConf());

        Result result = new Result();
        SmsAccountConf smsAccountConf = JsonUtils.map2obj(conf, SmsAccountConf.class);
        smsAccountConf.setEp_id(epId);
        smsAccountConfMapper.insertSelective(smsAccountConf);
        result.setSuccess();
        return result;
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
    private Result doSend(String destPhoneNum, Map<String, String> params, String outTplId,
                          SmsAccountConf smsAccountConf) {
        Result result = new Result();
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
            req.setSmsFreeSignName(epSignName);//短信签名
            rsp = client.execute(req);
            logger.debug("result:" + rsp.getBody());
            Map<String, Map<String, Map>> resultMap = mapper.readValue(rsp.getBody(), Map.class);

            if (resultMap.containsKey("alibaba_aliqin_fc_sms_num_send_response")) {
                Object ret = resultMap.get("alibaba_aliqin_fc_sms_num_send_response").get("result").get("success");
                if (ret != null && Boolean.TRUE.equals(ret)) {
                    // logger.info("短信发送成功");
                    result.setSuccess();
                }
            } else if (resultMap.containsKey("error_response")) {
                String code = String.valueOf(resultMap.get("error_response").get("code")) + ":" +
                        String.valueOf(resultMap.get("error_response").get("sub_msg"));
                result.setFail();
                result.setError("短信发送失败：" + code);
                logger.error(result.getError());
            }
        } catch (Exception e) {
            result.setFail();
            result.setError("短信发送失败：" + e.getMessage());
            logger.error(result.getError(), e);
        }
        return result;
    }

    /* 创建短信配置信息的验证规则 */
    private Map<String[], ValidRule[]> genRulesOfCreateConf() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "url", // 短信网关地址
                "appId", // 应用ID
                "appPwd", // 应用密码
                "sign" // 企业签名
        }, new ValidRule[]{new ValidRule.NotNull()});
        return rules;
    }

    @Override
    public void setIsSend(boolean isSend) {
        this.smsSend = isSend ? "1" : "0";
    }
}
