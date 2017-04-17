package com.all580.order.dao;

import com.all580.order.entity.RefundSerial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RefundSerialMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int insert(RefundSerial record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int insertSelective(RefundSerial record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    RefundSerial selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int updateByPrimaryKeySelective(RefundSerial record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int updateByPrimaryKey(RefundSerial record);

    /**
     * 根据本地退票流水获取退票流水
     * @param localSn 本地退票流水
     * @return
     */
    RefundSerial selectByLocalSn(Long localSn);

    List<RefundSerial> selectItemRefundSerial(Integer itemId);
    /**
     * 根据退订订单ID获取退票流水
     * @param refundId 退订订单ID
     * @return
     */
    RefundSerial selectByRefundOrder(@Param("refundId") Integer refundId);
}