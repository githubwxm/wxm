package com.all580.base.util;


import com.alibaba.fastjson.serializer.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

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
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Boolean.class, new JsonSerializer<Boolean>() {
			@Override
			public void serialize(Boolean aBoolean, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
				jsonGenerator.writeNumber(aBoolean ? 1 : 0);
			}
		});
		mapper.registerModule(simpleModule);

		config = new SerializeConfig();
		SimpleDateFormatSerializer dateFormatSerializer = new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss");
		config.put(Timestamp.class, dateFormatSerializer);
		config.put(Date.class, dateFormatSerializer);
		config.put(Boolean.class, new ObjectSerializer() {
			@Override
			public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type) throws IOException {
				if(o == null) {
					if(jsonSerializer.isEnabled(SerializerFeature.WriteNullBooleanAsFalse)) {
						jsonSerializer.write(0);
					} else {
						jsonSerializer.writeNull();
					}
				} else {
					Boolean b = (Boolean) o;
					jsonSerializer.write(b ? 1 : 0);
				}
			}
		});
		super.setObjectMapper(mapper);
	}

	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		outputMessage.getHeaders().set("If-Modified-Since", "0");
		outputMessage.getHeaders().set("expires", "0");
		if (object instanceof Result) {
			Result r = (Result) object;
			TreeMap<String, Object> result = new TreeMap<>();
			result.put("code", r.getCode() == null ? (r.isSuccess() ? Result.SUCCESS : Result.FAIL) : r.getCode());
			result.put("message", r.getError()== null ? (r.isSuccess() ? "操作成功" : "操作失败") :r.getError());
			result.put("data", r.get());
			result.put("sync_data", r.getExt(Result.SYNC_DATA));
			String key = CommonUtil.objectParseString(r.getExt("access_key"));
		 	key = key == null ? "" : key;



			//String data = JSONObject.toJSONString(result, config, SerializerFeature.SortField);
			String data = JsonUtils.toJson(result);
			data=data.replace("null","");
			//['"',"\\","[","]","{","}",'null'
			data=data.replaceAll("[\"\\\\\\[\\]\\{\\}]","");
			String sign=CommonUtil.signForData(key,data);
			result.put("sign",sign);
			log.debug("result: {}", data);
			super.writeInternal(result, outputMessage);
			return;
		}
		super.writeInternal(object, outputMessage);
	}
}
