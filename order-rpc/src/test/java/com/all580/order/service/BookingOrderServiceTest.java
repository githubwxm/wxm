package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.api.service.RefundOrderService;
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
import java.util.*;

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
    private RefundOrderService refundOrderService;

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private BookingOrderManager bookingOrderManager;

    @Test
    //@Repeat(3)
    @Transactional(rollbackFor = Exception.class)
    public void testCreatePackageOrder1() throws Exception{
        String str = "{\"access_id\":\"1476277249859N2T3JBGA\",\"ep_id\":\"24\",\"items\":[{\"days\":\"1\",\"items\":[{\"amount\":\"1\",\"days\":\"1\"," +
                "\"id\":\"98358\",\"message\":\"\",\"name\":\"测试-普通票-散客-在线支付-全人工\",\"product_name\":\"订单改版测试\"," +
                "\"product_sub_code\":\"1499841934558701\",\"product_type\":\"5101\",\"quantity\":\"1\",\"require_sid\":\"5111\",\"send_msg\":\"\"," +
                "\"sid_day_count\":\"0\",\"sid_day_quantity\":\"0\",\"start\":\"2017-08-18 00:00:00\",\"visitor\":[{\"name\":\"测试\",\"nameMessage\":\"\"," +
                "\"phone\":\"18711066023\",\"phoneMessage\":\"\",\"quantity\":\"1\",\"sid\":\"43072519900301806X\",\"sidMessage\":\"\",\"sidType\":true}]}," +
                "{\"amount\":\"1\",\"days\":\"1\",\"id\":\"98402\",\"message\":\"\",\"name\":\"大发发-普通票-散客-在线支付\",\"product_name\":\"萍乡武功山\"," +
                "\"product_sub_code\":\"1502847589476601\",\"product_type\":\"5101\",\"quantity\":\"1\",\"require_sid\":\"5111\",\"send_msg\":\"\"," +
                "\"sid_day_count\":\"0\",\"sid_day_quantity\":\"0\",\"start\":\"2017-08-18 00:00:00\",\"visitor\":[{\"name\":\"测试\",\"nameMessage\":\"\"," +
                "\"phone\":\"18711066023\",\"phoneMessage\":\"\",\"quantity\":\"1\",\"sid\":\"430726199109273727\",\"sidMessage\":\"\",\"sidType\":true}]}]," +
                "\"product_sub_code\":\"1502939709682170\",\"product_type\":\"5104\",\"start\":\"2017-08-18 00:00:00\",\"quantity\":1}],\"operator_id\":\"71\",\"operator_name\":\"细明王\",\"outer_id\":\"\",\"remark\":\"\"," +
                "\"sale_amount\":\"\",\"shipping\":{\"name\":\"测试\",\"phone\":\"18711066023\",\"sid\":\"\"},\"sign\":\"06a9965e781581bc05fda4e871d3e41f\"," +
                "\"core_ep_id\":1}";
        Map params = JsonUtils.json2Map(str);
        params.put("from", OrderConstant.FromType.NON_TRUST);
        System.out.println("params--->" + params);
        Result result = bookingOrderService.create(params, "PACKAGE");
        System.out.println("---->" + JsonUtils.toJson(result.get()));
    }

    @Test
    @Repeat(3)
    @Transactional(rollbackFor = Exception.class)
    public void testCreatePackageOrder() throws Exception{
        String str = "{\"access_id\":\"1476277249859N2T3JBGA\",\"ep_id\":\"34\",\"items\":[{\"days\":\"1\"," +
                "\"items\":[{\"amount\":\"1\",\"days\":\"1\",\"id\":\"98338\",\"message\":\"\"," +
                "\"name\":\"订单改版测试产品-普通票-散客-在线支付\",\"product_name\":\"订单改版测试产品\"," +
                "\"product_sub_code\":\"1499220704555281\",\"product_type\":\"5101\",\"quantity\":\"1\"," +
                "\"require_sid\":\"5110\",\"send_msg\":\"\",\"sid_day_count\":\"0\",\"sid_day_quantity\":\"5\"," +
                "\"start\":\"2017-08-18 00:00:00\",\"visitor\":[{\"name\":\"测试\",\"nameMessage\":\"\"," +
                "\"phone\":\"13574177622\",\"phoneMessage\":\"\",\"quantity\":1,\"sid\":\"43072519900301806X\"," +
                "\"sidMessage\":\"\",\"sidType\":true}]},{\"amount\":\"2\",\"days\":\"1\",\"id\":\"98399\"," +
                "\"message\":\"\",\"name\":\"套票测试1-普通票-散客-在线支付\",\"product_name\":\"订单改版测试产品\"," +
                "\"product_sub_code\":\"1502500633912601\",\"product_type\":\"5101\",\"quantity\":\"2\"," +
                "\"require_sid\":\"5111\",\"send_msg\":\"\",\"sid_day_count\":\"0\",\"sid_day_quantity\":\"0\"," +
                "\"start\":\"2017-08-19 00:00:00\",\"visitor\":[{\"name\":\"测试\",\"nameMessage\":\"\"," +
                "\"phone\":\"13574177622\",\"phoneMessage\":\"\",\"quantity\":\"2\",\"sid\":\"430726199109273727\"," +
                "\"sidMessage\":\"\",\"sidType\":true}]},{\"amount\":\"1\",\"days\":\"1\",\"id\":\"98400\"," +
                "\"message\":\"\",\"name\":\"测试套票2-普通票-散客-在线支付\",\"product_name\":\"订单改版测试产品\"," +
                "\"product_sub_code\":\"1502500722662601\",\"product_type\":\"5101\",\"quantity\":\"1\"," +
                "\"require_sid\":\"5111\",\"send_msg\":\"\",\"sid_day_count\":\"0\",\"sid_day_quantity\":\"0\"," +
                "\"start\":\"2017-08-20 00:00:00\",\"visitor\":[{\"name\":\"测试\",\"nameMessage\":\"\"," +
                "\"phone\":\"13574177622\",\"phoneMessage\":\"\",\"quantity\":\"1\",\"sid\":\"43072319820310001X\"," +
                "\"sidMessage\":\"\",\"sidType\":true}]}],\"product_sub_code\":\"1502501431451601\",\"product_type\":\"5104\",\"quantity\":1,\"remark\":\"\"," +
                "\"start\":\"2017-08-18 00:00:00\"}],\"operator_id\":\"535731\",\"operator_name\":\"小布\",\"outer_id\":\"\"," +
                "\"sale_amount\":\"\",\"shipping\":{\"name\":\"测试\",\"phone\":\"13574177622\",\"sid\":\"\"}," +
                "\"sign\":\"e42ccc6a32a216775c1c3b952f3b2470\",\"core_ep_id\":1,\"product_type\":\"5104\"}";
        Map params = JsonUtils.json2Map(str);
        params.put("from", OrderConstant.FromType.NON_TRUST);

        Result rseult = bookingOrderService.create(params, "PACKAGE");
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
