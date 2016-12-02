package com.all580.base.controller.group;

import com.all580.base.manager.GroupValidateManager;
import com.all580.order.api.service.GroupService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public Result<?> addGroup(Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.addGroupValidate());
        return groupService.addGroup(params);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateGroup(Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.updateGroupValidate());
        return groupService.updateGroup(params);
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteGroup(Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.deleteGroupValidate());
        return groupService.delGroup(params);
    }

    @RequestMapping(value = "guide/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addGuide(Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.addGuideValidate());
        return groupService.addGuide(params);
    }

    @RequestMapping(value = "guide/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateGuide(Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.updateGuideValidate());
        return groupService.updateGuide(params);
    }

    @RequestMapping(value = "guide/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteGuide(Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.deleteGuideValidate());
        return groupService.delGuide(params);
    }

    @RequestMapping(value = "member/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addGroupMember(Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.addGroupMemberValidate());
        return groupService.addGroupMember(params);
    }

    @RequestMapping(value = "member/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteGroupMember(Map params) {
        ParamsMapValidate.validate(params, groupValidateManager.deleteGroupMemberValidate());
        return groupService.delGroupMember(params);
    }
}
