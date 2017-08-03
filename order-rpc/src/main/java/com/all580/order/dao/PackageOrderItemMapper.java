package com.all580.order.dao;

import com.all580.order.entity.PackageOrderItem;
import org.apache.ibatis.annotations.Param;

public interface PackageOrderItemMapper {
    /**
     *  根据主键删除数据库的记录,t_package_order_item
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_package_order_item
     *
     * @param record
     */
    int insert(PackageOrderItem record);

    /**
     *  动态字段,写入数据库记录,t_package_order_item
     *
     * @param record
     */
    int insertSelective(PackageOrderItem record);

    /**
     *  根据指定主键获取一条数据库记录,t_package_order_item
     *
     * @param id
     */
    PackageOrderItem selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_package_order_item
     *
     * @param record
     */
    int updateByPrimaryKeySelective(PackageOrderItem record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_package_order_item
     *
     * @param record
     */
    int updateByPrimaryKey(PackageOrderItem record);

    PackageOrderItem selectByNumber(@Param("number") Long number);
}