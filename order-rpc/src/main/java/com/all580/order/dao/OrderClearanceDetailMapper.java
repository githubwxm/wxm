package com.all580.order.dao;

import com.all580.order.entity.OrderClearanceDetail;

public interface OrderClearanceDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_clearance_detail
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_clearance_detail
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int insert(OrderClearanceDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_clearance_detail
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int insertSelective(OrderClearanceDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_clearance_detail
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    OrderClearanceDetail selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_clearance_detail
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int updateByPrimaryKeySelective(OrderClearanceDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_clearance_detail
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int updateByPrimaryKey(OrderClearanceDetail record);
}