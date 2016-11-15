package com.all580.ep.service;

import com.all580.ep.api.service.*;

import com.all580.ep.dao.EpBalanceThresholdMapper;
import com.all580.ep.dao.EpMapper;
import com.all580.notice.api.conf.SmsType;
import com.all580.notice.api.service.SmsService;
import com.all580.payment.api.service.BalancePayService;
import com.all580.product.api.service.PlanGroupRPCService;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.all580.role.api.service.EpRoleService;
import com.all580.role.api.service.FuncService;
import com.all580.role.dao.EpRoleFuncMapper;
import com.all580.role.dao.EpRoleMapper;
import com.all580.role.dao.FuncMapper;
import com.all580.role.dao.IntfMapper;
import com.framework.common.Result;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author
 * @ClassName:
 * @Description:
 * @date 2016/9/28 10:21
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
public class epServiceTest {
    @Resource
    private EpService epService;
    @Resource
    private LogCreditService logCreditService;

    @Resource
    private EpFinanceService epFinanceService;

    @Resource
    private CoreEpAccessService coreEpAccessService;

    @Resource
    private EpMapper epMapper;
    @Resource
    private FuncMapper funcMapper;
    @Resource
    private EpRoleFuncMapper epRoleFuncMapper;
    @Resource
    private EpBalanceThresholdMapper epBalanceThresholdMapper;

//    @Autowired
//    private ProductSalesPlanRPCService productSalesPlanRPCService;

    @Autowired
    private EpBalanceThresholdService epBalanceThresholdService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private PlanGroupRPCService planGroupRPCService;

    @Autowired
    private EpRoleService epRoleService;
    @Resource
    private FuncService funcService;
    @Autowired
    private IntfMapper intfMapper;//ddd

    @Autowired
    private EpRoleMapper epRoleMapper;

    @Test
    public void testEpRoleService(){
        Map map = new HashMap();
        map.put("name","test");
        map.put("user_id","11");
        map.put("id",3);
       // epRoleService.updateEpRole(map);
        //epRoleMapper.updateByPrimaryKey(map);
        print(epRoleService.selectList(map));
    }
    @Test
    public void testFunc() {
        Map map = new HashMap();
        List<Integer> list = new ArrayList<>();
        list.add(6666);
        list.add(5555);
       // map.put("id", "2");
        map.put("name", "hahaha");
        map.put("pid", "1");
        map.put("oper_id", "30");
        map.put("status", 1);
        map.put("func_ids", list);
//
//        // name , user_id 创建人,   func_ids 功能集合list<Integer>
//        Result r =epRoleService.updateEpRole(map);
        //funcService.insertSelective(map);
        funcService.deleteByPrimaryKey(4);
      print(funcService.getAll().get());

    }

    @Test
    public void EpFinanceServiceImple() {
        Integer epId = 30;
        String productName = null;
        String productSubName = null;
        Integer province = null;
        Integer city = null;
        Integer area = null;
        Integer ticketFlag = null;
        Integer payType = null;
        Integer ticketDict = null;
        Integer start = 0;
        Integer count = 10;

        // productSalesPlanRPCService.selectCanSaleSubProduct(epId,productName,productSubName,province,city,area,ticketFlag,payType,ticketDict,start,count);

//       Map map = new HashMap();
//       map.put("id", 56);
//       map.put("ep_id",2);
//       Result<Integer> r =epService.freeze(map);
//
//        print(r);
//       Map map = new HashMap();
//       map.put("id",-2222);
//       map.put("core_ep_id",-22222);
//       try{
//           Assert.assertEquals(epBalanceThresholdMapper.createOrUpdate(map), 1);
//       }catch(Exception e){
//           e.printStackTrace();
//       }


       /*print("ttssttss");

       map.put("id",7);
       print(epService.select(map).get()+"   ttss");
       List ids = new ArrayList<Integer>();
       ids.add(-1);
       print(coreEpAccessService.selectAccessList(ids).get()+"ssss");*/
       Map map = new HashMap();
       map.put("core_ep_id","1");
       epFinanceService.addBalance(1146,1,11111);
    }

    @Test
    public void logCreditService() {
        Map map = new HashMap();
        map.put("ep_id","1139");
        map.put("epName","");
        map.put("epCity","");
        map.put("epPhone","");
        map.put("epProvince","");
        map.put("epType","");
        map.put("epCity","");

       // print(epService.platformListDown(map).get());
        // print(logCreditService.selectList(map).get());
    }

    @Test
    public void createChannel() {
        Map map = new HashMap();
        map.put("ep_id", "1");
//        map.put("core_ep_id", "-1");
        // log.info("132132");
       // print(epService.platformListDown(map));

//    print(epService.select(map).toString());
//        print(epService.select(map).get()==null);
//        print(epService.select(map).get().isEmpty());

        //  print(cpBalanceThresholdService.createOrUpdate(map));
        // System.out.println(coreEpChannelService.select(map).get());
    }

    @Test
    public void createEp() {
        Map map = new HashMap();
//        map.put("ep_id",3);
//        map.put("record_start",0);
//        map.put("record_count",1);
//        Result<Map> result =epService.platformListUp(map);
//        print(result.get("code"));
//      //  print(result.get("status"));
        //  print(epService.selectCreatorEpId(15));
        // print(epService.platformListDown(map).get());
        map.put("ep_type", null);
        map.put("creator_ep_id", "1");//创建
        map.put("core_ep_id", 1);//平台
        map.put("name", "含浦大道");
        map.put("code", "code");
        map.put("license", "license");
        map.put("linkman", "linkman");
        map.put("link_phone", "13417325939");
        map.put("province", "1");
        map.put("city", "2");
        map.put("area", "123");
        map.put("ep_class", "10010");
        map.put("address", "含浦大道");
        map.put("logo_pic", "logopic");
        // epService.createEp(map);
        map.put("id", "1");
        //  epService.platformFreeze(map);
       // print(epService.selectSeller(30));
        //epService.createPlatform(map);
        Integer[] ids = new Integer[]{1, 3};
        String[] fields = new String[]{"id", "ep_type"};

        //print(epService.getEp(ids,fields).get());
//        epService.createPlatform(map);
//        map.put("address",map.get("address").toString()+"333333");
//        System.out.println(epService.updateEp(map).get());
//        // epService.createPlatform(map);

        //   System.out.println(epService.select(map).get());
    }

    @Test
    public void createTest() {
        Map map = new HashMap<String, Object>();
        map.put("id", "4");
        map.put("ep_id", "1");

        Map<String, String> smsParams = new HashMap<>();
        smsParams.put("username", "username");//客户
        smsParams.put("password","mima");
        Result r = smsService.send("13417325939", 12003, 1114, smsParams);
        print(r.isSuccess());
        //  epService.platformFreeze(map);
        //epBalanceThresholdService.createOrUpdate(map);
        // System.out.println(coreEpPaymentConfService.add(map)+"      *****");
        //System.out.println(epService.selectPlatformId(2).get());
        // coreEpChannelService.cancle(2);
//        map.put("id",2);
//        map.put("access_id", Common.getAccessId());
        // epService.freeze(map);
        map.put("core_ep_id", "1");
        //  System.out.println(epService.select(map).get().size());

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
        map.put("access_id", "83YL8MD4JJAX");
        map.put("sdfasd", "");
//        epService.validate(map);

//        coreEpPaymentConfService.add(map);
//        coreEpPaymentConfService.update(map);
        // epService.validate(map);
        //epService.findById(1);
//        epService.create(null);
//        epService.all(null);
//        epService.validate(null);
    }

    public void print(Object obj) {
        System.out.println(obj);
    }
}
