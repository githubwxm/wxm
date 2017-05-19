package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Supplier;

public interface SupplierMapper {
    /**
     *  根据主键删除数据库的记录,t_supplier
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_supplier
     *
     * @param record
     */
    int insert(Supplier record);

    /**
     *  动态字段,写入数据库记录,t_supplier
     *
     * @param record
     */
    int insertSelective(Supplier record);

    /**
     *  根据指定主键获取一条数据库记录,t_supplier
     *
     * @param id
     */
    Supplier selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_supplier
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Supplier record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_supplier
     *
     * @param record
     */
    int updateByPrimaryKey(Supplier record);
}