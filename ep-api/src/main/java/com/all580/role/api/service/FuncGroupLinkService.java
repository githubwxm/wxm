package com.all580.role.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by wxming on 2017/6/29 0029.
 */
public interface FuncGroupLinkService {
    Result addFuncGroupLink(Map<String, Object> map);
    Result selectFuncGroupLink(int func_group_id);
    Result selectPlatFromFuncGroupLink(int core_ep_id);
    Result selectPlatFromFuncGroup(int ep_func_type, int core_ep_id);
    Result deleteFuncGroupId(int func_group_id);

    /**
     * 添加菜单时  添加默认组与菜单关联关系
     * @param func_id
     * @return
     */
    Result addDefaultGroupLink(int func_id);

    /**
     * 根据菜单id删除  关联权限
     * @param func_id
     * @return
     */
    Result deleteFuncId(int func_id);
}
