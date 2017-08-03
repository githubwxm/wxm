package com.all580.role.api.service;

import com.framework.common.Result;

import java.util.List;

/**
 * Created by wxming on 2017/6/29 0029.
 */
public interface PlatFuncService {
    Result addPlatFunc(int core_ep_id, List<Integer> list);
}
