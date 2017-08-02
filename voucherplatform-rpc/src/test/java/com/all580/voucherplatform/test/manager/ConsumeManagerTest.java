package com.all580.voucherplatform.test.manager;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.manager.order.ConsumeOrderManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Linv2 on 2017-07-04.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration(defaultRollback = false)
public class ConsumeManagerTest {


    @Autowired
    AdapterLoader adapterLoader;

    @Test
    public void TestConsume() {
        ConsumeOrderManager consumeOrderManager = adapterLoader.getBean(ConsumeOrderManager.class);
        try {
            consumeOrderManager.setOrder("1499133036679250");
            consumeOrderManager.submitConsume(UUID.randomUUID().toString(), 1, "Address", new Date(), "0123");
            Thread.sleep(20000);
        } catch (Exception ex) {
            throw new ApiException(ex);
        }

    }

}
