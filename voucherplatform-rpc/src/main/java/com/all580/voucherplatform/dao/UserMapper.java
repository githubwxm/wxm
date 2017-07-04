package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    /**
     *  根据主键删除数据库的记录,t_user
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_user
     *
     * @param record
     */
    int insert(User record);

    /**
     *  动态字段,写入数据库记录,t_user
     *
     * @param record
     */
    int insertSelective(User record);

    /**
     *  根据指定主键获取一条数据库记录,t_user
     *
     * @param id
     */
    User selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_user
     *
     * @param record
     */
    int updateByPrimaryKeySelective(User record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_user
     *
     * @param record
     */
    int updateByPrimaryKey(User record);

    User selectByName(@Param("userName") String userName);
}