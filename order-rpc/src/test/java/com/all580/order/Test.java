package com.all580.order;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.model.Base64TopicMessage;
import com.all580.order.api.OrderConstant;
import com.all580.order.entity.Group;
import com.all580.order.entity.Order;
import com.all580.product.api.consts.ProductRules;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.framework.common.lang.Arith;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.codec.Md5Utils;
import com.framework.common.lang.codec.TranscodeUtil;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.apache.commons.lang.ArrayUtils;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;
import java.math.BigDecimal;
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

        System.out.println(Md5Utils.getMD5For16("T_1476277250138WFBZ35GTM7PL25").toUpperCase());
        System.out.println(Md5Utils.getMD5For16("T_1478327104821BSA8LKK93VV4GQ").toUpperCase());
        System.out.println(Md5Utils.getMD5For16("D_1476277250138WFBZ35GTM7PL25").toUpperCase());

        Map<String, String> data1 = new TreeMap<>();
        data1.put("access_id", "1476277249859N2T3JBGA");
        data1.put("ep_id", "1");
        data1.put("supplier_core_ep_id", "1139");
        System.out.println(CommonUtil.signForData("1476277250138WFBZ35GTM7PL25", JsonUtils.toJson(data1)));

        Map rule = ProductRules.calcRefund("{\"all\":\"0\",\"rule\":[{\"after\":\"\",\"before\":{\"day\":\"-3\",\"time\":\"11:00\"},\"fixed\":\"\",\"percent\":\"1\",\"type\":\"5072\"},{\"after\":{\"day\":\"-3\",\"time\":\"11:00\"},\"before\":{\"day\":\"-2\",\"time\":\"10:00\"},\"fixed\":\"\",\"percent\":\"2\",\"type\":\"5072\"},{\"after\":{\"day\":\"-3\",\"time\":\"11:00\"},\"before\":{\"day\":\"-2\",\"time\":\"10:00\"},\"fixed\":\"\",\"percent\":\"2\",\"type\":\"5072\"},{\"after\":{\"day\":\"-2\",\"time\":\"10:00\"},\"before\":{\"day\":\"-1\",\"time\":\"09:00\"},\"fixed\":\"\",\"percent\":\"3\",\"type\":\"5072\"},{\"after\":{\"day\":\"-1\",\"time\":\"09:00\"},\"before\":{\"day\":\"0\",\"time\":\"08:00\"},\"fixed\":100,\"percent\":\"\",\"type\":\"5071\"},{\"after\":{\"day\":\"0\",\"time\":\"08:00\"},\"before\":{\"day\":\"1\",\"time\":\"12:00\"},\"fixed\":200,\"percent\":\"\",\"type\":\"5071\"},{\"after\":{\"day\":\"1\",\"time\":\"12:00\"},\"before\":{\"day\":\"2\",\"time\":\"17:00\"},\"fixed\":300,\"percent\":\"\",\"type\":\"5071\"},{\"after\":{\"day\":\"2\",\"time\":\"17:00\"},\"before\":{\"day\":\"3\",\"time\":\"18:00\"},\"fixed\":400,\"percent\":\"\",\"type\":\"5071\"},{\"after\":{\"day\":\"3\",\"time\":\"18:00\"},\"before\":\"\",\"fixed\":\"5\",\"percent\":\"100\",\"type\":\"5072\"}]}",
                DateFormatUtils.converToDateTime("2016-11-09 00:00:00"), DateFormatUtils.converToDateTime("2016-11-09 16:30:00"));
        System.out.println(rule);

        double percent = new BigDecimal(200 / (double)1500).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        System.out.println(percent);
        System.out.println(new BigDecimal(1000 * 1 * (1 - percent)));
        System.out.println(500 * 1 * (1 - percent));

        percent = new BigDecimal(13/100.0).setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
        System.out.println(percent);
        System.out.println(1000 * 1 * (1 - percent));

        System.out.println(Arith.round(Arith.mul(1000, 1 - Arith.div(200, 1500, 4)), 0));
        System.out.println(Arith.round(Arith.mul(500, 1 - Arith.div(200, 1500, 4)), 0));

        System.out.println(Arith.round(Arith.mul(1000, Arith.div(200, 1500, 4)), 0));
        System.out.println(Arith.round(Arith.mul(500, Arith.div(200, 1500, 4)), 0));

        Map params = JsonUtils.fromJson("{\"success\":false,\"errMsg\":null,\"orderSn\":\"1479093717010180\",\"procTime\":\"2016-11-14 11:23:38\",\"status\":1,\"data\":[{\"visitorSeqId\":\"234\",\"imageUrl\":\"http://m8e.cm/test\",\"voucherNumber\":\"1d7cb88188957054\",\"ticketId\":\"47420a667f42e254\",\"maProductId\":null,\"sid\":\"511702197407135024\",\"phone\":\"15019418143\"}]}", HashMap.class);
        System.out.println(params);
        validateParams(params);

        int[] a = {
                OrderConstant.OrderItemStatus.SEND,
                OrderConstant.OrderItemStatus.NON_SEND,
                OrderConstant.OrderItemStatus.TICKET_FAIL
        };
        Arrays.sort(a);
        System.out.println(Arrays.binarySearch(a, 325));
        System.out.println(ArrayUtils.indexOf(a, 325));

        Base64TopicMessage base64TopicMessage = new Base64TopicMessage();
        Map<String, Object> base64Map = new HashMap<>();
        base64Map.put("action", "mnsBalanceChangeAction");
        base64Map.put("content", "1111");
        base64Map.put("createTime", new Date());
        base64TopicMessage.setBaseMessageBody(JsonUtils.toJson(base64Map));
        System.out.println(base64TopicMessage.getMessageBodyAsBase64());


        Map test1 = new HashMap();
        test1.put("access_id", "14789195271477R2MCC3X");
        test1.put("adult", "10");
        test1.put("area", "430104");
        test1.put("city", "430100");
        test1.put("ep_id", "1139");
        test1.put("guide_card", "");
        test1.put("guide_name", "周老板");
        test1.put("guide_phone", "18711154335");
        test1.put("guide_sid", "360321199504152034");
        test1.put("kid", "5");
        test1.put("manager_name", "周老板");
        test1.put("manager_phone", "");
        test1.put("number", "TNT2016");
        test1.put("operator_id", "42");
        test1.put("operator_name", "老汉推车");
        test1.put("province", "430000");
        test1.put("sign", "0e66e0402de0cde9b632d026f9c83e8a");
        test1.put("start_date", "2016-12-05");
        test1.put("tour_name", "邹老板");
        test1.put("tour_phone", "");
        test1.put("travel_name", "畅旅啊");
        Group group = JsonUtils.map2obj(test1, Group.class, "yyyy-MM-dd");
        System.out.println(group);
        group.setId(null);
    }
    public static void validateParams(Map params) {
        Map<String[], ValidRule[]> validRuleMap = new HashMap<>();
        validRuleMap.put(new String[]{
                "orderSn",
                "procTime",
                "data.visitorSeqId",
                "data.imageUrl",
                "data.voucherNumber",
                "data.ticketId",
                "data.phone"
        }, new ValidRule[]{new ValidRule.NotNull()});
        validRuleMap.put(new String[]{"procTime"}, new ValidRule[]{new ValidRule.Date()});
        validRuleMap.put(new String[]{"orderSn", "visitorSeqId"}, new ValidRule[]{new ValidRule.Digits()});
        ParamsMapValidate.validate(params, validRuleMap);
    }
}
