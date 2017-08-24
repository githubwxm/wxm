package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderMapper {
    /**
     * 根据主键删除数据库的记录,t_order
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新写入数据库记录,t_order
     *
     * @param record
     */
    int insert(Order record);

    /**
     * 动态字段,写入数据库记录,t_order
     *
     * @param record
     */
    int insertSelective(Order record);

    /**
     * 根据指定主键获取一条数据库记录,t_order
     *
     * @param id
     */
    Order selectByPrimaryKey(Integer id);

    /**
     * 动态字段,根据主键来更新符合条件的数据库记录,t_order
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     * 根据主键来更新符合条件的数据库记录,t_order
     *
     * @param record
     */
    int updateByPrimaryKey(Order record);

    /**
     * @param orderCode
     * @return
     */
    Order selectByOrderCode(@Param("order_Code") String orderCode);


    /**
     * @param platformOrderCode
     * @param seqId
     * @return
     */
    Order selectByPlatform(@Param("platform_Id") Integer platformId,
                           @Param("platform_OrderCode") String platformOrderCode,
                           @Param("seqId") String seqId);

    /**
     * @param supplyId
     * @param supplyOrderId
     * @return
     */
    Order selectBySupply(@Param("supply_Id") int supplyId,
                         @Param("supply_OrderId") String supplyOrderId);

    /**
     * @param platformId
     * @param supplyId
     * @param orderCode
     * @param platformOrderId
     * @param mobile
     * @param idNumber
     * @param voucherNumber
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    int selectOrderCount(@Param("platform_Id") Integer platformId,
                         @Param("supply_Id") Integer supplyId,
                         @Param("order_Code") String orderCode,
                         @Param("platform_OrderId") String platformOrderId,
                         @Param("mobile") String mobile,
                         @Param("idNumber") String idNumber,
                         @Param("voucherNumber") String voucherNumber,
                         @Param("status") Integer status,
                         @Param("start_Time") Date startTime,
                         @Param("end_Time") Date endTime);

    /**
     * @param platformId
     * @param supplyId
     * @param orderCode
     * @param platformOrderId
     * @param mobile
     * @param idNumber
     * @param voucherNumber
     * @param status
     * @param startTime
     * @param endTime
     * @param recordStart
     * @param recordCount
     * @return
     */
    List<Map> selectOrderList(@Param("platform_Id") Integer platformId,
                              @Param("supply_Id") Integer supplyId,
                              @Param("order_Code") String orderCode,
                              @Param("platform_OrderId") String platformOrderId,
                              @Param("mobile") String mobile,
                              @Param("idNumber") String idNumber,
                              @Param("voucherNumber") String voucherNumber,
                              @Param("status") Integer status,
                              @Param("start_Time") Date startTime,
                              @Param("end_Time") Date endTime,
                              @Param("record_start") Integer recordStart,
                              @Param("record_count") Integer recordCount);

    List<Order> selectOrderListByPrimaryKey(@Param("list") Integer... orderId);

    int selectOrderConsumeCount(@Param("platform_Id") Integer platformId,
                                @Param("supply_Id") Integer supplyId,
                                @Param("order_Code") String orderCode,
                                @Param("platform_OrderId") String platformOrderId,
                                @Param("mobile") String mobile,
                                @Param("idNumber") String idNumber,
                                @Param("voucherNumber") String voucherNumber,
                                @Param("status") Integer status,
                                @Param("start_Time") Date startTime,
                                @Param("end_Time") Date endTime,
                                @Param("consume_Code") String consumeCode,
                                @Param("supply_consume_Code") String supplyConsumeCode,
                                @Param("deviceId") String deviceId,
                                @Param("order_id") Integer orderId);


    List<Map> selectOrderConsumeList(@Param("platform_Id") Integer platformId,
                                     @Param("supply_Id") Integer supplyId,
                                     @Param("order_Code") String orderCode,
                                     @Param("platform_OrderId") String platformOrderId,
                                     @Param("mobile") String mobile,
                                     @Param("idNumber") String idNumber,
                                     @Param("voucherNumber") String voucherNumber,
                                     @Param("status") Integer status,
                                     @Param("start_Time") Date startTime,
                                     @Param("end_Time") Date endTime,
                                     @Param("consume_Code") String consumeCode,
                                     @Param("supply_consume_Code") String supplyConsumeCode,
                                     @Param("deviceId") String deviceId,
                                     @Param("order_id") Integer orderId,
                                     @Param("record_start") Integer recordStart,
                                     @Param("record_count") Integer recordCount);


    int selectOrderRefCount(@Param("platformId") Integer platformId,
                            @Param("supplyId") Integer supplyId,
                            @Param("orderCode") String orderCode,
                            @Param("platformOrderId") String platformOrderId,
                            @Param("mobile") String mobile,
                            @Param("idNumber") String idNumber,
                            @Param("voucherNumber") String voucherNumber,
                            @Param("status") Integer status,
                            @Param("startTime") Date startTime,
                            @Param("endTime") Date endTime,
                            @Param("platformRefId") String platformRefId,
                            @Param("voucherRefId") String voucherRefId,
                            @Param("supplyRefId") String supplyRefId,
                            @Param("orderId") Integer orderId,
                            @Param("prodType") Integer prodType);


    List<Map> selectOrderRefList(@Param("platformId") Integer platformId,
                                 @Param("supplyId") Integer supplyId,
                                 @Param("orderCode") String orderCode,
                                 @Param("platformOrderId") String platformOrderId,
                                 @Param("mobile") String mobile,
                                 @Param("idNumber") String idNumber,
                                 @Param("voucherNumber") String voucherNumber,
                                 @Param("status") Integer status,
                                 @Param("startTime") Date startTime,
                                 @Param("endTime") Date endTime,
                                 @Param("platformRefId") String platformRefId,
                                 @Param("voucherRefId") String voucherRefId,
                                 @Param("supplyRefId") String supplyRefId,
                                 @Param("orderId") Integer orderId,
                                 @Param("prodType") Integer prodType,
                                 @Param("record_start") Integer recordStart,
                                 @Param("record_count") Integer recordCount);

    List<Map> selectOrderReportNumber(@Param("startTime") Date startTime,
                                @Param("endTime") Date endTime,
                                @Param("platformProdId") Integer platformProdId,
                                @Param("supplyProdId") Integer supplyProdId);

    List<Map> selectOrderReportCount(@Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime,
                                     @Param("platformProdId") Integer platformProdId,
                                     @Param("supplyProdId") Integer supplyProdId);
}