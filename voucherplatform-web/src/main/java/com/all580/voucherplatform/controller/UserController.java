package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.UserService;
import com.all580.voucherplatform.manager.UserValidateManager;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/10.
 */

@Controller
@RequestMapping("api/user")
@Slf4j
public class UserController extends BaseController {

    @Autowired
    private UserValidateManager userValidateManager;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestBody Map map,
                        HttpServletRequest request) {
        // 验证参数
        ParamsMapValidate.validate(map, userValidateManager.loginValidate());
        Result result = userService.login(map);
        if (result.isSuccess()) {
            String userName = CommonUtil.emptyStringParseNull(map.get("userName"));
            Result userResult = userService.getUser(userName);
            Object o = userResult.get();
            request.getSession().setAttribute("user", o);
        }
        return userService.login(map);
    }


    @RequestMapping(value = "updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public Result updatePassword(@RequestBody Map map,
                                 HttpServletRequest request) {        // 验证参数
        Map mapUser = (Map) getAttribute("user");
        String oldPassword = CommonUtil.emptyStringParseNull(map.get("oldPassword"));
        oldPassword = DigestUtils.md5Hex(oldPassword);
        if (!oldPassword.equals(CommonUtil.emptyStringParseNull(mapUser.get("password")))) {
            return new Result(false, "原密码输入错误！");
        }
        request.getSession().invalidate();
        mapUser.put("password", CommonUtil.emptyStringParseNull(map.get("passWord")));
        return userService.update(mapUser);

    }

    @RequestMapping(value = "getLoginUser", method = RequestMethod.GET)
    @ResponseBody
    public Result getLoginUser() {
        Map map = (Map) getAttribute("user");
        Result result = new Result(true);
        map.remove("password");
        result.putAll(map);
        return result;
    }

    @RequestMapping(value = "getById", method = RequestMethod.GET)
    @ResponseBody
    public Result getById(@RequestParam("id") int id) {
        return userService.getUser(id);
    }

    @RequestMapping(value = "getByName", method = RequestMethod.GET)
    @ResponseBody
    public Result getById(@RequestParam("userName") String userName) {
        return userService.getUser(userName);
    }
}
