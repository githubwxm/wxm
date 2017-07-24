package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Refund;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RefundMapper {
    /**
     * 根据主键删除数据库的记录,t_refund
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新写入数据库记录,t_refund
     *
     * @param record
     */
    int insert(Refund record);

    /**
     * 动态字段,写入数据库记录,t_refund
     *
     * @param record
     */
    int insertSelective(Refund record);

    /**
     * 根据指定主键获取一条数据库记录,t_refund
     *
     * @param id
     */
    Refund selectByPrimaryKey(Integer id);

    /**
     * 动态字段,根据主键来更新符合条件的数据库记录,t_refund
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Refund record);

    /**
     * 根据主键来更新符合条件的数据库记录,t_refund
     *
     * @param record
     */
    int updateByPrimaryKey(Refund record);

    /**
     * @param refCode
     * @return
     */
    Refund selectByRefCode(@Param("ref_code") String refCode);

    /**
     * @param orderId
     * @param orderCode
     * @param platformId
     * @param seqId
     * @return
     */
    Refund selectBySeqId(@Param("order_Id") Integer orderId,
                         @Param("order_Code") String orderCode,
                         @Param("platform_Id") Integer platformId,
                         @Param("seqId") String seqId,
                         @Param("prodType") Integer prodType);

    /**
     * 根据实体类查询数量
     * @param record
     * @return
     */
    Integer selectCount(Refund record);



    List<Refund> selectRefundByOrder(@Param("orderId") Integer orderId,
                                       @Param("orderCode") String orderCode);
}