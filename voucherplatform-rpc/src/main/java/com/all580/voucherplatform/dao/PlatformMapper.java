package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.Platform;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PlatformMapper {
    /**
     *  根据主键删除数据库的记录,t_platform
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_platform
     *
     * @param record
     */
    int insert(Platform record);

    /**
     *  动态字段,写入数据库记录,t_platform
     *
     * @param record
     */
    int insertSelective(Platform record);

    /**
     *  根据指定主键获取一条数据库记录,t_platform
     *
     * @param id
     */
    Platform selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_platform
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Platform record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_platform
     *
     * @param record
     */
    int updateByPrimaryKey(Platform record);

    int selectPlatformCount(@Param("name") String name);

    List<Map> selectPlatformList(@Param("name") String name, @Param("record_start") Integer recordStart, @Param("record_count") Integer recordCount);
}