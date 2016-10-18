package com.all580.order.dao;

import com.all580.order.entity.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface OrderItemMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item
     *
     * @mbggenerated Thu Oct 13 11:24:47 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item
     *
     * @mbggenerated Thu Oct 13 11:24:47 CST 2016
     */
    int insert(OrderItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item
     *
     * @mbggenerated Thu Oct 13 11:24:47 CST 2016
     */
    OrderItem selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item
     *
     * @mbggenerated Thu Oct 13 11:24:47 CST 2016
     */
    List<OrderItem> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item
     *
     * @mbggenerated Thu Oct 13 11:24:47 CST 2016
     */
    int updateByPrimaryKey(OrderItem record);

    /**
     * 根据子产品和身份证获取该身份证在指定时间内次数
     * @param productSubId 子产品ID
     * @param sid 身份证
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    int countBySidAndProductForDate(@Param("productSubId") Integer productSubId, @Param("sid") String sid,
                                    @Param("start") Date start, @Param("end") Date end);

    /**
     * 根据订单ID查询子订单
     * @param orderId 订单ID
     * @return
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Integer orderId);

    /**
     * 根据订单ID设置子订单状态
     * @param orderId 订单ID
     * @param status 状态
     * @return
     */
    int setStatusByOrderId(@Param("orderId") Integer orderId, @Param("status") Integer status);

    /**
     * 根据子订单编号获取
     * @param sn 编号
     * @return
     */
    OrderItem selectBySN(@Param("sn") Long sn);

    /**
     * 根据订单ID获取商品名称
     * @param orderId 订单ID
     * @return
     */
    List<String> getProductNamesByOrderId(@Param("orderId") Integer orderId);

    /**
     * 根据订单ID获取商品ID
     * @param orderId 订单ID
     * @return
     */
    List<Integer> getProductIdsByOrderId(@Param("orderId") Integer orderId);
}