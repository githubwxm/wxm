package com.all580.order;

import com.all580.order.entity.Order;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.codec.TranscodeUtil;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/25 16:53
 */
public class Test {
    public static void main(String[] args) throws ParseException {
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
        map.put("data", "{\"buy_ep_id\":54,\"buy_operator_id\":1,\"buy_operator_name\":\"xxx\"}");
        map.put("boolean", false);
        FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();
        String toBase64Str = TranscodeUtil.byteArrayToBase64Str(configuration.asByteArray(map));
        System.out.println(toBase64Str);
        String data2 = "{\"data\":{\"t_order_item\":[{\"id\":81,\"number\":1477640163480070,\"order_id\":125,\"pro_sub_id\":1,\"pro_name\":\"test1\",\"pro_sub_name\":\"成人票\",\"start\":\"2016-10-29 00:00:00.000\",\"end\":\"2016-10-29 23:59:59.059\",\"days\":1,\"quantity\":1,\"refund_quantity\":0,\"status\":321,\"group_id\":0,\"payment_flag\":5011,\"sale_amount\":40,\"supplier_ep_id\":31,\"ep_ma_id\":1,\"update_time\":\"2016-10-28 15:23:44.044\"}],\"t_order\":[{\"id\":125,\"number\":1477640163181070,\"pay_amount\":40,\"status\":311,\"buy_ep_id\":54,\"buy_operator_id\":1,\"buy_operator_name\":\"xxx\",\"payee_ep_id\":1,\"create_time\":\"2016-10-28 15:36:03.003\",\"sale_amount\":40,\"from_type\":351,\"remark\":\"test\",\"update_time\":\"2016-10-28 15:23:45.045\"}],\"t_visitor\":[{\"id\":83,\"ref_id\":80,\"phone\":\"15019418143\",\"name\":\"Alone\",\"sid\":\"210905197807210546\",\"quantity\":1,\"pre_return\":0,\"return_quantity\":0,\"use_quantity\":0,\"update_time\":\"2016-10-28 15:23:44.044\"}],\"t_order_item_detail\":[{\"id\":80,\"order_item_id\":81,\"day\":\"2016-10-29 00:00:00.000\",\"quantity\":1,\"used_quantity\":0,\"refund_quantity\":0,\"cust_refund_rule\":\"{\\\"all\\\":false,\\\"rule\\\":[{\\\"before\\\":{\\\"day\\\":-1,\\\"time\\\":\\\"12:00\\\"},\\\"fixed\\\":0,\\\"percent\\\":10,\\\"type\\\":5072},{\\\"before\\\":{\\\"day\\\":0,\\\"time\\\":\\\"08:00\\\"},\\\"after\\\":{\\\"day\\\":-1,\\\"time\\\":\\\"12:00\\\"},\\\"fixed\\\":0,\\\"percent\\\":20,\\\"type\\\":5072},{\\\"before\\\":{\\\"day\\\":0,\\\"time\\\":\\\"08:00\\\"},\\\"fixed\\\":0,\\\"percent\\\":30,\\\"type\\\":5072}]}\",\"saler_refund_rule\":\"{\\\"all\\\":false,\\\"rule\\\":[{\\\"before\\\":{\\\"day\\\":-1,\\\"time\\\":\\\"12:00\\\"},\\\"fixed\\\":0,\\\"percent\\\":10,\\\"type\\\":5072},{\\\"before\\\":{\\\"day\\\":0,\\\"time\\\":\\\"08:00\\\"},\\\"after\\\":{\\\"day\\\":-1,\\\"time\\\":\\\"12:00\\\"},\\\"fixed\\\":0,\\\"percent\\\":20,\\\"type\\\":5072},{\\\"before\\\":{\\\"day\\\":0,\\\"time\\\":\\\"08:00\\\"},\\\"fixed\\\":0,\\\"percent\\\":30,\\\"type\\\":5072}]}\",\"oversell\":false,\"use_hours_limit\":0,\"expiry_date\":\"2016-10-29 23:59:59.059\",\"effective_date\":\"2016-10-29 00:00:00.000\",\"create_time\":\"2016-10-28 15:36:03.003\",\"update_time\":\"2016-10-28 15:23:44.044\"}],\"t_shipping\":[{\"id\":55,\"order_id\":125,\"name\":\"周先军\",\"phone\":\"15019418143\",\"update_time\":\"2016-10-28 15:23:45.045\"}],\"t_order_item_account\":[{\"id\":198,\"order_item_id\":81,\"money\":0,\"profit\":0,\"settled_money\":0,\"ep_id\":54,\"status\":330,\"core_ep_id\":1,\"data\":\"[{\\\"outPrice\\\":40,\\\"inPrice\\\":40,\\\"profit\\\":0,\\\"day\\\":\\\"2016-10-29 00:00:00\\\"}]\",\"update_time\":\"2016-10-28 15:23:45.045\"},{\"id\":199,\"order_item_id\":81,\"money\":30,\"profit\":30,\"settled_money\":0,\"ep_id\":31,\"status\":330,\"core_ep_id\":1,\"data\":\"[{\\\"outPrice\\\":30,\\\"inPrice\\\":0,\\\"profit\\\":30,\\\"day\\\":\\\"2016-10-29 00:00:00\\\"}]\",\"update_time\":\"2016-10-28 15:23:45.045\"},{\"id\":200,\"order_item_id\":81,\"money\":-30,\"profit\":10,\"settled_money\":0,\"ep_id\":1,\"status\":330,\"core_ep_id\":1,\"data\":\"[{\\\"outPrice\\\":40,\\\"inPrice\\\":30,\\\"profit\\\":10,\\\"day\\\":\\\"2016-10-29 00:00:00\\\"}]\",\"update_time\":\"2016-10-28 15:23:45.045\"}]},\"sign\":\"95c6a7a5a30312b4e794a2e7c090e709\"}";
        System.out.println(JsonUtils.toJson(map));

        Object object = configuration.asObject(TranscodeUtil.base64StrToByteArray(toBase64Str));
        System.out.println(object);
        System.out.println(JsonUtils.toJson(object));
    }
}
