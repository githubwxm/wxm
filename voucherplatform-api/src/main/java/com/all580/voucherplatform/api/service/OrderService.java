package com.all580.voucherplatform.api.service;

import com.framework.common.Result;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/14.
 */
public interface OrderService {
    /**
     * 创建一个新的平台商
     * 目前只有小秘书一个平台商
     * 尽量简化调用过程
     *
     * @param map {name:xx,description:xx,signType:xx}
     *            name  -string -名字
     *            description   -string -描述
     *            signType  -euun   -签名类型
     * @return
     */
    Result createOrder(Integer platformId,Map map);

    Result getOrder(int id);

    Result getOrder(String orderCode);

    Result getOrder(int platformId, String platformOrderId);

    Result updateOrder(Map map);

    int getOrderCount(Integer platformId,
                      Integer supplierId,
                      String orderCode,
                      String platformOrderId,
                      String mobile,
                      String idNumber,
                      String voucherNumber,
                      Integer status,
                      Date startTime,
                      Date endTime);

    List<Map> getOrderList(Integer platformId,
                           Integer supplierId,
                           String orderCode,
                           String platformOrderId,
                           String mobile,
                           String idNumber,
                           String voucherNumber,
                           Integer status,
                           Date startTime,
                           Date endTime,
                           Integer recordStart,
                           Integer recordCount);
}