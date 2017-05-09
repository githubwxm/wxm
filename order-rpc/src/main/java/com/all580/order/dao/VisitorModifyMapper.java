package com.all580.order.dao;

import com.all580.order.entity.VisitorModify;
import org.apache.ibatis.annotations.Param;

public interface VisitorModifyMapper {
    /**
     *  根据主键删除数据库的记录,t_visitor_modify
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_visitor_modify
     *
     * @param record
     */
    int insert(VisitorModify record);

    /**
     *  动态字段,写入数据库记录,t_visitor_modify
     *
     * @param record
     */
    int insertSelective(VisitorModify record);

    /**
     *  根据指定主键获取一条数据库记录,t_visitor_modify
     *
     * @param id
     */
    VisitorModify selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_visitor_modify
     *
     * @param record
     */
    int updateByPrimaryKeySelective(VisitorModify record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_visitor_modify
     *
     * @param record
     */
    int updateByPrimaryKey(VisitorModify record);

    int modifyed(@Param("itemId") Integer itemId);
}