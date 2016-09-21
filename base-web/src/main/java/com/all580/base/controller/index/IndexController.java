package com.all580.base.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.common.BaseController;

@Controller
public class IndexController extends BaseController {
	
	
	
	@RequestMapping("/")
	public String homePage(){
	
		return "index";
	}

}
