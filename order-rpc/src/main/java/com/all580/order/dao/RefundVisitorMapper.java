package com.all580.order.dao;

import com.all580.order.entity.RefundVisitor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RefundVisitorMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_visitor
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_visitor
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int insert(RefundVisitor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_visitor
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int insertSelective(RefundVisitor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_visitor
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    RefundVisitor selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_visitor
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int updateByPrimaryKeySelective(RefundVisitor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_visitor
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int updateByPrimaryKey(RefundVisitor record);

    /**
     * 根据子订单查询已退订的游客(没有自增ID的 聚合)
     * @param itemId
     * @return
     */
    List<RefundVisitor> selectByItemId(@Param("itemId") Integer itemId);

    /**
     * 根据子订单查询已退订的游客 不包含退订失败的(没有自增ID的 聚合)
     * @param itemId
     * @return
     */
    List<RefundVisitor> selectByItemIdExcludeFailed(@Param("itemId") Integer itemId);

    /**
     * 根据退订订单查询已退订的游客
     * @param refundId
     * @return
     */
    List<RefundVisitor> selectByRefundId(@Param("refundId") Integer refundId);

    /**
     * 根据退订订单和游客ID获取已退订的游客
     * @param refundId
     * @param visitorId
     * @return
     */
    RefundVisitor selectByRefundIdAndVisitorId(@Param("refundId") Integer refundId, @Param("visitorId") Integer visitorId);
}