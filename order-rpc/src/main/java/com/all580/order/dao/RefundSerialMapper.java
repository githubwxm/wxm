package com.all580.order.dao;

import com.all580.order.entity.RefundSerial;

public interface RefundSerialMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int insert(RefundSerial record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int insertSelective(RefundSerial record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    RefundSerial selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int updateByPrimaryKeySelective(RefundSerial record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_serial
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int updateByPrimaryKey(RefundSerial record);
}