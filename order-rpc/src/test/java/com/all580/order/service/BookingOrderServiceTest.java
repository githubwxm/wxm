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
    private RefundOrderService refundOrderService;

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private BookingOrderManager bookingOrderManager;

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testRefundPackageOrder() throws Exception{
        Map params = new HashMap();
        params.put("order_item_sn","1500965139090360");
        params.put("apply_from", 351);
        params.put("ep_id", "24");
        params.put("core_ep_id", "1");
        params.put("operator_id", "71");

        Result rseult = refundOrderService.refundApplyForPackage(params);
        System.out.println("---->" + JsonUtils.toJson(rseult.get()));
    }

    @Test
    @Repeat(5)
    @Transactional(rollbackFor = Exception.class)
    public void testCreatePackageOrder() throws Exception{
        String str = "{\"access_id\":\"1476277249859N2T3JBGA\",\"days\":\"1\",\"ep_id\":\"34\",\"items\":[{\"amount\":\"1\",\"days\":\"1\",\"id\":\"98338\",\"message\":\"\",\"name\":\"订单改版测试产品-普通票-散客-在线支付\",\"product_name\":\"订单改版测试产品\",\"product_sub_code\":\"1499220704555281\",\"product_type\":\"5101\",\"quantity\":\"1\",\"require_sid\":\"5110\",\"send_msg\":\"\",\"sid_day_count\":\"0\",\"sid_day_quantity\":\"5\",\"start\":\"2017-08-15 00:00:00\",\"visitor\":[{\"name\":\"测试\",\"nameMessage\":\"\",\"phone\":\"13574177622\",\"phoneMessage\":\"\",\"quantity\":1,\"sid\":\"43072519900301806X\",\"sidMessage\":\"\",\"sidType\":true}]},{\"amount\":\"2\",\"days\":\"1\",\"id\":\"98399\",\"message\":\"\",\"name\":\"套票测试1-普通票-散客-在线支付\",\"product_name\":\"订单改版测试产品\",\"product_sub_code\":\"1502500633912601\",\"product_type\":\"5101\",\"quantity\":\"2\",\"require_sid\":\"5111\",\"send_msg\":\"\",\"sid_day_count\":\"0\",\"sid_day_quantity\":\"0\",\"start\":\"2017-08-16 00:00:00\",\"visitor\":[{\"name\":\"测试\",\"nameMessage\":\"\",\"phone\":\"13574177622\",\"phoneMessage\":\"\",\"quantity\":\"2\",\"sid\":\"430726199109273727\",\"sidMessage\":\"\",\"sidType\":true}]},{\"amount\":\"1\",\"days\":\"1\",\"id\":\"98400\",\"message\":\"\",\"name\":\"测试套票2-普通票-散客-在线支付\",\"product_name\":\"订单改版测试产品\",\"product_sub_code\":\"1502500722662601\",\"product_type\":\"5101\",\"quantity\":\"1\",\"require_sid\":\"5111\",\"send_msg\":\"\",\"sid_day_count\":\"0\",\"sid_day_quantity\":\"0\",\"start\":\"2017-08-17 00:00:00\",\"visitor\":[{\"name\":\"测试\",\"nameMessage\":\"\",\"phone\":\"13574177622\",\"phoneMessage\":\"\",\"quantity\":\"1\",\"sid\":\"43072319820310001X\",\"sidMessage\":\"\",\"sidType\":true}]}],\"operator_id\":\"535731\",\"operator_name\":\"小布\",\"outer_id\":\"\",\"product_sub_code\":\"1502501431451601\",\"quantity\":1,\"remark\":\"\",\"sale_amount\":\"\",\"shipping\":{\"name\":\"测试\",\"phone\":\"13574177622\",\"sid\":\"\"},\"sign\":\"e42ccc6a32a216775c1c3b952f3b2470\",\"start\":\"2017-08-14 00:00:00\",\"core_ep_id\":1}";
        Map params = JsonUtils.json2Map(str);
        params.put("from", OrderConstant.FromType.NON_TRUST);
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
