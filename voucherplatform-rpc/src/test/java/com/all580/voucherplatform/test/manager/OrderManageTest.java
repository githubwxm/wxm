package com.all580.voucherplatform.test.manager;

import com.all580.voucherplatform.manager.CreateOrderManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.HashMap;
import java.util.Map;

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
        manager.setProd(1, null);
    }
}

