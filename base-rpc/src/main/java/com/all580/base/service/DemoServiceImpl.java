package com.all580.base.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.framework.common.BaseService;
import com.framework.common.Result;
import com.all580.base.api.model.Demo;
import com.all580.base.api.service.DemoService;

@Service
public class DemoServiceImpl implements DemoService {

	private static List<Demo> demos = new ArrayList<Demo>();
	static {
		for (int i = 0; i < 1000; i++) {
			demos.add(new Demo(i, "name" + i));
		}
	}
	
	public Result<Demo> findDemo(int id) {
		Result<Demo> result = new Result<Demo>();
		try {
			result.put(localSearchDemo(id));
			result.setSuccess();
			System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]  " + id + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
		} catch (Exception e) {
			System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]  " + id + " search error" + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
			result.setFail();
			result.setError(Result.DB_FAIL, "数据查询出错。出错原因：***************");
		}
		return result;
	}
	
	/**
	 * 模拟本地curd
	 * @param id
	 * @return
	 */
	private Demo localSearchDemo (int id) throws Exception {
		if (id >= demos.size()) return null;
		return demos.get(id);
	}

	public Result<List<Demo>> allDemo() {
		Result<List<Demo>> result = new Result<List<Demo>>();
		try {
			result.put(demos);
			result.setSuccess();
		} catch (Exception e) {
			result.setFail();
			result.setError(Result.DB_FAIL, "数据库查询出错。出错原因：***************");
		}
		return result;
	}

	public Result<Object> deleteDemo(int id) {
		Result<Object> result = new Result<Object>();
		try {
			demos.remove(id);
			result.setSuccess();
		} catch (Exception e) {
			result.setFail();
			result.setError(Result.DB_FAIL, "数据库查询出错。出错原因：***************");
		}
		return result;
	}
    
}
