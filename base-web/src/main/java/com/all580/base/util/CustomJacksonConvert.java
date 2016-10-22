package com.all580.base.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.common.Result;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomJacksonConvert extends MappingJackson2HttpMessageConverter {
	public CustomJacksonConvert() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
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
			result.put("message", r.getError());
			result.put("data", r.get());
			result.put("sign", "");
			super.writeInternal(result, outputMessage);
			return;
		}
		super.writeInternal(object, outputMessage);
	}
}
