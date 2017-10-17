package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.ConsumeSync;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ConsumeSyncMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ConsumeSync record);

    int insertSelective(ConsumeSync record);

    ConsumeSync selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConsumeSync record);

    int updateByPrimaryKey(ConsumeSync record);

    List<Map> selectByTimeAndPlatformRole(@Param("auths") List auths,
                                     @Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime,
                                     @Param("record_start") Integer recordStart,
                                     @Param("record_count") Integer recordCount);

    int selectByTimeAndPlatformRoleCount(@Param("auths") List auths,
                                         @Param("startTime") Date startTime,
                                         @Param("endTime") Date endTime);
}