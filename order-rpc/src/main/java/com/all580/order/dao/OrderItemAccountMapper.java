package com.all580.order.dao;

import com.all580.order.entity.OrderItemAccount;

public interface OrderItemAccountMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int insert(OrderItemAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int insertSelective(OrderItemAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    OrderItemAccount selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int updateByPrimaryKeySelective(OrderItemAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int updateByPrimaryKey(OrderItemAccount record);
}