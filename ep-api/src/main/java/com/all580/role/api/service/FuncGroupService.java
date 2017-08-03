package com.all580.role.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by wxming on 2017/6/29 0029.
 */
public interface FuncGroupService {
    Result selectFuncGroupList();
    Result updateFuncGroup(Map<String, Object> map);
    Result addFuncGroup(Map<String, Object> map);
    Result deleteFuncGroup(int id);
    Result selectFuncGroupId(int id);

}
