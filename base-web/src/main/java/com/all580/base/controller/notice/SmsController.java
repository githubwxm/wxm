package com.all580.base.controller.notice;

import com.all580.base.manager.SmsValidateManager;
import com.all580.ep.api.conf.EpConstant;
import com.all580.notice.api.service.SmsService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private SmsValidateManager smsValidateManager;

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

    @ResponseBody
    @RequestMapping(value = "config/add", method = RequestMethod.POST)
    public Result<?> addConfig(@RequestBody Map params) {
        ParamsMapValidate.validate(params, smsValidateManager.addValidate());
        return smsService.addConfig(params);
    }

    @ResponseBody
    @RequestMapping(value = "config/update", method = RequestMethod.POST)
    public Result<?> updateConfig(@RequestBody Map params) {
        ParamsMapValidate.validate(params, smsValidateManager.updateValidate());
        return smsService.updateConfig(params);
    }

    @ResponseBody
    @RequestMapping(value = "template/add", method = RequestMethod.POST)
    public Result<?> addTemplate(@RequestBody Map params) {
        ParamsMapValidate.validate(params, smsValidateManager.addTemplateValidate());
        return smsService.addTemplate(params);
    }

    @ResponseBody
    @RequestMapping(value = "template/update", method = RequestMethod.POST)
    public Result<?> updateTemplate(@RequestBody Map params) {
        ParamsMapValidate.validate(params, smsValidateManager.updateTemplateValidate());
        return smsService.updateTemplate(params);
    }

    @ResponseBody
    @RequestMapping(value = "template/remove", method = RequestMethod.POST)
    public Result<?> removeTemplate(@RequestBody Map params) {
        return smsService.removeTemplate(params);
    }

    @ResponseBody
    @RequestMapping("config/get")
    public Result<?> getConfig(@RequestParam Integer ep_id) {
        return smsService.getConfig(ep_id);
    }

    @ResponseBody
    @RequestMapping("template/list")
    public Result<?> listTemplate(@RequestParam Integer ep_id, Integer type) {
        return smsService.listTemplate(ep_id, type);
    }
}
