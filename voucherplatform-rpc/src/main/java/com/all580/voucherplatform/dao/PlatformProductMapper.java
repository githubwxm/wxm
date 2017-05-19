package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.PlatformProduct;

public interface PlatformProductMapper {
    /**
     *  根据主键删除数据库的记录,t_platformproduct
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_platformproduct
     *
     * @param record
     */
    int insert(PlatformProduct record);

    /**
     *  动态字段,写入数据库记录,t_platformproduct
     *
     * @param record
     */
    int insertSelective(PlatformProduct record);

    /**
     *  根据指定主键获取一条数据库记录,t_platformproduct
     *
     * @param id
     */
    PlatformProduct selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_platformproduct
     *
     * @param record
     */
    int updateByPrimaryKeySelective(PlatformProduct record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_platformproduct
     *
     * @param record
     */
    int updateByPrimaryKey(PlatformProduct record);
}