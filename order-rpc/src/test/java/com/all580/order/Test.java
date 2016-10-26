package com.all580.order;

import com.all580.order.entity.Order;
import com.framework.common.lang.JsonUtils;

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
        List<List<Object>> oneOrder = new ArrayList<>();
        List<Object> orderFieldValues = new ArrayList<>();
        orderFieldValues.add(order);
        orderFieldValues.add(order2);
        oneOrder.add(orderFieldValues);
        data.put("t_order", orderFieldValues);
        System.out.println(JsonUtils.toJson(data));
    }
}
