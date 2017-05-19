package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.ProductType;

public interface ProductTypeMapper {
    /**
     *  根据主键删除数据库的记录,t_producttype
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_producttype
     *
     * @param record
     */
    int insert(ProductType record);

    /**
     *  动态字段,写入数据库记录,t_producttype
     *
     * @param record
     */
    int insertSelective(ProductType record);

    /**
     *  根据指定主键获取一条数据库记录,t_producttype
     *
     * @param id
     */
    ProductType selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_producttype
     *
     * @param record
     */
    int updateByPrimaryKeySelective(ProductType record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_producttype
     *
     * @param record
     */
    int updateByPrimaryKey(ProductType record);
}