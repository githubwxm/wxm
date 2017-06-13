package com.all580.voucherplatform.test.service;

import com.all580.voucherplatform.api.service.All580Service;
import com.all580.voucherplatform.api.service.PlatformService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration(defaultRollback = false)
public class All580Test {

    @Autowired
    All580Service all580Service;

    @Test
    public void TestBindMember() {
        Map subMap = new HashMap();
        subMap.put("mId", "d951e49c-9f9f-46f9-b806-64dc3c627092");
        subMap.put("mKey", "4a105ffb-eb1d-4cab-b48a-d2618b87f81d");
        subMap.put("otaMerchantId", "123");
        Map map = new HashMap();
        map.put("action", "bindMerchant");
        map.put("identity", "1");
        map.put("content", JsonUtils.toJson(subMap));
        Result result = all580Service.process(map);
        System.out.println(JsonUtils.toJson(result));
    }

    @Test
    public void TestQueryTicketProd() {
        Map subMap = new HashMap();
        subMap.put("mId", "d951e49c-9f9f-46f9-b806-64dc3c627092");
        Map map = new HashMap();
        map.put("action", "queryTicketProduct");
        map.put("identity", "1");
        map.put("content", JsonUtils.toJson(subMap));
        Result result = all580Service.process(map);
        System.out.println(JsonUtils.toJson(result));
    }

    @Test
    public void TestImportProd() {
        Map subMap = new HashMap();
        subMap.put("mId", "d951e49c-9f9f-46f9-b806-64dc3c627092");
        subMap.put("productId", "234");
        subMap.put("productName", "单元测试票");
        subMap.put("ticketProductId", "1");
        Map map = new HashMap();
        map.put("action", "importProduct");
        map.put("identity", "1");
        map.put("content", JsonUtils.toJson(subMap));
        Result result = all580Service.process(map);
        System.out.println(JsonUtils.toJson(result));
    }
}
