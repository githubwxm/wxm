package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Device;

public interface DeviceMapper {
    /**
     *  根据主键删除数据库的记录,t_device
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_device
     *
     * @param record
     */
    int insert(Device record);

    /**
     *  动态字段,写入数据库记录,t_device
     *
     * @param record
     */
    int insertSelective(Device record);

    /**
     *  根据指定主键获取一条数据库记录,t_device
     *
     * @param id
     */
    Device selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_device
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Device record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_device
     *
     * @param record
     */
    int updateByPrimaryKey(Device record);
}