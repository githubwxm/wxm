package com.all580.base.controller.index;

import com.all580.base.manager.MnsEventCache;
import com.all580.notice.api.service.SmsService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

	@RequestMapping(value = "sms/set", method = RequestMethod.GET)
	@ResponseBody
	public Result setSms(HttpServletRequest request, HttpServletResponse response, @RequestParam boolean is){
		Result result=smsService.setIsSend(is);
		result.put(is);
		return result;
	}

	@RequestMapping("events")
	public Result listEvents() {
		Result<Map> result = new Result<>(true);
		result.put(mnsEventCache.getCacheEventsMap().asMap());
		return result;
	}
}
