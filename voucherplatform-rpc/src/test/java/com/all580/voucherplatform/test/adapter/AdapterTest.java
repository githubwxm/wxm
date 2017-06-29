package com.all580.voucherplatform.test.adapter;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Created by Linv2 on 2017-06-20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration(defaultRollback = false)
public class AdapterTest {
    @Autowired
    private AdapterLoadder loadder;

    @Test
    public void TestQueryProductTicket() {
        SupplyAdapterService supplyAdapterService = loadder.getSupplyAdapterService(1);
        supplyAdapterService.queryProd(2);
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
        }
    }
}
