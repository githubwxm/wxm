package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.GroupOrder;

public interface GroupOrderMapper {
    /**
     *  根据主键删除数据库的记录,t_group_order
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_group_order
     *
     * @param record
     */
    int insert(GroupOrder record);

    /**
     *  动态字段,写入数据库记录,t_group_order
     *
     * @param record
     */
    int insertSelective(GroupOrder record);

    /**
     *  根据指定主键获取一条数据库记录,t_group_order
     *
     * @param id
     */
    GroupOrder selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_group_order
     *
     * @param record
     */
    int updateByPrimaryKeySelective(GroupOrder record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_group_order
     *
     * @param record
     */
    int updateByPrimaryKey(GroupOrder record);
}