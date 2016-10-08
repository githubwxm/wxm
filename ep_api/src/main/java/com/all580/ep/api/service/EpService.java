package com.all580.ep.api.service;


import com.all580.ep.api.entity.CoreEpAccess;
import com.all580.ep.api.entity.Ep;
import com.all580.ep.api.entity.PlatformEp;
import com.framework.common.Result;

import java.util.List;
import java.util.Map;

public interface EpService {

    Result<CoreEpAccess> createPlatform(Map ep);
    Result<?> select (Map map);
    Result<List<Ep>>  getEp(Integer [] epids, String[] field);

    Result<List<Ep>> all(Map params);
    Result<PlatformEp> validate(Map params);
    Result<Integer> getEpStatus(Integer id);
}
