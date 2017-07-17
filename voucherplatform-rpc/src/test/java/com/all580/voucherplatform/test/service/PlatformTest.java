package com.all580.voucherplatform.test.service;

import com.all580.voucherplatform.api.service.PlatformService;
import com.framework.common.Result;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-05-25.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration(defaultRollback = false)
public class PlatformTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    PlatformService platformService;

    @Test
    public void testCreate() {

        Map map = new HashMap();
        map.put("name", "湖南畅旅");
        map.put("description", "专业智慧旅游三十年");
        map.put("signType", 1);
        Result result = platformService.create(map);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdate() {

        Map src = platformService.getPlatform(1);
        Map map = new HashMap();
        map.put("id", 1);
        map.put("name", "湖南畅旅2");
        map.put("description", "专业智慧旅游三十年2");
        map.put("signType", 2);
        Result result = platformService.update(map);
        Map dist = platformService.getPlatform(1);
        Assert.assertTrue(dist.get("name").equals(src.get("name")));
    }

    @Test
    public void testCreateRole() {
        Result result = platformService.createRole(1, 2);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testAuth() {
        Map map = new HashMap();
        map.put("authId", "d951e49c-9f9f-46f9-b806-64dc3c627092");
        map.put("authKey", "4a105ffb-eb1d-4cab-b48a-d2618b87f81d");
        map.put("code", "1");
        map.put("name", "成人票");
        Result result = platformService.auth(map);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void getPlatform() {
        Map map = platformService.getPlatform(1);
        Assert.assertTrue(map.get("id").equals(1));
    }

    @Test
    public void testAddProd() {
        Map map = new HashMap();
        map.put("code", "1");
        map.put("name", "成人票");
        Result result = platformService.setProd(1, map);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testProdUpdate() {
        Map map = new HashMap();
        map.put("code", "1");
        map.put("name", "成人票2");
        Result result = platformService.setProd(1, map);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testgetProdByPlatform() {
        Result result = platformService.getProdByPlatform(1, "1");
        Map map = (Map) result.getResult();
        Assert.assertTrue(!StringUtils.isEmpty(String.valueOf(map.get("name"))));
    }

    @Test
    public void testGetProById() {
        Map map = platformService.getProd(1);
        Assert.assertTrue(!StringUtils.isEmpty(String.valueOf(map.get("name"))));
    }

//    @Test
//    public void testGetProdCount() {
//        int count = platformService.getProdCount("成人", null, null, null, null, null);
//        Assert.assertTrue(count > 0);
//    }
//
//    @Test
//    public void testGetProdList() {
//        List<Map> list = platformService.getProdList("成人", null, null, null, null, null, 0, 1);
//        Assert.assertTrue(list.size() > 0);
//    }
}
