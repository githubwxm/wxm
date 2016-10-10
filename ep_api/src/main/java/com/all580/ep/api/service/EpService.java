package com.all580.ep.api.service;


import com.all580.ep.api.entity.CoreEpAccess;
import com.all580.ep.api.entity.Ep;
import com.all580.ep.api.entity.PlatformEp;
import com.framework.common.Result;

import java.util.List;
import java.util.Map;

public interface EpService {

    /**
     * 创建平台商
     * @param ep
     * @return
     */
    Result<CoreEpAccess> createPlatform(Map ep);

    /**
     * 创建企业
     * @param map
     * @return
     */
    Result<Map> createEp(Map map);
    Result<List<Ep>> select (Map map);

    /**
     * 内部借口   int[] id企业id数组， String []返回列
     * @param epids
     * @param field
     * @return
     */
    Result<List<Ep>>  getEp(Integer [] epids, String[] field);

    Result<List<Ep>> all(Map params);

    /**
     * 验证平台商
     * @param params
     * @return
     */
    Result<PlatformEp> validate(Map params);
    /**
     * 获取企业状态（包括上级企业）
     *100-未初始化101-正常\n102-已冻结\n103-已停用
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

    /**
     * 冻结激活停用
     * @param params
     * @return
     */
    Result<Integer> updateStatus(Map params);

    Result<Integer> freeze(Map params);
    Result<Integer> disable(Map params);
    Result<Integer> enable(Map params);

    //    Result<List<Ep>> selectEp(Map map);
    Result<Map> updateEp(Map map);
}
