package com.all580.voucherplatform.test;

import com.all580.voucherplatform.service.SupplyServiceImpl;
import com.framework.common.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-05-22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration
public class SupplyTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    SupplyServiceImpl supplyService;

    @Test
    public void testCreate() {
        Map map = new HashMap<String, Object>();
        map.put("name", "湖南畅旅");
        map.put("address", "新长海");
        map.put("region", "长沙");
        map.put("description", "专业智慧旅游三十年");
        map.put("signTye", 1);
        Result result = supplyService.create(map);
        Assert.assertTrue(result.isSuccess());
    }
}
