package com.all580.base.controller.voucherplatform;

import com.all580.base.manager.voucherplatform.UserValidateManager;
import com.all580.voucherplatform.api.service.UserService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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


    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestBody Map map) {
        // 验证参数
        ParamsMapValidate.validate(map, userValidateManager.loginValidate());
        return userService.login(map);
    }


    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map map) {
        // 验证参数
        ParamsMapValidate.validate(map, userValidateManager.updateValidate());
        return userService.update(map);

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
