package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Refund;

public interface RefundMapper {
    /**
     *  根据主键删除数据库的记录,t_refund
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_refund
     *
     * @param record
     */
    int insert(Refund record);

    /**
     *  动态字段,写入数据库记录,t_refund
     *
     * @param record
     */
    int insertSelective(Refund record);

    /**
     *  根据指定主键获取一条数据库记录,t_refund
     *
     * @param id
     */
    Refund selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_refund
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Refund record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_refund
     *
     * @param record
     */
    int updateByPrimaryKey(Refund record);
}