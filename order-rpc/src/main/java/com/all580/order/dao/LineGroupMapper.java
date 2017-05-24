package com.all580.order.dao;

import com.all580.order.entity.LineGroup;
import org.apache.ibatis.annotations.Param;

public interface LineGroupMapper {
    /**
     *  根据主键删除数据库的记录,t_line_group
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_line_group
     *
     * @param record
     */
    int insert(LineGroup record);

    /**
     *  动态字段,写入数据库记录,t_line_group
     *
     * @param record
     */
    int insertSelective(LineGroup record);

    /**
     *  根据指定主键获取一条数据库记录,t_line_group
     *
     * @param id
     */
    LineGroup selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_line_group
     *
     * @param record
     */
    int updateByPrimaryKeySelective(LineGroup record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_line_group
     *
     * @param record
     */
    int updateByPrimaryKey(LineGroup record);

    /**
     * 根据团号查询
     * @param number
     * @param epId
     * @param coreEpId
     * @return
     */
    LineGroup selectByNumber(@Param("number") String number, @Param("epId") Integer epId, @Param("coreEpId") Integer coreEpId);
}