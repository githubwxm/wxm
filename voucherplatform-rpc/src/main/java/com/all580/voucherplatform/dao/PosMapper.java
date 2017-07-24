package com.all580.voucherplatform.dao;

import org.apache.ibatis.annotations.Param;
import org.mapdb.Atomic;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-20.
 */
public interface PosMapper {
    /**
     * @param voucherNumber
     * @param mobile
     * @param idNumber
     * @return
     */
    int selectOrderCount(@Param("voucherNumber") String voucherNumber,
                         @Param("mobile") String mobile,
                         @Param("idNumber") String idNumber,
                         @Param("deviceGroupId") Integer deviceGroupId);

    List<Map> selectOrderList(@Param("voucherNumber") String voucherNumber,
                              @Param("mobile") String mobile,
                              @Param("idNumber") String idNumber,
                              @Param("deviceGroupId") Integer deviceGroupId,
                              @Param("recordStart") Integer recordStart,
                              @Param("recordCount") Integer recordCount);

    int selectOrderConsumeCount(@Param("voucherNumber") String voucherNumber,
                                @Param("mobile") String mobile,
                                @Param("idNumber") String idNumber,
                                @Param("deviceId") String deviceId,
                                @Param("startTime") Date startTime,
                                @Param("endTime") Date endTime);

    List<Map> selectOrderConsumeList(@Param("voucherNumber") String voucherNumber,
                                     @Param("mobile") String mobile,
                                     @Param("idNumber") String idNumber,
                                     @Param("deviceId") String deviceId,
                                     @Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime,
                                     @Param("recordStart") Integer recordStart,
                                     @Param("recordCount") Integer recordCount);

    List<Map> selectDeviceReportByDate(@Param("deviceId") String deviceId,
                                       @Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime);

    List<Map> selectDeviceReportByProduct(@Param("deviceId") String deviceId,
                                          @Param("startTime") Date startTime,
                                          @Param("endTime") Date endTime);
}
