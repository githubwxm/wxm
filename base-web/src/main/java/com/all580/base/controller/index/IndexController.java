package com.all580.base.controller.index;

import com.all580.base.manager.MnsEventCache;
import com.all580.ep.api.conf.EpConstant;
import com.all580.notice.api.service.SmsService;
import com.all580.payment.api.service.ThirdPayService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.lang.exception.ApiException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@Slf4j
public class IndexController extends BaseController {
	@Autowired
	private SmsService smsService;

	@Autowired
	private MnsEventCache mnsEventCache;
	@Autowired
	private ThirdPayService thirdPayService;

	@RequestMapping(value = "sms/set", method = RequestMethod.GET)
	@ResponseBody
	public Result setSms(HttpServletRequest request, HttpServletResponse response, @RequestParam boolean is){
		Result result=smsService.setIsSend(is);
		result.put(is);
		return result;
	}

	@RequestMapping(value = "events")
	@ResponseBody
	public Result listEvents() {
		Result<Map> result = new Result<>(true);
		result.put(mnsEventCache.getCacheEventsMap().asMap());
		return result;
	}

	@RequestMapping(value = "api/signature/wx", method = RequestMethod.POST)
	@ResponseBody
	public Result signatureForWx(@RequestBody Map params) {
		Result<Map> result = new Result<>(true);
        Integer coreEpId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID));
        Result<Map> tokenResult = thirdPayService.getWxAccessToken(coreEpId);
        if (!tokenResult.isSuccess()) {
            throw new ApiException("获取微信token失败");
        }
        Map map = tokenResult.get();
        String nonceStr = DigestUtils.md5Hex(RandomStringUtils.randomAlphanumeric(10));
        long timestamp = System.currentTimeMillis() / 1000;
        String signature = DigestUtils.sha1Hex(String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s", map.get("ticket"), nonceStr, timestamp));
        result.put("app_id", map.get("appId"));
        result.put("timestamp", timestamp);
        result.put("noncestr", nonceStr);
        result.put("signature", signature);
		return result;
	}
}
