package com.all580.base.controller;

import com.all580.report.api.ReportConstant;
import com.all580.report.api.dto.OrderInfo;
import com.framework.common.lang.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by xiangzw on 2017/5/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class LineControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void testListGroup() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("product_name","product_name");
        params.put("number","1452563256");
        params.put("booking_start_date","2017-05-18");
        params.put("booking_end_date","2017-05-26");
        params.put("status","0");
        params.put("record_start","1");
        params.put("record_count","10");
        params.put("ep_id","1");
        mockMvc.perform(
                /*get("/api/order/line/group/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(params))*/
                post("/api/order/line/group/list").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void testGetLineGroupDetailByNumber() throws Exception{
        mockMvc.perform(
                get("/api/order/line/group/detail")
                        .param("number","0517201705312017")
                        .param("ep_id","1")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void testListOrderVisitor() throws Exception{
        mockMvc.perform(
                get("/api/order/line/visitor/list")
                        .param("number","0517201706012017")
                        .param("record_start","1")
                        .param("record_count","20")
                        .param("ep_id","1")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void testSetLineGroupGuide() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("number","0517201705312017");
        params.put("guide_name","测试o");
        params.put("guide_phone","13623623652");
        params.put("guide_card","156235656");
        params.put("guide_sid","0517201705312017");
        params.put("ep_id","1");
        mockMvc.perform(
                post("/api/order/line/guide/set")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(params))
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void testUpdateLineGroupStatus() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("number","0517201705312017");
        params.put("status","392");
        mockMvc.perform(
                post("/api/order/line/group_status/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(params))
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void testListOrderItems() throws Exception{
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setProductType(ReportConstant.ProductType.SCENERY);
        orderInfo.setOrderSn(1498029473012180L);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016,4,12);
        //orderInfo.setStart(calendar.getTime());
        //orderInfo.setEnd(new Date());
//        orderInfo.setEpId(24);
//        orderInfo.setEpType(10003);
//        orderInfo.setCoreEpId(1);
//        orderInfo.setStatus(new Integer[]{110});
//        orderInfo.setSid("1");
//        orderInfo.setName("测试");
//        orderInfo.setPhone("1");
//        orderInfo.setProductName("票");
//        orderInfo.setProductSubName("票");
//        orderInfo.setVoucherNumber("1");
        mockMvc.perform(
                get("/api/order/query/item/list")
                        .param("productType","5101")
                        .param("start","2017-06-27")
                        .param("end","2017-06-28")
                        .param("epType","10003")
                        .param("epId","24")
                        .param("coreEpId","1")
                        .param("record_start","0")
                        .param("record_count","20")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void testGetSceneryOrderItemList() throws Exception{
        mockMvc.perform(
                get("/api/order/query/scenery/list")
                        //.param("productType","5101")
                        .param("start","2017-06-27")
                        .param("end","2017-06-28")
                        .param("ep_type","10003")
                        .param("ep_id","24")
                        .param("core_ep_id","1")
                        .param("record_start","0")
                        .param("record_count","20")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void testGetSceneryOrderDetailByNumber() throws Exception{
        mockMvc.perform(
                get("/api/order/query/scenery/get_item_detail")
                        .param("orderSn","1498029473012180")
                        .param("ep_type","10003")
                        .param("ep_id","24")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void testGetOrderDetailByNumber() throws Exception{
        mockMvc.perform(
                get("/api/order/query/item/get_item_detail")
                        .param("orderSn","1498029473012180")
                        .param("ep_type","10003")
                        .param("ep_id","24")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void  testPreRefundOrder() throws Exception{
        mockMvc.perform(
                get("/api/order/query/item/pre_refund")
                        .param("itemSn","1498029473012180")
                        .param("ep_type","10003")
                        .param("ep_id","24")
                        //.param("core_ep_id","1")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void  testPreClearanceHotelInfo() throws Exception{
        mockMvc.perform(
                get("/api/order/query/hotel/pre_clearance")
                        .param("itemSn","1494323810924730")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }
}
