package com.all580.voucherplatform.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by Linv2 on 2017/5/10.
 */
public interface UserService {

    /**
     * 登录
     *
     * @param map {userName:xx,passWord:xx}
     *             userName - String - 用户名
     *             passWord - String - 密码
     * @return
     */
    Result login(Map map);

    /**
     *  用户信息修改
     * @param map { id:xx,passWord:xx,status:xx  }
     *             id - String - 用户id
     *             passWord - String - 密码
     *             status - bool - 账号启用状态
     * @return
     */
    Result update(Map map);

    /**
     * 根据用户id查找用户
     * @param id
     * @return
     */
    Result getUser(int id);

    /**
     * 根据用户名查找用户
     * @param userName
     * @return
     */
    Result getUser(String userName);
}
