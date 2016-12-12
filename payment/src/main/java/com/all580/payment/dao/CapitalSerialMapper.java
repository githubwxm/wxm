package com.all580.payment.dao;

import com.all580.payment.entity.CapitalSerial;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CapitalSerialMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CapitalSerial record);
    int updateSummary(@Param("id")int id,@Param("summary")String summary);
    int insertBatch(@Param("records") List<CapitalSerial> records);

    int insertSelective(CapitalSerial record);

    CapitalSerial selectByPrimaryKey(Integer id);

    List<Map<String,String>> listByCapitalId(@Param("capital_id") Integer capital_id,
                                             @Param("balance_status") String balance_status,
                                             @Param("start_date") String startDate,
                                             @Param("end_date") String endDate,
                                             @Param("ref_id") String ref_id,
                                             @Param("start_record")Integer startRecord,
                                             @Param("max_records")Integer maxRecords);
    List<Map<String,String>> listByCapitalIdExport (@Param("capital_id") Integer capital_id,
                                             @Param("balance_status") String balance_status,
                                             @Param("start_date") String startDate,
                                             @Param("end_date") String endDate,
                                             @Param("ref_id") String ref_id,
                                             @Param("start_record")Integer startRecord,
                                             @Param("max_records")Integer maxRecords);

    int countByCapitalId(@Param("capital_id") Integer capital_id,
                         @Param("balance_status") String balance_status,
                          @Param("start_date") String startDate,
                         @Param("end_date") String endDate,   @Param("ref_id") String ref_id);

    int updateByPrimaryKeySelective(CapitalSerial record);

    int updateByPrimaryKey(CapitalSerial record);
}