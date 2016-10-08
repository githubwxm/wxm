package com.all580.order.dao;

import com.all580.order.entity.ClearanceWashedSerial;
import java.util.List;

public interface ClearanceWashedSerialMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_clearance_washed_serial
     *
     * @mbggenerated Fri Sep 30 15:22:08 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_clearance_washed_serial
     *
     * @mbggenerated Fri Sep 30 15:22:08 CST 2016
     */
    int insert(ClearanceWashedSerial record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_clearance_washed_serial
     *
     * @mbggenerated Fri Sep 30 15:22:08 CST 2016
     */
    ClearanceWashedSerial selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_clearance_washed_serial
     *
     * @mbggenerated Fri Sep 30 15:22:08 CST 2016
     */
    List<ClearanceWashedSerial> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_clearance_washed_serial
     *
     * @mbggenerated Fri Sep 30 15:22:08 CST 2016
     */
    int updateByPrimaryKey(ClearanceWashedSerial record);
}