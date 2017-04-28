package com.all580.order.dao;

import com.all580.order.entity.ShippingModify;
import org.apache.ibatis.annotations.Param;

public interface ShippingModifyMapper {
    /**
     *  根据主键删除数据库的记录,t_shipping_modify
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_shipping_modify
     *
     * @param record
     */
    int insert(ShippingModify record);

    /**
     *  动态字段,写入数据库记录,t_shipping_modify
     *
     * @param record
     */
    int insertSelective(ShippingModify record);

    /**
     *  根据指定主键获取一条数据库记录,t_shipping_modify
     *
     * @param id
     */
    ShippingModify selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_shipping_modify
     *
     * @param record
     */
    int updateByPrimaryKeySelective(ShippingModify record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_shipping_modify
     *
     * @param record
     */
    int updateByPrimaryKey(ShippingModify record);

    int modifyed(@Param("orderId") Integer orderId);
}