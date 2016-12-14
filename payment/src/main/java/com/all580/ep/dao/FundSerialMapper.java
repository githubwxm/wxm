package com.all580.ep.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FundSerialMapper {
    //@Param("id") Integer id,@Param("summary") String summary
    int updateFundSerialSummary(Map<String,Object> params);
    int insertFundSerial(Map<String,Object> params);
    List<Map<String,Object>> selectFundSerial(@Param("core_ep_id") Integer core_ep_id,
                                              @Param("status") String status,
                                              @Param("start_date") String start_date,
                                              @Param("end_date") String end_date,
                                              @Param("ref_id") String ref_id,
                                              @Param("start_record")Integer start_record,
                                              @Param("max_records")Integer max_records);
    List<Map<String,Object>> selectFundSerialExport(@Param("core_ep_id") Integer core_ep_id,
                                              @Param("status") String status,
                                              @Param("start_date") String start_date,
                                              @Param("end_date") String end_date,
                                              @Param("ref_id") String ref_id,
                                              @Param("start_record")Integer start_record,
                                              @Param("max_records")Integer max_records);
    int selectFundSerialCount(@Param("core_ep_id") Integer core_ep_id,
                                              @Param("status") String status,
                                              @Param("start_date") String start_date,
                                              @Param("end_date") String end_date,
                                              @Param("ref_id") String ref_id,
                                              @Param("start_record")Integer start_record,
                                              @Param("max_records")Integer max_records);

}