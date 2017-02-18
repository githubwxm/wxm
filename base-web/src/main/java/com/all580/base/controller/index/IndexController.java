package com.all580.base.controller.index;

import com.all580.notice.api.service.SmsService;
import com.framework.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController extends BaseController {
	@Autowired
	private SmsService smsService;

	@RequestMapping("/sms/set")
	public void setSms(@RequestParam boolean is){
		smsService.setIsSend(is);
	}
}
