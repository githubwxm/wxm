package com.all580.order;

import com.alibaba.fastjson.JSON;
import com.all580.order.entity.Order;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.codec.TranscodeUtil;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/25 16:53
 */
public class Test {
    public static void main(String[] args) throws Exception {
        Map<String, List<Object>> data = new HashMap<>();
        Order order = new Order();
        order.setId(11);
        order.setRemark("111");
        order.setPayTime(new Date());
        Order order2 = new Order();
        order2.setId(22);
        order2.setRemark("222");
        List<Object> orderFieldValues = new ArrayList<>();
        orderFieldValues.add(order);
        orderFieldValues.add(order2);
        data.put("t_order", orderFieldValues);
        System.out.println(JsonUtils.toJson(data));

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Boolean.class, new JsonSerializer<Boolean>() {
            @Override
            public void serialize(Boolean aBoolean, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
                jsonGenerator.writeNumber(aBoolean ? 1 : 0);
            }
        });
        JsonUtils.OBJECT_MAPPER.registerModule(simpleModule);

        Map map = new HashMap();
        map.put("data", data);
        map.put("boolean", false);
        FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();
        String toBase64Str = TranscodeUtil.byteArrayToBase64Str(configuration.asByteArray(map));
        String base64 = TranscodeUtil.byteArrayToBase64Str(JSON.toJSONBytes(map));
        System.out.println(toBase64Str);
        System.out.println(base64);

        Object object = configuration.asObject(TranscodeUtil.base64StrToByteArray(toBase64Str));
        Object obj = JSON.parse(TranscodeUtil.base64StrToByteArray(base64));
        System.out.println(object);
        System.out.println(obj);
        System.out.println(JsonUtils.toJson(object));
        System.out.println(JsonUtils.toJson(obj));
    }
}
