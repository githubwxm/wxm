package com.all580.voucherplatform.test.service;

import com.all580.voucherplatform.api.service.SupplyService;
import com.framework.common.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-05-22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration(defaultRollback = false)
public class SupplyTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    SupplyService supplyService;

    @Test
    public void testCreate() {
        Map map = new HashMap<String, Object>();
        map.put("name", "湖南畅旅");
        map.put("phone", "13888888888");
        map.put("address", "新长海");
        map.put("region", "长沙");
        map.put("description", "专业智慧旅游三十年");
        map.put("signType", 1);
        Result result = supplyService.create(map);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testGetSupply() {
        Result result = supplyService.getSupply(2);
        System.out.println(result.getOutPutMap().get("name"));
        Assert.assertTrue(result.getResult() != null);
    }

    @Test
    public void testUpdate() {
        Map map = new HashMap<String, Object>();
        map.put("id", 2);
        map.put("name", "湖南畅旅2");
        map.put("phone", "138888888882");
        map.put("address", "新长海2");
        map.put("region", "长沙2");
        map.put("description", "专业智慧旅游三十年2");
        Result result = supplyService.update(map);
    }

    @Test
    public void testUpdateConf() {
        Map map = new HashMap<String, Object>();
        map.put("id", 2);
        map.put("conf", "{a:1}");
        Result result = supplyService.updateConf(map);

    }

    @Test
    public void testTicketSys() {
        Map map = new HashMap<String, Object>();
        map.put("id", 2);
        map.put("ticketsysId", 5);
        Result result = supplyService.updateTicketSys(map);

    }

    @Test
    public void testSetProd() {
        Map dataMap = new HashMap();
        dataMap.put("a", 1);
        Map map = new HashMap();
        map.put("code", "1");
        map.put("name", "成人票");
        map.put("description", "成人票描述");
        // map.put("data", dataMap);
        supplyService.setProd(2, map);
    }

    @Test
    public void testSetProd2() {
        Map map = new HashMap();
        map.put("code", "1");
        map.put("name", "成人票2");
        map.put("description", "成人票描述");
        supplyService.setProd(2, map);
    }

    @Test
    public void testDelProd2() {
        supplyService.delProd(2, "1");

    }

    @Test
    public void testBatchSetProd() {
        Map map = new HashMap();
        map.put("code", "1");
        map.put("name", "成人票");
        map.put("description", "成人票描述");
        Map map2 = new HashMap();
        map2.put("code", "2");
        map2.put("name", "儿童票");
        map2.put("description", "儿童票描述");
        List<Map> list = new ArrayList<>();
        list.add(map);
        list.add(map2);
        // map.put("data", dataMap);
        supplyService.setProd(2, list);
    }
}
