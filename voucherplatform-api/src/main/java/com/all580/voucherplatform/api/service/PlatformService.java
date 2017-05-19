package com.all580.voucherplatform.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by Linv2 on 2017/5/15.
 */
public interface PlatformService {
    Result create(Map map);

    Result getPlatform(Integer id);

    int getCount(String name);

    Result getList(String name, Integer recordStart, Integer recordCount);
}
