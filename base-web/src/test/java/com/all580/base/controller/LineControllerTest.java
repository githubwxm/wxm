package com.all580.base.controller;

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
}
