package com.all580.order;

import com.alibaba.fastjson.JSON;
import com.all580.order.entity.Order;
import com.all580.product.api.consts.ProductRules;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.codec.Md5Utils;
import com.framework.common.lang.codec.TranscodeUtil;
import com.framework.common.util.CommonUtil;
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

        System.out.println(Md5Utils.getMD5For16("1476425996329CJCJ6VXG3YMCNB").toUpperCase());

        Map<String, String> data1 = new TreeMap<>();
        data1.put("access_id", "1476277249859N2T3JBGA");
        data1.put("ep_id", "1");
        data1.put("supplier_core_ep_id", "30");
        System.out.println(CommonUtil.signForData("1476277250138WFBZ35GTM7PL25", JsonUtils.toJson(data1)));

        Map rule = ProductRules.calcRefund("{\"all\":\"0\",\"rule\":[{\"after\":\"\",\"before\":{\"day\":\"-3\",\"time\":\"11:00\"},\"fixed\":\"\",\"percent\":\"1\",\"type\":\"5072\"},{\"after\":{\"day\":\"-3\",\"time\":\"11:00\"},\"before\":{\"day\":\"-2\",\"time\":\"10:00\"},\"fixed\":\"\",\"percent\":\"2\",\"type\":\"5072\"},{\"after\":{\"day\":\"-3\",\"time\":\"11:00\"},\"before\":{\"day\":\"-2\",\"time\":\"10:00\"},\"fixed\":\"\",\"percent\":\"2\",\"type\":\"5072\"},{\"after\":{\"day\":\"-2\",\"time\":\"10:00\"},\"before\":{\"day\":\"-1\",\"time\":\"09:00\"},\"fixed\":\"\",\"percent\":\"3\",\"type\":\"5072\"},{\"after\":{\"day\":\"-1\",\"time\":\"09:00\"},\"before\":{\"day\":\"0\",\"time\":\"08:00\"},\"fixed\":100,\"percent\":\"\",\"type\":\"5071\"},{\"after\":{\"day\":\"0\",\"time\":\"08:00\"},\"before\":{\"day\":\"1\",\"time\":\"12:00\"},\"fixed\":200,\"percent\":\"\",\"type\":\"5071\"},{\"after\":{\"day\":\"1\",\"time\":\"12:00\"},\"before\":{\"day\":\"2\",\"time\":\"17:00\"},\"fixed\":300,\"percent\":\"\",\"type\":\"5071\"},{\"after\":{\"day\":\"2\",\"time\":\"17:00\"},\"before\":{\"day\":\"3\",\"time\":\"18:00\"},\"fixed\":400,\"percent\":\"\",\"type\":\"5071\"},{\"after\":{\"day\":\"3\",\"time\":\"18:00\"},\"before\":\"\",\"fixed\":\"5\",\"percent\":\"100\",\"type\":\"5072\"}]}",
                DateFormatUtils.converToDateTime("2016-11-09 00:00:00"), DateFormatUtils.converToDateTime("2016-11-09 16:30:00"));
        System.out.println(rule);
    }
}
