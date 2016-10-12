package com.all580.ep.api.service;



import com.framework.common.Result;

import java.util.List;
import java.util.Map;

public interface EpService {

    /**
     * 创建平台商
     * @param ep
     * @return
     */
    Result<Map> createPlatform(Map ep);

    /**
     * 创建企业
     * @param map
     * @return
     */
    Result<Map> createEp(Map map);
    Result<List<Map>> select (Map map);

    /**
     * 内部借口   int[] id企业id数组， String []返回列
     * @param epids
     * @param field
     * @return
     */
    Result<List<Map>>  getEp(Integer [] epids, String[] field);

    Result<List<Map>> all(Map params);

    /**
     * 验证平台商
     * @param params
     * @return
     */
    Result<Map> validate(Map params);
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
     * 冻结停用企业
     * @param params
     * @return
     */
    Result<Integer> freeze(Map params);
    Result<Integer> disable(Map params);
    Result<Integer> enable(Map params);

    Result<Integer> platformFreeze(Map params);
    Result<Integer> platformDisable(Map params);
    Result<Integer> platformEnable(Map params);
    //    Result<List<Ep>> selectEp(Map map);
    Result<Map> updateEp(Map map);

    /**
     * 下游平台商列表接口
     * @param map
     * @return
     */
    Result<List<Map>> platformListDown(Map map);
}
