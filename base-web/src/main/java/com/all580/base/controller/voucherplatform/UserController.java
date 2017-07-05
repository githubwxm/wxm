package com.all580.base.controller.voucherplatform;

import com.all580.base.manager.voucherplatform.UserValidateManager;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.api.service.UserService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/10.
 */

@Controller
@RequestMapping("voucherplatform/user")
@Slf4j
public class UserController extends BaseController {

    @Autowired
    private UserValidateManager userValidateManager;

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestBody Map map, HttpServletResponse response) {
        // 验证参数
        ParamsMapValidate.validate(map, userValidateManager.loginValidate());
        Result result = userService.login(map);
        if (result.isSuccess()) {
            String userName = CommonUtil.emptyStringParseNull(map.get("userName"));
            Result userResult = userService.getUser(userName);
            String token = UUIDGenerator.getUUID();
            Cookie cookie = new Cookie(VoucherConstant.COOKIENAME, token);
            Object o = userResult.get();
            redisUtils.set(VoucherConstant.REDISVOUCHERLOGINKEY + ":" + token, o);
            response.addCookie(cookie);
        }
        return userService.login(map);
    }


    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map map) {        // 验证参数

        ParamsMapValidate.validate(map, userValidateManager.updateValidate());
        return userService.update(map);

    }

    @RequestMapping(value = "getLoginUser", method = RequestMethod.GET)
    @ResponseBody
    public Result getLoginUser() {
        Map map = (Map) getAttribute("user");
        Result result = new Result(true);
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
