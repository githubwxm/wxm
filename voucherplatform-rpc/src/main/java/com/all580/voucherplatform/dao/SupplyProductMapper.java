package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.SupplyProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SupplyProductMapper {
    /**
     * 根据主键删除数据库的记录,t_supplyproduct
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新写入数据库记录,t_supplyproduct
     *
     * @param record
     */
    int insert(SupplyProduct record);

    /**
     * 动态字段,写入数据库记录,t_supplyproduct
     *
     * @param record
     */
    int insertSelective(SupplyProduct record);

    /**
     * 根据指定主键获取一条数据库记录,t_supplyproduct
     *
     * @param id
     */
    SupplyProduct selectByPrimaryKey(Integer id);

    /**
     * 动态字段,根据主键来更新符合条件的数据库记录,t_supplyproduct
     *
     * @param record
     */
    int updateByPrimaryKeySelective(SupplyProduct record);

    /**
     * 根据主键来更新符合条件的数据库记录,t_supplyproduct
     *
     * @param record
     */
    int updateByPrimaryKey(SupplyProduct record);

    Map getSupplyProdByProdId(@Param("supply_Id") int supplyId, @Param("prod_Id") String prodId);

    List<Map>  getSupplyProdBySupplyId(@Param("supply_Id") int supplyId);
}