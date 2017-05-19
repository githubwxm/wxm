package com.all580.voucherplatform.api.service;

import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/15.
 */
public interface SupplyService {

    Result create(Map map);

    Result getSupply(Integer id);

    int getCount(String name);

    Result getList(String name, Integer recordStart, Integer recordCount);


    /**
     * 根据供应商的身份id和产品id，获取产品信息
     *
     * @param supplyId
     * @param prodId
     * @return
     */
    Result getProd(int supplyId, String prodId);


    /**
     * 根据供应商的身份id，更新产品信息
     *
     * @param supplyId
     * @param map      {id:xx,name:xx:descripton:xx:data:object}
     *                 id-   string- id
     *                 name- string- 名字
     *                 description-  string- 描述
     *                 data- object- 对象数据
     * @return
     */
    Result setProd(int supplyId, Map map);

    /**
     * 根据供应商的身份id和产品id，删除产品
     *
     * @param supplyId
     * @param prodId
     * @return
     */

    Result delProd(int supplyId, String prodId);

    /**
     * 根据供应商的身份id，批量更新产品信息
     *
     * @param supplyId
     * @param map      {id:xx,name:xx:descripton:xx:data:object}
     *                 id-   string- id
     *                 name- string- 名字
     *                 description-  string- 描述
     *                 data- object- 对象数据
     * @return
     */
    Result setProd(int supplyId, List<Map> map);


}
