package com.all580.base.controller.com.all580.base.order;

import com.all580.order.api.OrderConstant;
import com.framework.common.lang.JsonUtils;
import com.framework.common.net.HttpUtils;
import com.framework.common.util.CommonUtil;

import java.util.*;

import static com.all580.base.controller.com.all580.base.order.Linked.linkedList;

/**
 * Created by wxming on 2017/7/27 0027.
 */
public class CreateOrder {

//    private static String access_id="1490680345283PB83BA2R";  // 114
//    private static String access_key="14906803452838HT2PTWG45GJ2V";
//    private static int ep_id=5006;
//    //static  String url ="http://all580.star.test.ngrok.all580.cn/service/remote/core/order/create/pay";   //250
//    static  String url="http://192.168.1.250:10001/api/order/create/pay";
    static String url ="http://service.all580.com.cn/api/order/create/pay";
    private static String access_id="1498610846448BBLZLSJQ";  //
    private static String access_key="1498610846448JCNU5XDUQDQNKX";
    private static int ep_id=4027;

    public static void main(String [] a){
        System.out.println("start:"+new Date());
        CollectSql.addSql("start:"+new Date());
        for(int i =1;i<=10;i++){
            Task  task1 = new Task(linkedList);
            Thread t1 = new Thread(task1,"name --"+i);
            t1.start();
        }
        System.out.println("end:"+new Date());
    }

    public static void start(OrderTicket order,String url,int ref){
        String outer_id = "_999"+System.currentTimeMillis();
        Map map = createOrder(order.getProduct_sub_code(),outer_id,order.getDate());

        String response = HttpUtils.postJson(url, JsonUtils.toJson(map), "UTF-8");
        Map result = JsonUtils.json2Map(response);
         if(result!=null){
             Integer code = CommonUtil.objectParseInteger(result.get("code"));
             if(code-200!=0){
                 System.out.println(response+"   "+outer_id);
             }else{
                 System.out.println("数量:"+ref+"   "+Thread.currentThread().getName() );
             }
         }
    }
    public static Map createOrder(String orderImteCode,String outer_id,String date){
        Map<String, Object> params = new HashMap<String, Object>(){{
            put("shipping", new HashMap<String, Object>(){{
                put("name", "余海涛0802");
                put("phone", "11111111111");
            }});
            put("items", new ArrayList<Map>(){{
                add(new HashMap<String, Object>(){{
                    put("visitor", new ArrayList<Map<String, Object>>(){{
                        add(new HashMap<String, Object>(){{
                            put("name", "余海涛0802");
                            put("phone", "11111111111");
                            put("sid", "420501198007121314");
                            put("quantity", "1");
                        }});
                    }});
                    put("product_sub_code", orderImteCode);
                    put("start", date);
                    put("days", "1");
                    put("quantity", "1");
                    put("send_msg",false);
                }});
            }});
            put("from", OrderConstant.FromType.NON_TRUST);
            put("ep_id", "54");
            put("outer_id",null);
            put("operator_id", "1");
            put("operator_name", "xxx");
            put("sale_amount", "0");
            put("remark", "test");
        }};
        params.put("access_id",access_id);
        params.put("ep_id",ep_id);
        params.put("operator_name","0802");
        params.put(" operator_id",ep_id);
        TreeMap tree=new TreeMap(params);
        String  postParams = JsonUtils.toJson(tree);
        postParams=postParams.replace("null","");
        //['"',"\\","[","]","{","}",'null'
        postParams=postParams.replaceAll("[\"\\\\\\[\\]\\{\\}]","");
        String resultSign= CommonUtil.signForData(access_key,postParams);
        params.put("sign",resultSign);

        return params;
    }
}
