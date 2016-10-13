package com.all580.order.dao;

import com.all580.order.entity.OrderItemDetail;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface OrderItemDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_detail
     *
     * @mbggenerated Wed Oct 12 17:49:36 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_detail
     *
     * @mbggenerated Wed Oct 12 17:49:36 CST 2016
     */
    int insert(OrderItemDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_detail
     *
     * @mbggenerated Wed Oct 12 17:49:36 CST 2016
     */
    OrderItemDetail selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_detail
     *
     * @mbggenerated Wed Oct 12 17:49:36 CST 2016
     */
    List<OrderItemDetail> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_detail
     *
     * @mbggenerated Wed Oct 12 17:49:36 CST 2016
     */
    int updateByPrimaryKey(OrderItemDetail record);

    /**
     * 判断是否可退
     * @param itemId 子订单ID
     * @param day 日期
     * @param quantity 张数
     * @return
     */
    boolean canRefund(@Param("itemId") Integer itemId, @Param("day") Date day, @Param("quantity") Integer quantity);

    /**
     * 根据子订单获取每日详情
     * @param itemId 子订单
     * @return
     */
    List<OrderItemDetail> selectByItemId(@Param("itemId") Integer itemId);
}