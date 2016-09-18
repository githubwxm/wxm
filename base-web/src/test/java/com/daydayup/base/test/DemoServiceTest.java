package com.daydayup.base.test;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import com.all580.base.api.service.DemoService;
import com.framework.common.Result;

import javax.annotation.Resource;

public class DemoServiceTest extends BaseTest {

	@Resource
	DemoService demoService;
	
	@Test
	@Rollback(value=false)
	public void testDeleteDemo(){
		Result<Object> result = demoService.deleteDemo(1);
		System.out.println(result.toJsonString());
	}
}
