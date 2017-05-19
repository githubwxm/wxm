package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.DeviceGroup;

public interface DeviceGroupMapper {
    /**
     *  根据主键删除数据库的记录,t_device_group
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_device_group
     *
     * @param record
     */
    int insert(DeviceGroup record);

    /**
     *  动态字段,写入数据库记录,t_device_group
     *
     * @param record
     */
    int insertSelective(DeviceGroup record);

    /**
     *  根据指定主键获取一条数据库记录,t_device_group
     *
     * @param id
     */
    DeviceGroup selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_device_group
     *
     * @param record
     */
    int updateByPrimaryKeySelective(DeviceGroup record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_device_group
     *
     * @param record
     */
    int updateByPrimaryKey(DeviceGroup record);
}