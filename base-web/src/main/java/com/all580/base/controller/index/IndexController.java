package com.all580.base.controller.index;

import com.all580.base.manager.MnsEventCache;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.notice.api.service.SmsService;
import com.all580.order.api.service.OrderService;
import com.all580.payment.api.service.EpPaymentConfService;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.product.api.service.ProductRPCService;
import com.all580.report.api.service.QueryOrderService;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.lang.exception.ApiException;
import javax.lang.exception.ParamsMapValidationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
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
	@Autowired
	private EpService epService;
	@Autowired
	private EpPaymentConfService epPaymentConfService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private VoucherRPCService voucherRPCService;
	@Autowired
	private ProductRPCService productRPCService;
	@Autowired
	private QueryOrderService queryOrderService;

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
        String url = CommonUtil.objectParseString(params.get("url"));
        if (StringUtils.isEmpty(url)) {
        	throw new ParamsMapValidationException("url 不能为空");
		}
		url = url.replaceAll("#(.*)", "");
        Result<Map> tokenResult = thirdPayService.getWxAccessToken(coreEpId);
        if (!tokenResult.isSuccess()) {
            throw new ApiException("获取微信token失败");
        }
        Map map = tokenResult.get();
        String nonceStr = DigestUtils.md5Hex(RandomStringUtils.randomAlphanumeric(10));
        long timestamp = System.currentTimeMillis() / 1000;
        String signature = DigestUtils.sha1Hex(String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s", map.get("ticket"), nonceStr, timestamp, url));
        result.put("app_id", map.get("appId"));
        result.put("timestamp", timestamp);
        result.put("noncestr", nonceStr);
        result.put("signature", signature);
		return result;
	}

	@RequestMapping(value = "api/openid/wx", method = RequestMethod.POST)
	@ResponseBody
	public Result openidForWx(@RequestBody Map params) {
        Integer coreEpId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID));
		Object code = params.get("code");
		if (code == null) {
			throw new ParamsMapValidationException("code 不能为空");
		}
		Result<Map> tokenResult = thirdPayService.getWxOpenid(coreEpId, code.toString());
        if (!tokenResult.isSuccess()) {
            throw new ApiException("获取微信openid失败");
        }
		return tokenResult;
	}

	@RequestMapping(value = "heartbeat", method = RequestMethod.POST)
	@ResponseBody
	public Result heartbeat(HttpServletResponse response) {
		Result result = new Result(true);
		Map<String, Long> map = new HashMap<>();
		map.put("ep", heartbeat(epService, response));
		map.put("payment", heartbeat(epPaymentConfService, response));
		map.put("voucher", heartbeat(voucherRPCService, response));
		map.put("product", heartbeat(productRPCService, response));
		map.put("order", heartbeat(orderService, response));
		map.put("report", heartbeat(queryOrderService, response));
		result.put(map);
		return result;
	}

	private long heartbeat(Object service, HttpServletResponse response) {
		long start = System.currentTimeMillis();
		try {
			Method method = null;
			for (Class<?> aClass : service.getClass().getInterfaces()) {
				if (aClass.getName().startsWith("com.all580.")) {
					try {
						method = aClass.getMethod("heartbeat");
					} catch (NoSuchMethodException ignored) {}
				}
			}
			if (method == null) {throw new NoSuchMethodException();}
			method.invoke(service);
		} catch (Exception e) {
			log.warn("heartbeat error", e);
			response.setStatus(504);
			return -1;
		}
		return System.currentTimeMillis() - start;
	}
}
