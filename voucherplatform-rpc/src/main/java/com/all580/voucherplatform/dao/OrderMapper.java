package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Order;
import com.framework.common.Result;

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
     * @param platformId      平台的id
     * @param supplierId      供应商id
     * @param orderCode       凭证订单号
     * @param platformOrderId 平台的订单号
     * @param mobile          手机号
     * @param idNumber        身份证号码
     * @param voucherNumber   凭证号
     * @param status          订单状态
     * @param startTime       订单创建起始时间
     * @param endTime         订单创建结束时间
     * @return
     */
    int getOrderCount(Integer platformId, Integer supplierId, String orderCode, String platformOrderId, String mobile, String idNumber, String voucherNumber, Integer status, Date startTime, Date endTime);


    /**
     * @param platformId      平台的id
     * @param supplierId      供应商id
     * @param orderCode       凭证订单号
     * @param platformOrderId 平台的订单号
     * @param mobile          手机号
     * @param idNumber        身份证号码
     * @param voucherNumber   凭证号
     * @param status          订单状态
     * @param startTime       订单创建起始时间
     * @param endTime         订单创建结束时间
     * @param recordStart
     * @param recordCount
     * @return
     */
    List<Map> getOrderList(Integer platformId, Integer supplierId, String orderCode, String platformOrderId, String mobile, String idNumber, String voucherNumber, Integer status, Date startTime, Date endTime, Integer recordStart, Integer recordCount);
}
