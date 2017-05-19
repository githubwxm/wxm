package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.GroupVisitor;

public interface GroupVisitorMapper {
    /**
     *  根据主键删除数据库的记录,t_group_visitor
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_group_visitor
     *
     * @param record
     */
    int insert(GroupVisitor record);

    /**
     *  动态字段,写入数据库记录,t_group_visitor
     *
     * @param record
     */
    int insertSelective(GroupVisitor record);

    /**
     *  根据指定主键获取一条数据库记录,t_group_visitor
     *
     * @param id
     */
    GroupVisitor selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_group_visitor
     *
     * @param record
     */
    int updateByPrimaryKeySelective(GroupVisitor record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_group_visitor
     *
     * @param record
     */
    int updateByPrimaryKey(GroupVisitor record);
}