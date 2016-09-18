package com.all580.base.api.service;

import java.util.List;

import com.framework.common.Result;
import com.all580.base.api.model.Demo;

public interface DemoService {

	Result<Demo> findDemo (int id);
	
	Result<List<Demo>> allDemo ();
	
	Result<Object> deleteDemo (int id);
}
