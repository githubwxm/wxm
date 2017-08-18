package com.all580.base.controller.com.all580.base.order;

import com.framework.common.lang.JsonUtils;
import com.framework.common.net.HttpUtils;
import com.framework.common.util.CommonUtil;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import static com.all580.base.controller.com.all580.base.order.CreateOrder.url;

/**
 * Created by wxming on 2017/7/31 0031.
 */
public class Task implements Runnable {

    LinkedList<OrderTicket> linkedList;

    public Task()
    {

    }

    public Task(LinkedList<OrderTicket> linkedList)
    {
        this.linkedList = linkedList;
    }

    @Override
    public void run()
    {
        while (!linkedList.isEmpty())
        {
            CreateOrder.start(Linked.getOrderTicket(), url,linkedList.size());
        }
        System.out.println("linkedList 为空  end:"+new Date());
        CollectSql.addSql("end:"+new Date());
    }
    public static void main(String a[]){
        String url ="http://service.all580.com.cn/api/order/refund/audit";
        String json = "{\"refund_sn\":\"1502716122755780\", \"status\": \"true\", \"reason\":\"ok\"}";
        Map<String, Object> params = JsonUtils.json2Map(json);
        params.put("access_id","1476277249859N2T3JBGA");
        params.put("ep_id","1");
        params.put("operator_name","畅旅");
        params.put("operator_id","1");
        TreeMap tree=new TreeMap(params);
        String  postParams = JsonUtils.toJson(tree);
        postParams=postParams.replace("null","");
        //['"',"\\","[","]","{","}",'null'
        postParams=postParams.replaceAll("[\"\\\\\\[\\]\\{\\}]","");
        String resultSign= CommonUtil.signForData("1476277250138WFBZ35GTM7PL25",postParams);
        params.put("sign",resultSign);
        String response = HttpUtils.postJson(url, JsonUtils.toJson(params), "UTF-8");
        System.out.println(response);
    }
}
