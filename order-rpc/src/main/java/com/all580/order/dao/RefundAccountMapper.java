package com.all580.order.dao;

import com.all580.order.entity.RefundAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RefundAccountMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_account
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_account
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int insert(RefundAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_account
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int insertSelective(RefundAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_account
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    RefundAccount selectByPrimaryKey(Integer id);

    List<RefundAccount> selectItemRefundOrder(Integer itemId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_account
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int updateByPrimaryKeySelective(RefundAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_refund_account
     *
     * @mbggenerated Fri Nov 25 14:51:24 CST 2016
     */
    int updateByPrimaryKey(RefundAccount record);

    /**
     * 根据退订订单ID获取退订分账
     * @param refundId 退订订单ID
     * @return
     */
    List<RefundAccount> selectByRefundId(@Param("refundId") Integer refundId);

    List<RefundAccount> selectByRefundIdAndCore(@Param("refundId") Integer refundId, @Param("coreEpId") Integer coreEpId);

    /**
     * 根据订单号和平台商查询退订分账
     * @param orderId
     * @param coreEpId
     * @return
     */
    List<RefundAccount> selectByOrderIdAndCore(@Param("orderId") Integer orderId, @Param("coreEpId") Integer coreEpId);
}