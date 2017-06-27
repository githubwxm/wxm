package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.GroupOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GroupOrderMapper {
    /**
     *  根据主键删除数据库的记录,t_group_order
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_group_order
     *
     * @param record
     */
    int insert(GroupOrder record);

    /**
     *  动态字段,写入数据库记录,t_group_order
     *
     * @param record
     */
    int insertSelective(GroupOrder record);

    /**
     *  根据指定主键获取一条数据库记录,t_group_order
     *
     * @param id
     */
    GroupOrder selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_group_order
     *
     * @param record
     */
    int updateByPrimaryKeySelective(GroupOrder record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_group_order
     *
     * @param record
     */
    int updateByPrimaryKey(GroupOrder record);

    GroupOrder selectByOrderCode(@Param("orderCode") String orderCode);

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
    int getOrderCount(@Param("platform_Id") Integer platformId,
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
    List<Map> getOrderList(@Param("platform_Id") Integer platformId,
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

    GroupOrder selectByPlatform(@Param("platform_Id") Integer platformId, @Param("platform_OrderCode") String platformOrderCode);
}