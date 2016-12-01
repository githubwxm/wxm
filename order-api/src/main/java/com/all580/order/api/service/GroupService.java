package com.all580.order.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 团队服务
 * @date 2016/12/1 11:30
 */
public interface GroupService {
    Result<?> addGroup(Map params);

    Result<?> updateGroup(Map params);

    Result<?> delGroup(Map params);

    Result<?> addGuide(Map params);

    Result<?> updateGuide(Map params);

    Result<?> delGuide(Map params);

    Result<?> addGroupMember(Map params);

    Result<?> delGroupMember(Map params);
}
