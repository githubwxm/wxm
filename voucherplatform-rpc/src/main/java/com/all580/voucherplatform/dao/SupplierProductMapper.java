package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.SupplierProduct;

public interface SupplierProductMapper {
    /**
     *  根据主键删除数据库的记录,t_supplierproduct
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_supplierproduct
     *
     * @param record
     */
    int insert(SupplierProduct record);

    /**
     *  动态字段,写入数据库记录,t_supplierproduct
     *
     * @param record
     */
    int insertSelective(SupplierProduct record);

    /**
     *  根据指定主键获取一条数据库记录,t_supplierproduct
     *
     * @param id
     */
    SupplierProduct selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_supplierproduct
     *
     * @param record
     */
    int updateByPrimaryKeySelective(SupplierProduct record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_supplierproduct
     *
     * @param record
     */
    int updateByPrimaryKey(SupplierProduct record);
}