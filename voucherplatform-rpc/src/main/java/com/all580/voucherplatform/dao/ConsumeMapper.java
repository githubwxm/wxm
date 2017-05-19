package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Consume;

public interface ConsumeMapper {
    /**
     *  根据主键删除数据库的记录,t_consume
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_consume
     *
     * @param record
     */
    int insert(Consume record);

    /**
     *  动态字段,写入数据库记录,t_consume
     *
     * @param record
     */
    int insertSelective(Consume record);

    /**
     *  根据指定主键获取一条数据库记录,t_consume
     *
     * @param id
     */
    Consume selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_consume
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Consume record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_consume
     *
     * @param record
     */
    int updateByPrimaryKey(Consume record);
}