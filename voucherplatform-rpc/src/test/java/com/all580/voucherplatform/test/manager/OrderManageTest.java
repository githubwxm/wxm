package com.all580.voucherplatform.test.manager;

import com.all580.voucherplatform.manager.CreateOrderManager;
import com.framework.common.lang.DateFormatUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Linv2 on 2017-05-25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration(defaultRollback = false)
public class OrderManageTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testCreateOrder() throws Exception {
        CreateOrderManager manager = applicationContext.getBean(CreateOrderManager.class);
        Map map = new HashMap();
        map.put("orderId", UUID.randomUUID().toString());
        map.put("prodId", "111111");
        map.put("validTime", DateFormatUtils.converToStringTime(new Date()));
        map.put("invalidTime", DateFormatUtils.converToStringTime(DateFormatUtils.addDay(new Date(), 7)));
        Map visitorMap = new HashMap();
        visitorMap.put("seqId", UUID.randomUUID().toString());
        visitorMap.put("customName", "Linv2");
        visitorMap.put("mobile", "18773112193");
        visitorMap.put("idNumber", "411381198706292251");
        visitorMap.put("number", "1");
        manager.setProd(1, map);
        manager.setVisitor(visitorMap);
        manager.saveOrder();

    }
}
