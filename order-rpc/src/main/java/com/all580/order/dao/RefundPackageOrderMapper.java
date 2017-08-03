package com.all580.order.dao;

import com.all580.order.entity.RefundPackageOrder;
import org.apache.ibatis.annotations.Param;

public interface RefundPackageOrderMapper {
    /**
     *  根据主键删除数据库的记录,t_refund_package_order
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_refund_package_order
     *
     * @param record
     */
    int insert(RefundPackageOrder record);

    /**
     *  动态字段,写入数据库记录,t_refund_package_order
     *
     * @param record
     */
    int insertSelective(RefundPackageOrder record);

    /**
     *  根据指定主键获取一条数据库记录,t_refund_package_order
     *
     * @param id
     */
    RefundPackageOrder selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_refund_package_order
     *
     * @param record
     */
    int updateByPrimaryKeySelective(RefundPackageOrder record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_refund_package_order
     *
     * @param record
     */
    int updateByPrimaryKey(RefundPackageOrder record);

    RefundPackageOrder selectByItemIdAndOuter(@Param("itemId") Integer itemId, @Param("outer") String outer);
}