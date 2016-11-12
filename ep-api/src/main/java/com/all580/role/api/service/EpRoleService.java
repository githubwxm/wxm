package com.all580.role.api.service;


import com.framework.common.Result;

import java.util.Map;

/**
 * Created by wxming on 2016/11/11 0011.
 */
public interface EpRoleService {

    /**
     * 添加角色与菜单
     * @param params  name , user_id 创建人,   func_ids 功能集合list<Integer>
     * @return
     */
    Result addEpRole(Map<String,Object> params);

    /**
     * 修改角色与菜单
     * @param params  id  角色id，  name ,status 状态 0 ， oper_id 操作人   func_ids 功能集合list<Integer>
     * @return
     */
    Result updateEpRole(Map<String,Object> params);
}
