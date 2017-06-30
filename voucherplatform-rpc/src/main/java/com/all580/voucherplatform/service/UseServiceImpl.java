package com.all580.voucherplatform.service;

import com.all580.voucherplatform.api.service.UserService;
import com.all580.voucherplatform.dao.UserMapper;
import com.all580.voucherplatform.entity.User;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by Linv2 on 2017/5/10.
 */

@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class UseServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public Result login(Map map) {
        String userName = CommonUtil.emptyStringParseNull(map.get("userName"));
        String passWord = CommonUtil.emptyStringParseNull(map.get("passWord"));
        passWord = DigestUtils.md5Hex(passWord);
        //password加密
        Result result = new Result(false);
        // 先根据用户名查出用户再比对是否正确，如果直接使用用户名和密码sql查询则会导致sql注入问题。
        User user = userMapper.selectByName(userName);
        if (user == null) {
            return new Result(false, "用户名不存在");
        } else if (!user.getPassword().equals(passWord)) {
            return new Result(false, "密码错误");
        } else if (!user.getStatus()) {
            return new Result(false, "用户已被禁用");
        } else {
            return new Result(true);
        }

    }

    @Override
    public Result update(Map map) {
        Integer id = CommonUtil.objectParseInteger(map.get("id"));
        String passWord = CommonUtil.objectParseString(map.get("passWord"));
        passWord = new String(DigestUtils.md5(passWord));
        User user = new User();
        user.setId(id);
        user.setPassword(passWord);
        if (map.containsKey("status"))
            user.setStatus(Boolean.valueOf(map.get("status").toString()));
        Result result = new Result(true);
        userMapper.updateByPrimaryKeySelective(user);
        return result;
    }

    @Override
    public Result getUser(int id) {
        User user = userMapper.selectByPrimaryKey(id);
        Result result = new Result(true);
        if (user != null) {
            Map map = JsonUtils.obj2map(user);
            map.remove("password");
            result.put(map);
        }
        return result;
    }

    @Override
    public Result getUser(String userName) {
        User user = userMapper.selectByName(userName);
        Result result = new Result(true);
        if (user != null) {
            Map map = JsonUtils.obj2map(user);
            map.remove("password");
            result.put(map);
        }
        return result;
    }
}
