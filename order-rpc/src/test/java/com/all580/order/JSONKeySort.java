package com.all580.order;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.all580.order.entity.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javassist.bytecode.stackmap.TypeData;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/12/2 16:11
 */
public class JSONKeySort {
    public static void main(String[] args) throws JsonProcessingException {
        Map<String, Object> root = new HashMap<>();
        root.put("b", "b");
        root.put("d", null);
        root.put("a", "a");

        List<Map<String, Object>> kid1 = new ArrayList<>();
        Map<String, Object> kid1_1 = new HashMap<>();
        kid1_1.put("time", new Date());
        Order order = new Order();
        order.setNumber(1111L);
        order.setId(1111);
        order.setCreate_time(new Date());
        kid1_1.put("order", order);
        kid1.add(kid1_1);
        root.put("kid1", kid1);

        System.out.println(JSONObject.toJSONString(root, SerializerFeature.SortField));
        SerializeConfig config = new SerializeConfig();
        config.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        config.put(Timestamp.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        System.out.println(JSONObject.toJSONString(root, config, SerializerFeature.SortField));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        System.out.println(mapper.writeValueAsString(root));
        System.out.println(TypeData.ClassName.NullType.class);
    }
}
