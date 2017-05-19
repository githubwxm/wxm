package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Order;

public interface OrderMapper {
    /**
     *  根据主键删除数据库的记录,t_order
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_order
     *
     * @param record
     */
    int insert(Order record);

    /**
     *  动态字段,写入数据库记录,t_order
     *
     * @param record
     */
    int insertSelective(Order record);

    /**
     *  根据指定主键获取一条数据库记录,t_order
     *
     * @param id
     */
    Order selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_order
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_order
     *
     * @param record
     */
    int updateByPrimaryKey(Order record);
}