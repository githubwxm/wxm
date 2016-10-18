package com.all580.payment.dao;

import com.all580.payment.entity.CapitalSerial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CapitalSerialMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CapitalSerial record);

    int insertBatch(@Param("records") List<CapitalSerial> records);

    int insertSelective(CapitalSerial record);

    CapitalSerial selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CapitalSerial record);

    int updateByPrimaryKey(CapitalSerial record);
}