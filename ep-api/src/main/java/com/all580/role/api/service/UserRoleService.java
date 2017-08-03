package com.all580.role.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by wxming on 2017/7/5 0005.
 */
public interface UserRoleService {
    Result addUserRole(Map<String, Object> params);
    Result updateUserRole(Map<String, Object> params);
    Result addUserRoleFunc(Map params);
    Result updateUserRoleFunc(Map<String, Object> params);
    Result selectUserRoleList();
    Result selectUserId(int id);
    Result selectUserRoleIdFunc(int user_role_id);
    Result deleteFuncId(int func_id);
    Result selectUserRoleTypeList(int ep_type);

}
