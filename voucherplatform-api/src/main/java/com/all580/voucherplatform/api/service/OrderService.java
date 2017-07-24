package com.all580.voucherplatform.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/14.
 */
public interface OrderService {

    Result getOrder(int id);

    Result getOrder(String orderCode);


    Result<PageRecord<Map>> selectOrderList(Integer platformId,
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


    Result<PageRecord<Map>> selectOrderConsumeList(Integer platformId,
                                                   Integer supplyId,
                                                   String orderCode,
                                                   String platformOrderId,
                                                   String mobile,
                                                   String idNumber,
                                                   String voucherNumber,
                                                   Integer status,
                                                   Date startTime,
                                                   Date endTime,
                                                   String consumeCode,
                                                   String supplyConsumeCode,
                                                   String deviceId,
                                                   Integer orderId,
                                                   Integer recordStart,
                                                   Integer recordCount);


    Result<PageRecord<Map>> selectOrderRefundList(Integer platformId,
                                                  Integer supplyId,
                                                  String orderCode,
                                                  String platformOrderId,
                                                  String mobile,
                                                  String idNumber,
                                                  String voucherNumber,
                                                  Integer status,
                                                  Date startTime,
                                                  Date endTime,
                                                  String platformRefId,
                                                  String voucherRefId,
                                                  String supplyRefId,
                                                  Integer orderId,
                                                  Integer prodType,
                                                  Integer recordStart,
                                                  Integer recordCount);


    Result<PageRecord<Map>> selectGroupOrderList(Integer platformId,
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
