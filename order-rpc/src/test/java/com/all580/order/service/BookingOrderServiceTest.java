package com.all580.order.service;

import com.all580.order.api.service.BookingOrderService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/9/28 10:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback=false)
@ContextConfiguration(locations = { "classpath:META-INF/spring/*.xml"})
public class BookingOrderServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Resource
    private BookingOrderService bookingOrderService;
    @Resource
    private ProductSalesPlanRPCService productSalesPlanRPCService;

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private BookingOrderManager bookingOrderManager;

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testCreatePackageOrder() throws Exception{
        Map params = new HashMap();
        Map ship = new HashMap();
        ship.put("name","ceshi");
        ship.put("sid","1231312312");
        ship.put("phone","13636362563");
        params.put("shipping",ship);
        List<Map> items = new ArrayList<>();

        Map item1 = new HashMap();
        item1.put("product_sub_code", 1499846578900701L);
        item1.put("product_type", 5101);
        item1.put("quantity", 2);
        List<Map> visitors = new ArrayList<>();
        Map visitor = new HashMap();
        visitor.put("name","ceshi");
        visitor.put("sid","1231312312");
        visitor.put("phone","13636362563");
        visitor.put("quantity","2");
        visitors.add(visitor);
        item1.put("visitor", visitors);
        item1.put("days", 1);
        item1.put("start", "2017-07-22 00:00:00");
        items.add(item1);

        Map item2 = new HashMap();
        item2.put("product_sub_code", 1499841934558701L);
        item2.put("product_type", 5101);
        item2.put("quantity", 2);
        item2.put("visitor", visitors);
        item2.put("days", 1);
        item2.put("start", "2017-07-22 00:00:00");
        items.add(item2);

//        Map item3 = new HashMap();
//        item3.put("product_sub_code", 1492589311726231L);
//        item3.put("product_type", 5101);
//        item3.put("quantity", 2);
//        item3.put("visitor", visitors);
//        item3.put("days", 1);
//        item3.put("start", "2017-07-19 00:00:00");
//        items.add(item3);

        params.put("items", items);
        params.put("from", 351);
        params.put("remark", "备注");
        params.put("ep_id", "24");
        params.put("core_ep_id", "1");
        params.put("operator_id", "71");

        params.put("product_sub_code", 1499744068564331L);
        params.put("quantity", 2);
        params.put("days", 1);
        params.put("start", "2017-07-22 00:00:00");

        Result rseult = bookingOrderService.createPackageOrder(params);
        System.out.println("---->" + JsonUtils.toJson(rseult.get()));
    }

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
