package com.all580.base.controller.group;

import com.all580.base.manager.GroupValidateManager;
import com.all580.base.util.Utils;
import com.all580.ep.api.conf.EpConstant;
import com.all580.order.api.service.GroupService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 团队网关
 * @date 2016/12/1 17:34
 */
@Controller
@RequestMapping("api/group")
@Slf4j
public class GroupController extends BaseController {
    @Autowired
    private GroupValidateManager groupValidateManager;

    @Autowired
    private GroupService groupService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addGroup(@RequestBody Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.addGroupValidate());
        return groupService.addGroup(params);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateGroup(@RequestBody Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.updateGroupValidate());
        return groupService.updateGroup(params);
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteGroup(@RequestBody Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.deleteGroupValidate());
        return groupService.delGroup(params);
    }

    @RequestMapping(value = "guide/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addGuide(@RequestBody Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.addGuideValidate());
        return groupService.addGuide(params);
    }

    @RequestMapping(value = "guide/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateGuide(@RequestBody Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.updateGuideValidate());
        return groupService.updateGuide(params);
    }

    @RequestMapping(value = "guide/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteGuide(@RequestBody Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.deleteGuideValidate());
        return groupService.delGuide(params);
    }

    @RequestMapping(value = "member/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addGroupMember(@RequestBody Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.addGroupMemberValidate());
        return groupService.addGroupMember(params);
    }

    @RequestMapping(value = "member/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteGroupMember(@RequestBody Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.deleteGroupMemberValidate());
        return groupService.delGroupMember(params);
    }

    @RequestMapping(value = "query/list" , method = RequestMethod.GET)
    @ResponseBody
    public  Result<?> queryGroupList(@RequestParam String number,
                                                String guide_name,
                                                String start,
                                                String end,
                                                String province,
                                                String city,
                                                @RequestParam Integer ep_id,
                                                @RequestParam(defaultValue = "0") Integer record_start,
                                                @RequestParam(defaultValue = "20") Integer record_count){
        return groupService.queryGroupList(ep_id,number,guide_name,start,end,province,city,record_start, record_count);
    }

    @RequestMapping(value = "guide/list" , method = RequestMethod.GET)
    @ResponseBody
    public  Result<?> queryGuideList(@RequestParam String name,
                                     String phone,
                                     String card,
                                     @RequestParam Integer ep_id,
                                     @RequestParam(defaultValue = "0") Integer record_start,
                                     @RequestParam(defaultValue = "20") Integer record_count){
        return groupService.queryGuideList(ep_id,name,phone,card,record_start, record_count);
    }

    @RequestMapping(value = "view" , method = RequestMethod.GET)
    @ResponseBody
    public  Result<?> queryGroupViewById(@RequestParam Integer id){
        return groupService.queryGroupById(id);
    }

    @RequestMapping(value = "guide/view" , method = RequestMethod.GET)
    @ResponseBody
    public  Result<?> queryGuideViewById(@RequestParam Integer id){
        return groupService.queryGuideById(id);
    }

    @RequestMapping(value = "member/list" , method = RequestMethod.GET)
    @ResponseBody
    public Result<?> queryMemberList(@RequestParam Integer group_id,
                                     @RequestParam String name,
                                     @RequestParam String card,
                                     @RequestParam String phone,
                                     @RequestParam(defaultValue = "0") Integer record_start,
                                     @RequestParam(defaultValue = "20") Integer record_count){
        return groupService.queryMemberList(group_id,name,card,phone,record_start,record_count);
    }

    @RequestMapping(value = "member/all" , method = RequestMethod.GET)
    @ResponseBody
    public Result<?> queryMemberNoPageList(@RequestParam Integer group_id){
        return groupService.queryMemberNoPageList(group_id);
    }

    @RequestMapping(value = "order/guide" , method = RequestMethod.GET)
    @ResponseBody
    public Result<?> queryOrderGuideByOrderId(@RequestParam Integer orderId){
        return groupService.queryOrderGuideByOrderId(orderId);
    }

    @RequestMapping(value = "order/member" , method = RequestMethod.GET)
    @ResponseBody
    public Result<?> queryOrderMember(@RequestParam Integer suborderid,
                                      @RequestParam String name,
                                      @RequestParam String card,
                                      @RequestParam String phone){
        return groupService.queryOrderMember(suborderid,name,card,phone);
    }
}
