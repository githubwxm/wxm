package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Device;
import com.all580.voucherplatform.entity.DeviceProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DeviceProductMapper {
    /**
     *  根据主键删除数据库的记录,t_device_product
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_device_product
     *
     * @param record
     */
    int insert(DeviceProduct record);

    /**
     *  动态字段,写入数据库记录,t_device_product
     *
     * @param record
     */
    int insertSelective(DeviceProduct record);

    /**
     *  根据指定主键获取一条数据库记录,t_device_product
     *
     * @param id
     */
    DeviceProduct selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_device_product
     *
     * @param record
     */
    int updateByPrimaryKeySelective(DeviceProduct record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_device_product
     *
     * @param record
     */
    int updateByPrimaryKey(DeviceProduct record);


    DeviceProduct selectByProdId(@Param("groupId") Integer groupId, @Param("supplyProdId") Integer supplyProdId);

    List<Map> selectProdList(@Param("groupId") Integer groupId);
}