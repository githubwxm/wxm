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
    /**
     * 获取企业状态（包括上级企业）
     *0-未初始化1-正常\n2-已冻结\n3-已停用
     * @param id
     * @return
     */
    Result<Integer> getEpStatus(Integer id);

    /**
     * 根据企业id查询平台商id
     * @param epId
     * @return
     */
    Result<Integer>  selectPlatformId(Integer epId);
}
