package com.all580.base.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class CustomJacksonConvert extends MappingJackson2HttpMessageConverter {
	private SerializeConfig config = null;
	public CustomJacksonConvert() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

		config = new SerializeConfig();
		SimpleDateFormatSerializer dateFormatSerializer = new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss");
		config.put(Timestamp.class, dateFormatSerializer);
		config.put(Date.class, dateFormatSerializer);
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
			result.put("sync_data", r.getExt(Result.SYNC_DATA));
			String key = CommonUtil.objectParseString(r.getExt("access_key"));
			 key= key==null?"":key;
			//TreeMap tree=new TreeMap(result);//排序  不确定是否需要加
			//String data = JsonUtils.toJson(result);//替换转String 那行
			//result=  JSON.parseObject(data,LinkedHashMap.class,Feature.OrderedField);
//	2		result=SortMap.sortMapByKey(result);
//			String data = JsonUtils.toJson(result);

			String data = JSONObject.toJSONString(result, config, SerializerFeature.SortField);

			String sign=CommonUtil.signForData(key,data);
			result.put("sign",sign);
			super.writeInternal(result, outputMessage);
			return;
		}
		super.writeInternal(object, outputMessage);
	}
}
