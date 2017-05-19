package com.all580.voucherplatform.api.service;

import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/15.
 */
public interface SupplierService {

    Result create(Map map);

    Result getPlatform(Integer id);

    int getCount(String name);

    Result getList(String name, Integer recordStart, Integer recordCount);


    /**
     * 根据供应商的身份id和产品id，获取产品信息
     * @param supplyId
     * @param prodId
     * @return
     */
    Result getProd(int supplyId, String prodId);


    /**
     * 根据供应商的身份id，更新产品信息
     * @param supplyId
     * @param map
     * @return
     */
    Result setProd(int supplyId, Map map);
    /**
     * 根据供应商的身份id和产品id，删除产品
     * @param supplyId
     * @param prodId
     * @return
     */

    Result delProd(int supplyId, String prodId);
    /**
     * 根据供应商的身份id，批量更新产品信息
     * @param supplyId
     * @param map
     * @return
     */
    Result setProd(int supplyId, List<Map> map);


}
