package com.all580.base.controller.notice;

import com.all580.ep.api.conf.EpConstant;
import com.all580.notice.api.service.SmsService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 短信服务网关
 *
 * @author panyi on 2016/10/29.
 * @since V0.0.1
 */
@Controller
@RequestMapping("api/sms")
public class SmsController extends BaseController {
    @Resource
    private SmsService smsService;

    /**
     * 发送短信
     *
     * @param data     请求数据
     *          ｛destPhoneNum： 目标手机号码
     *          smsType：      短信类型
     *          params：       短信参数 ｝
     * @return 成功{code:200}；失败{code:xx,error:错误信息}
     */
    @ResponseBody
    @RequestMapping(value = "send", method = RequestMethod.POST)
    public Result send(@RequestBody Map<String, Object> data) {
        String destPhoneNum=CommonUtil.objectParseString(data.remove("destPhoneNum"));
        Integer smsType=CommonUtil.objectParseInteger( data.remove("smsType"));
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        Map<String, String> params=(Map<String, String>)data.get("params");
        return smsService.send(destPhoneNum, smsType, coreEpId,params );
    }
}
