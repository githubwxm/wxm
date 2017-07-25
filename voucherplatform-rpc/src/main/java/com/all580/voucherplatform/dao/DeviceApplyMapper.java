package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.DeviceApply;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DeviceApplyMapper {
    /**
     * 根据主键删除数据库的记录,t_device_apply
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新写入数据库记录,t_device_apply
     *
     * @param record
     */
    int insert(DeviceApply record);

    /**
     * 动态字段,写入数据库记录,t_device_apply
     *
     * @param record
     */
    int insertSelective(DeviceApply record);

    /**
     * 根据指定主键获取一条数据库记录,t_device_apply
     *
     * @param id
     */
    DeviceApply selectByPrimaryKey(Integer id);

    /**
     * 动态字段,根据主键来更新符合条件的数据库记录,t_device_apply
     *
     * @param record
     */
    int updateByPrimaryKeySelective(DeviceApply record);

    /**
     * 根据主键来更新符合条件的数据库记录,t_device_apply
     *
     * @param record
     */
    int updateByPrimaryKey(DeviceApply record);

    /**
     * 根据指定主键获取一条数据库记录,t_device_apply
     *
     * @param code
     */
    DeviceApply selectByCode(String code);


    int selectApplyCount(@Param("code") String code, @Param("status") Integer status);

    List<Map> selectApplyList(@Param("code") String code, @Param("status") Integer status, @Param("record_start") Integer recordStart, @Param("record_count") Integer recordCount);
}