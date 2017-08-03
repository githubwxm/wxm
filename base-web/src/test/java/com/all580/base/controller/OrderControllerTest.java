package com.all580.base.controller;

import com.all580.order.api.OrderConstant;
import com.all580.product.api.consts.ProductConstants;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/11 14:15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class OrderControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }


    @Test
    public void createPackageOrder() throws Exception{
        Map params = new HashMap();
        Map ship = new HashMap();
        ship.put("name","ceshi");
        ship.put("sid","1231312312");
        ship.put("phone","13636362563");
        params.put("shipping",ship);
        List<Map> items = new ArrayList<>();

        Map item1 = new HashMap();
        item1.put("product_sub_code", 1499846578900701L);
        item1.put("product_type", 5101);
        item1.put("quantity", 2);
        List<Map> visitors = new ArrayList<>();
        Map visitor = new HashMap();
        visitor.put("name","ceshi");
        visitor.put("sid","1231312312");
        visitor.put("phone","13636362563");
        visitor.put("quantity","2");
        visitors.add(visitor);
        item1.put("visitor", visitors);
        item1.put("days", 1);
        item1.put("start", "2017-07-27 00:00:00");
        items.add(item1);

        Map item2 = new HashMap();
        item2.put("product_sub_code", 1499841934558701L);
        item2.put("product_type", 5101);
        item2.put("quantity", 2);
        item2.put("visitor", visitors);
        item2.put("days", 1);
        item2.put("start", "2017-07-27 00:00:00");
        items.add(item2);

//        Map item3 = new HashMap();
//        item3.put("product_sub_code", 1492589311726231L);
//        item3.put("product_type", 5101);
//        item3.put("quantity", 2);
//        item3.put("visitor", visitors);
//        item3.put("days", 1);
//        item3.put("start", "2017-07-19 00:00:00");
//        items.add(item3);

        params.put("items", items);
        params.put("from", 351);
        params.put("remark", "备注");
        params.put("ep_id", "24");
        params.put("core_ep_id", "1");
        params.put("operator_id", "71");

        params.put("product_sub_code", 1499744068564331L);
        params.put("quantity", 2);
        params.put("days", 1);
        params.put("start", "2017-07-27 00:00:00");
        mockMvc.perform(
                post("/api/order/package/create").contentType(MediaType.APPLICATION_JSON).
                        content(JsonUtils.toJson(params))
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is(200))
        ).andDo(print());
    }

    @Test
    public void createEp()throws Exception{
        Map params = new HashMap();
        params.put("id",1);
        mockMvc.perform(
                post("/api/ep/platform/status/disable").contentType(MediaType.APPLICATION_JSON).
                        content(JsonUtils.toJson(params))
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is(200))
        ).andDo(print());
    }
    @Test
    public void createTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("shipping", new HashMap<String, Object>(){{
                put("name", "周先军");
                put("phone", "15019418143");
            }});
            put("items", new ArrayList<Map>(){{
                add(new HashMap<String, Object>(){{
                    put("visitor", new ArrayList<Map<String, Object>>(){{
                        add(new HashMap<String, Object>(){{
                            put("name", "Alone");
                            put("phone", "15019418143");
                            put("sid", "210905197807210546");
                            put("quantity", "1");
                        }});
                    }});
                    put("product_sub_id", "2");
                    put("start", "2016-10-29 00:00:00");
                    put("days", "1");
                    put("quantity", "1");
                }});
            }});
            put("from", OrderConstant.FromType.NON_TRUST);
            put("ep_id", "54");
            put("operator_id", "1");
            put("operator_name", "xxx");
            put("sale_amount", "0");
            put("remark", "test");
        }};
        mockMvc.perform(
                    post("/api/order/create").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
                ).andExpect(
                    status().isOk()
                ).andExpect(
                    jsonPath("$.code", is("200"))
                ).andDo(print());
    }

    @Test
    public void createLineOrder() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("shipping", new HashMap<String, Object>(){{
                put("name", "周先军");
                put("phone", "15019418143");
            }});
            put("items", new ArrayList<Map>(){{
                add(new HashMap<String, Object>(){{
                    put("visitor", new ArrayList<Map<String, Object>>(){{
                        add(new HashMap<String, Object>(){{
                            put("name", "Alone");
                            put("phone", "15019418143");
                            put("sid", "210905197807210546");
                            put("quantity", "1");
                            put("sex", "3001");
                        }});
                    }});
                    put("product_sub_code", "1495769742583200");
                    put("start", "2017-06-03 00:00:00");
                    put("days", "1");
                    put("quantity", "1");
                }});
            }});
            put("from", OrderConstant.FromType.NON_TRUST);
            put("ep_id", "24");
            put("core_ep_id", "1");
            put("operator_id", "1");
            put("operator_name", "xxx");
            put("sale_amount", "0");
            put("remark", "test");
        }};
        mockMvc.perform(
                post("/api/order/line/create").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void createAuditTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("shipping", new HashMap<String, Object>(){{
                put("name", "周先军");
                put("phone", "15019418143");
            }});
            put("items", new ArrayList<Map>(){{
                add(new HashMap<String, Object>(){{
                    put("visitor", new ArrayList<Map<String, Object>>(){{
                        add(new HashMap<String, Object>(){{
                            put("name", "Alone");
                            put("phone", "15019418143");
                            put("sid", "511702197403222585");
                            put("quantity", "1");
                        }});
                    }});
                    put("product_sub_id", "2");
                    put("start", "2016-10-26 00:00:00");
                    put("days", "1");
                    put("quantity", "1");
                }});
            }});
            put("from", OrderConstant.FromType.NON_TRUST);
            put("ep_id", "54");
            put("operator_id", "1");
            put("operator_name", "xxx");
            put("sale_amount", "0");
            put("remark", "test");
        }};
        mockMvc.perform(
                post("/api/order/create").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void createAuditAcceptTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("order_item_id", "1477363084692670");
            put("status", "true");
        }};
        mockMvc.perform(
                post("/api/order/audit").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void createAuditFailTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("order_item_id", "1477362994524670");
            put("status", "false");
        }};
        mockMvc.perform(
                post("/api/order/audit").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void paymentBalancesTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("order_sn", "1496283297143430");
            put("pay_type", "7111");
            put("ep_id", "24");
            put("core_ep_id", "1");
        }};
        mockMvc.perform(
                    post("/api/order/payment").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
                ).andExpect(
                    status().isOk()
                ).andExpect(
                    jsonPath("$.code", is("200"))
                ).andDo(print());
    }

    @Test
    public void refundApplyTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("order_item_sn", "1477387869542520");
            put("quantity", "1");
            put("cause", "自愿");
            put("days", new ArrayList<Map>(){{
                add(new HashMap<String, Object>(){{
                    put("day", "2016-10-26 00:00:00");
                    put("quantity", "1");
                    put("visitors", new ArrayList<Map>(){{
                        add(new HashMap<String, Object>(){{
                            put("sid", "210905197807210546");
                            put("phone", "15019418143");
                            put("quantity", "1");
                        }});
                    }});
                }});
            }});
        }};
        mockMvc.perform(
                    post("/api/order/refund/apply").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
                ).andExpect(
                    status().isOk()
                ).andExpect(
                    jsonPath("$.code", is("200"))
                ).andDo(print());
    }

    @Test
    public void refundApplyLine() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("order_item_sn", "1496283297344430");
            put("quantity", "1");
            put("cause", "自愿");
            put("ep_id", "24");
            put("core_ep_id", "1");
            put("apply_from", ProductConstants.RefundEqType.SELLER);
            put("days", new ArrayList<Map>(){{
                add(new HashMap<String, Object>(){{
                    put("day", "2017-06-03 00:00:00");
                    put("quantity", "1");
                    put("visitors", new ArrayList<Map>(){{
                        add(new HashMap<String, Object>(){{
                            put("id", "66499");
                            put("quantity", "1");
                        }});
                    }});
                }});
            }});
        }};
        mockMvc.perform(
                    post("/api/order/refund/line/apply").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
                ).andExpect(
                    status().isOk()
                ).andExpect(
                    jsonPath("$.code", is("200"))
                ).andDo(print());
    }

    @Test
    public void refundAcceptTest() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("refund_sn", "1477388159346520");
            put("status", "true");
            put("reason", "自愿");
        }};
        mockMvc.perform(
                    post("/api/order/refund/audit").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(params))
                ).andExpect(
                    status().isOk()
                ).andExpect(
                    jsonPath("$.code", is("200"))
                ).andDo(print());
    }

    @Test
    public void otaStatusInfo() throws Exception    {
        mockMvc.perform(
                get("/api/order/item/status/info/ota").param("number", "1490929842728350")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }

    @Test
    public void queryLineOrderList() throws Exception {
        mockMvc.perform(
                get("/api/order/query/line/list")
                        .param("ep_type", "10003").param("ep_id", "24").param("core_ep_id", "1")
        ).andExpect(
                status().isOk()
        ).andExpect(
                jsonPath("$.code", is("200"))
        ).andDo(print());
    }
}
