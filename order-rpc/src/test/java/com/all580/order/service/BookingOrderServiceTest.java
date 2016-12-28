package com.all580.order.service;

import com.all580.order.api.service.BookingOrderService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItemAccount;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/9/28 10:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback=true)
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
    public void createTest() {
        System.out.println(bookingOrderService);
        bookingOrderService.payment(new HashMap<String, Object>(){{
            put("name", "Alone");
            put("phone", "15019418143");
            put("sid", "15019418143");
        }});
    }

    @Test
    public void split() {
        int quantity = 1;
        ProductSearchParams searchParams = new ProductSearchParams();
        searchParams.setSubProductId(80);
        Date bookingDate = DateFormatUtils.converToDateTime("2016-12-14 00:00:00");
        searchParams.setStartDate(bookingDate);
        searchParams.setDays(1);
        searchParams.setQuantity(quantity);
        searchParams.setBuyEpId(1141);
        Result<ProductSalesInfo> salesInfoResult = productSalesPlanRPCService.validateProductSalesInfo(searchParams);
        if (!salesInfoResult.isSuccess()) {
            throw new ApiException(salesInfoResult.getError());
        }
        ProductSalesInfo salesInfo = salesInfoResult.get();
        // 每天的价格
        List<List<EpSalesInfo>> allDaysSales = salesInfo.getSales();
        bookingOrderManager.calcSalesPrice(allDaysSales, salesInfo, 1141, quantity, 350);

        List<OrderItemAccount> list = bookingOrderManager.preSplitAccount(allDaysSales, 999999, quantity, salesInfo.getPay_type(), bookingDate);
        System.out.println(JsonUtils.toJson(list));
        bookingOrderManager.consumeOrReConsumeSplitAccount(list, 5012, bookingDate, quantity, "ssssss", true, true);
        throw new ApiException("hahhah");
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
