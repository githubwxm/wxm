package com.all580.payment.dao;

import com.all580.payment.entity.CapitalSerial;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CapitalSerialMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CapitalSerial record);

    int insertBatch(@Param("records") List<CapitalSerial> records);

    int insertSelective(CapitalSerial record);

    CapitalSerial selectByPrimaryKey(Integer id);

    List<Map<String,String>> listByCapitalId(@Param("capitalId") Integer capitalId,
                                             @Param("startRecord")int startRecord,
                                             @Param("maxRecords")int maxRecords);

    int countByCapitalId(@Param("capitalId") Integer capitalId);

    int updateByPrimaryKeySelective(CapitalSerial record);

    int updateByPrimaryKey(CapitalSerial record);
}