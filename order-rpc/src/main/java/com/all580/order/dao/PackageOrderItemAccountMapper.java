package com.all580.order.dao;

import com.all580.order.entity.PackageOrderItemAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PackageOrderItemAccountMapper {
    /**
     *  根据主键删除数据库的记录,t_package_order_item_account
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_package_order_item_account
     *
     * @param record
     */
    int insert(PackageOrderItemAccount record);

    /**
     *  动态字段,写入数据库记录,t_package_order_item_account
     *
     * @param record
     */
    int insertSelective(PackageOrderItemAccount record);

    /**
     *  根据指定主键获取一条数据库记录,t_package_order_item_account
     *
     * @param id
     */
    PackageOrderItemAccount selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_package_order_item_account
     *
     * @param record
     */
    int updateByPrimaryKeySelective(PackageOrderItemAccount record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_package_order_item_account
     *
     * @param record
     */
    int updateByPrimaryKey(PackageOrderItemAccount record);

    PackageOrderItemAccount selectByOrderItemAndEp(@Param("itemId") Integer itemId, @Param("epId") Integer epId, @Param("coreEpId") Integer coreEpId);

    /**
     * 根据套票子订单ID查询预分账记录
     * @param itemId 子订单ID
     * @return
     */
    List<PackageOrderItemAccount> selectByOrderItem(@Param("itemId") Integer itemId);
}