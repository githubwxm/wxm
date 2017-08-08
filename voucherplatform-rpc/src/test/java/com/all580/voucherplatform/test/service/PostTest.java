package com.all580.voucherplatform.test.service;

import com.all580.voucherplatform.api.service.PosService;
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
import java.util.UUID;

/**
 * Created by Linv2 on 2017-07-24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
@TransactionConfiguration(defaultRollback = false)
public class PostTest {

    @Autowired
    private PosService posService;

    @Test
    public void TestPosApply() {
        Map map = new HashMap();
        map.put("code", "1234567890");
        Result result = posService.apply(map);
        System.out.println("----------------------------------------------------");
        System.out.println(JsonUtils.toJson(result));
    }

    @Test
    public void TestQueryPos() {
        Map map = new HashMap();
        map.put("code", "1234567890");
        Result result = posService.query(map);
        System.out.println("----------------------------------------------------");
        System.out.println(JsonUtils.toJson(result));
    }

    @Test
    public void TestQueryOrder() {

        Map map = new HashMap();
        map.put("voucher", "411381198706292251");
        Result result = posService.request(getRequestMap("query", map));
        System.out.println("----------------------------------------------------");
        System.out.println(JsonUtils.toJson(result));

    }

    @Test
    public void TestQueryConsumeOrder() {

        Map map = new HashMap();
        map.put("voucherId", "411381198706292251");
        map.put("seqId", UUID.randomUUID().toString());
        map.put("consumeNumber", 1);
        map.put("deviceId", "POS");
        Result result = posService.request(getRequestMap("consume", map));
        System.out.println("----------------------------------------------------");
        System.out.println(JsonUtils.toJson(result));

    }


    @Test
    public void TestQueryOrderDetail() {
        Map map = new HashMap();
        map.put("voucherId", "411381198706292251");
        Result result = posService.request(getRequestMap("detail", map));
        System.out.println("----------------------------------------------------");
        System.out.println(JsonUtils.toJson(result));

    }

    @Test
    public void TestReport() {
        Map map = new HashMap();
        map.put("startTime", "");
        map.put("endTime", "");
        Result result = posService.request(getRequestMap("report", map));
        System.out.println("----------------------------------------------------");
        System.out.println(JsonUtils.toJson(result));

    }

    @Test
    public void TestReportAll() {
        Map map = new HashMap();
        map.put("startTime", "");
        map.put("endTime", "");
        Result result = posService.request(getRequestMap("reportall", map));
        System.out.println("----------------------------------------------------");
        System.out.println(JsonUtils.toJson(result));

    }

    private Map getRequestMap(String action,
                              Map mapContent) {
        Map map = new HashMap();
        map.put("identity", "1234567890");
        map.put("action", action);
        map.put("signed", "1234567890");
        map.put("content", JsonUtils.toJson(mapContent));
        return map;
    }
}
