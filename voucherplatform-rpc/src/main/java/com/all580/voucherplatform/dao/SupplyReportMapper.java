package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.SupplyReport;
import org.apache.ibatis.annotations.Param;

public interface SupplyReportMapper {
    /**
     *  根据主键删除数据库的记录,t_supply_report
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_supply_report
     *
     * @param record
     */
    int insert(SupplyReport record);

    /**
     *  动态字段,写入数据库记录,t_supply_report
     *
     * @param record
     */
    int insertSelective(SupplyReport record);

    /**
     *  根据指定主键获取一条数据库记录,t_supply_report
     *
     * @param id
     */
    SupplyReport selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_supply_report
     *
     * @param record
     */
    int updateByPrimaryKeySelective(SupplyReport record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_supply_report
     *
     * @param record
     */
    int updateByPrimaryKey(SupplyReport record);

    Integer selectSupplyReportCount(
            @Param("platformId") Integer platformId,
            @Param("platformProdId") Integer platformProdId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("day") Integer day,
            @Param("hour") Integer hour);
}