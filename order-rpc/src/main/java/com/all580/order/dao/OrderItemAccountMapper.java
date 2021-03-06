package com.all580.order.dao;

import com.all580.order.entity.OrderItemAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemAccountMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Thu Dec 08 13:55:41 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Thu Dec 08 13:55:41 CST 2016
     */
    int insert(OrderItemAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Thu Dec 08 13:55:41 CST 2016
     */
    int insertSelective(OrderItemAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Thu Dec 08 13:55:41 CST 2016
     */
    OrderItemAccount selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Thu Dec 08 13:55:41 CST 2016
     */
    int updateByPrimaryKeySelective(OrderItemAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_order_item_account
     *
     * @mbggenerated Thu Dec 08 13:55:41 CST 2016
     */
    int updateByPrimaryKey(OrderItemAccount record);

    /**
     * 根据子订单ID查询预分账记录
     * @param itemId 子订单ID
     * @return
     */
    List<OrderItemAccount> selectByOrderItem(@Param("itemId") Integer itemId);

    /**
     * 根据子订单ID&&企业查询预分账记录
     * @param itemId 子订单
     * @param epId 企业ID
     * @param coreEpId 企业托管平台
     * @return
     */
    OrderItemAccount selectByOrderItemAndEp(@Param("itemId") Integer itemId, @Param("epId") Integer epId, @Param("coreEpId") Integer coreEpId);

    /**
     * 根据订单ID获取所有子弟孤单分账记录
     * @param orderId 订单ID
     * @return
     */
    List<OrderItemAccount> selectByOrder(@Param("orderId") Integer orderId);

    /**
     * 根据订单ID&&平台商ID获取
     * @param orderId
     * @param coreEpId
     * @return
     */
    List<OrderItemAccount> selectByOrderAndCore(@Param("orderId") Integer orderId, @Param("coreEpId") Integer coreEpId);

    /**
     * 根据订单ID&&企业ID获取
     * @param orderId 订单ID
     * @param epId 企业ID
     * @param coreEpId 企业托管平台
     * @return
     */
    List<OrderItemAccount> selectByOrderAnEp(@Param("orderId") Integer orderId, @Param("epId") Integer epId, @Param("coreEpId") Integer coreEpId);

    /**
     * 根据订单ID获取该订单所有关联的平台商ID
     * @param orderId 订单ID
     * @return
     */
    List<Integer> selectCoreEpIdByOrder(@Param("orderId") Integer orderId);

    /**
     * 根据订单ID获取该订单所有关联的平台商ID
     * @param itemId 子订单ID
     * @return
     */
    List<Integer> selectCoreEpIdByOrderItem(@Param("itemId") Integer itemId);
}