package com.all580.base.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class CustomJacksonConvert extends MappingJackson2HttpMessageConverter {
	public CustomJacksonConvert() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		super.setObjectMapper(mapper);
	}

	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		outputMessage.getHeaders().set("If-Modified-Since", "0");
		outputMessage.getHeaders().set("expires", "0");
		if (object instanceof Result) {
			Result r = (Result) object;
			Map<String, Object> result = new HashMap<>();
			result.put("code", r.getCode() == null ? (r.isSuccess() ? Result.SUCCESS : Result.FAIL) : r.getCode());
			result.put("message", r.getError()== null ? (r.isSuccess() ? "操作成功" : "操作失败") :r.getError());
			result.put("data", r.get());
			result.put("sync_data", r.getExt(Result.SYNC_DATA) == null ? Collections.EMPTY_MAP : r.getExt(Result.SYNC_DATA));
			String key = CommonUtil.objectParseString(r.getExt("access_key"));
			 key= key==null?"":key;
			TreeMap tree=new TreeMap(result);//排序  不确定是否需要加
			String data = JsonUtils.toJson(tree);//替换转String 那行
			//String data1 = JSON.toJSONString(result);
			String sign=CommonUtil.signForData(key,data);
			result.put("sign",sign);
			super.writeInternal(result, outputMessage);
			return;
		}
		super.writeInternal(object, outputMessage);
	}
}
