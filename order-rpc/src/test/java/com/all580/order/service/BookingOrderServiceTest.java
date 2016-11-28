package com.all580.order.service;

import com.all580.order.api.service.BookingOrderService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.framework.common.lang.UUIDGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/9/28 10:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
public class BookingOrderServiceTest {
    @Resource
    private BookingOrderService bookingOrderService;

    @Resource
    private OrderMapper orderMapper;

    @Test
    public void createTest() {
        System.out.println(bookingOrderService);
        bookingOrderService.payment(new HashMap<String, Object>(){{
            put("name", "Alone");
            put("phone", "15019418143");
            put("sid", "15019418143");
        }});
    }

    @Test
    public void test() {
        Order order = new Order();
        order.setId(157);
        order.setRemark("123");
        order.setUpdate_time("2016-10-31 14:49:58.000000");
        orderMapper.updateByPrimaryKeySelective(order);
        System.out.println(order.getUpdate_time());
    }
}
