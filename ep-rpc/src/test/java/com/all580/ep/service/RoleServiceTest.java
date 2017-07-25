package com.all580.ep.service;

import com.all580.role.api.service.FuncGroupLinkService;
import com.all580.role.api.service.FuncGroupService;
import com.all580.role.api.service.PlatFuncService;
import com.all580.role.api.service.UserRoleService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2017/6/29 0029.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=false)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
public class RoleServiceTest {

    @Autowired
    private FuncGroupLinkService funcGroupLinkService;
    @Autowired
    FuncGroupService funcGroupService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    PlatFuncService platFuncService;

    @Test
    public void testPlatFuncService(){
        Map map = new HashMap();
        map.put("name","name9");
        map.put("id","9");
        List<Integer> list = new ArrayList<Integer>();
        list.add(2);
        list.add(5);
        list.add(1);
        platFuncService.addPlatFunc(1,list);
    }
    @Test
    public void testUserRole(){
        Map map = new HashMap();
        map.put("name","name9");
        map.put("id","9");
        map.put("func_ids", Lists.newArrayList(2,3,4));
        map.put("oper_id",55);
        map.put("user_role_id","9");
        //userRoleService.addUserRole(map);
        //userRoleService.updateUserRole(map);
        //userRoleService.addUserRoleFunc(map);
        //userRoleService.updateUserRoleFunc(map);
        print(userRoleService.selectUserRoleList().get());
        print(userRoleService.selectUserId(9).get());
        print(111111);
    }
    @Test
    public void selectFuncGroupList(){
       // print(funcGroupService.selectFuncGroupList().get());
       // print(funcGroupLinkService.selectFuncGroupLink(1).get());
        print(funcGroupService.deleteFuncGroup(100).get());
        Map map = new HashMap();
        map.put("id","101");
        map.put("pid","1");
        map.put("title","4444");
        map.put("choose_type","1");
        map.put("extend_type","1");
        map.put("show_type","0");
        map.put("ep_func_type","7");
        funcGroupService.addFuncGroup(map);
    }
    @Test
    public void addFuncGroupList(){
        Map map = new HashMap<>();
        map.put("func_group_id","1");
        map.put("func_ids", Lists.newArrayList(4,2,5));
        funcGroupLinkService.addFuncGroupLink(map);
    }

    public void print(Object obj) {
        System.out.println(obj);
    }

}
