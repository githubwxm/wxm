package com.all580.ep.service;

import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.api.service.CoreEpChannelService;

import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.api.service.EpService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @ClassName:
 * @Description:
 * @date 2016/9/28 10:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
public class epServiceTest {
    @Resource
    private EpService epService;
    @Resource
    private CoreEpAccessService coreEpAccessService;

    @Resource
    private EpBalanceThresholdService cpBalanceThresholdService;
    @Resource
    private CoreEpChannelService coreEpChannelService;


    @Test
    public void createChannel(){
        Map map = new HashMap();
        map.put("ep_id","10");
        map.put("core_ep_id","10");
        cpBalanceThresholdService.createOrUpdate(map);
       // System.out.println(coreEpChannelService.select(map).get());
    }
    @Test
    public void createEp(){
        Map map = new HashMap();
        map.put("ep_id",2);
        print(epService.platformListDown(map).get());
//        map.put("ep_type","10002");
//        map.put("creator_ep_id","1");//创建
//        map.put("core_ep_id",1);//平台
//        map.put("name","含浦大道");
//        map.put("code","code");
//        map.put("license","license");
//        map.put("linkman","linkman");
//        map.put("link_phone","13417325939");
//        map.put("province","1");
//        map.put("city","2");
//        map.put("area","123");
//        map.put("ep_class","10010");
//        map.put("address","含浦大道");
//        map.put("logo_pic","logopic");
//        //epService.createEp(map);
//        epService.createPlatform(map);
//        map.put("address",map.get("address").toString()+"333333");
//        System.out.println(epService.updateEp(map).get());
//        // epService.createPlatform(map);

             //   System.out.println(epService.select(map).get());
    }
    @Test
    public void createTest() {
        Map map = new HashMap<String,Object>();
        map.put("supplier_name","adsf");
        map.put("","83YL8MD4JJAX");
        // System.out.println(coreEpPaymentConfService.add(map)+"      *****");
        //System.out.println(epService.selectPlatformId(2).get());
        // coreEpChannelService.cancle(2);
//        map.put("id",2);
//        map.put("access_id", Common.getAccessId());
        // epService.freeze(map);
        map.put("core_ep_id","1");
        System.out.println(epService.select(map).get().size());

        // map.put("id",11);
//        map.put("access_id","123");
//        map.put("link_phone","13417325939");
//        map.put("id","123");
//
//        map.put("access_key", Common.getAccessKey());
//        map.put("link", "123");
//        coreEpAccessService.create(map);
//        System.out.println(coreEpAccessService.select(map));
//        Ep ep = new Ep();
//        ep.setAccess_id("1212");
//        ep.setAccess_key("key");
//        ep.setAddress("地址");

        //  System.out.println(epService.selectPlatformId(6).get()+"   ***");
        //System.out.print(epService.getEp(new Integer[]{1,2},new String[]{"ep_type","address"}));
//        map.put("access_id","");
//        map.put("core_ep_id","1");
        map.put("access_id","83YL8MD4JJAX");
        map.put("sdfasd","");
//        epService.validate(map);

//        coreEpPaymentConfService.add(map);
//        coreEpPaymentConfService.update(map);
        // epService.validate(map);
        //epService.findById(1);
//        epService.create(null);
//        epService.all(null);
//        epService.validate(null);
    }
    public void print(Object obj){
        System.out.println(obj);
    }
}
