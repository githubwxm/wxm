package com.all580.voucherplatform.api.service;

import com.framework.common.Result;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/14.
 */
public interface OrderService {

    Result getOrder(int id);

    Result getOrder(String orderCode);



    int getOrderCount(Integer platformId,
                      Integer supplyId,
                      String orderCode,
                      String platformOrderId,
                      String mobile,
                      String idNumber,
                      String voucherNumber,
                      Integer status,
                      Date startTime,
                      Date endTime);

    List<Map> getOrderList(Integer platformId,
                           Integer supplyId,
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
