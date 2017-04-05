package com.all580.ep.api.service;

import com.framework.common.Result;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/4/5 15:17
 */
public interface EpPushService {
    /**
     * 根据企业ID获取推送配置
     * @param epId 企业ID
     * @return
     */
    Result<?> selectByEpId(String epId);
}
