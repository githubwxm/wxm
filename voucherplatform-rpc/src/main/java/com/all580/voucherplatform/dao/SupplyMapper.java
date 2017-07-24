package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Supply;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SupplyMapper {
    /**
     * 根据主键删除数据库的记录,t_supply
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新写入数据库记录,t_supply
     *
     * @param record
     */
    int insert(Supply record);

    /**
     * 动态字段,写入数据库记录,t_supply
     *
     * @param record
     */
    int insertSelective(Supply record);

    /**
     * 根据指定主键获取一条数据库记录,t_supply
     *
     * @param id
     */
    Supply selectByPrimaryKey(Integer id);

    /**
     * 动态字段,根据主键来更新符合条件的数据库记录,t_supply
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Supply record);

    /**
     * 根据主键来更新符合条件的数据库记录,t_supply
     *
     * @param record
     */
    int updateByPrimaryKey(Supply record);

    int selectSupplyCount(@Param("name") String name);

    List<Map> selectSupplyList(@Param("name") String name, @Param("record_start") Integer recordStart, @Param("record_count") Integer recordCount);


    Map selectSupplyMapByPrimaryKeySelective(Integer supplyId);

}