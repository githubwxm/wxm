package com.all580.order.dao;

import com.all580.order.entity.RefundPackageAccount;
import org.apache.ibatis.annotations.Param;

public interface RefundPackageAccountMapper {
    /**
     *  根据主键删除数据库的记录,t_refund_package_account
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_refund_package_account
     *
     * @param record
     */
    int insert(RefundPackageAccount record);

    /**
     *  动态字段,写入数据库记录,t_refund_package_account
     *
     * @param record
     */
    int insertSelective(RefundPackageAccount record);

    /**
     *  根据指定主键获取一条数据库记录,t_refund_package_account
     *
     * @param id
     */
    RefundPackageAccount selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_refund_package_account
     *
     * @param record
     */
    int updateByPrimaryKeySelective(RefundPackageAccount record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_refund_package_account
     *
     * @param record
     */
    int updateByPrimaryKey(RefundPackageAccount record);

    RefundPackageAccount selectByOrderItemAndEp(@Param("itemId") Integer itemId, @Param("epId") Integer epId, @Param("coreEpId") Integer coreEpId);
}