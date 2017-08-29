package com.all580.voucherplatform.manager.report;

import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.PlatformReport;
import com.all580.voucherplatform.utils.MapUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-08-12.
 */
@Component
@Slf4j
public class PlatformReportManager {
    @Autowired
    private PlatformReportMapper platformReportMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ConsumeMapper consumeMapper;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private ReverseMapper reverseMapper;
    @Autowired
    private PlatformProductMapper platformProductMapper;

    public PlatformReportManager() {}

    private Date parseDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    private Date parseMaxDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public void execute(Date date) {
        Date startTime = parseDateTime(date);
        Date endTime = parseMaxDateTime(startTime);
        Integer total = platformProductMapper.selectPlatformProdCount(null, null, null, null, null, null);
        Integer record_start = 0;
        Integer record_count = 10;
        Integer pageSize = total % record_count > 0 ? (total / record_count) : (total % record_count + 1);
        pageSize = pageSize > 0 ? pageSize : 1;
        for (Integer i = 0; i < pageSize; i++) {
            record_start = record_count * i;
            List<Map> mapList = platformProductMapper.selectPlatformProdList(null, null, null, null, null, null,
                    record_start,
                    record_count);
            for (Map map : mapList) {
                Integer platformProdId = CommonUtil.objectParseInteger(map.get("id"));
                Integer platformId = CommonUtil.objectParseInteger(map.get("platform_id"));
                PlatformProdReport(startTime, endTime, platformId, platformProdId);
            }
        }
    }

    private void PlatformProdReport(Date startTime,
                                    Date endTime,
                                    Integer platformId,
                                    Integer platformProdId) {

        Map orderCountMap = MapUtils.listToMap(
                orderMapper.selectOrderReportCount(startTime, endTime, platformProdId, null), "hour", "number");
        Map orderNumberMap = MapUtils.listToMap(
                orderMapper.selectOrderReportNumber(startTime, endTime, platformProdId, null), "hour", "number");
        Map consumeCountMap = MapUtils.listToMap(
                consumeMapper.selectOrderConsumeReportCount(startTime, endTime, platformProdId,
                        null), "hour", "number");
        Map consumeNumberMap = MapUtils.listToMap(
                consumeMapper.selectOrderConsumeReportNumber(startTime, endTime, platformProdId,
                        null), "hour", "number");
        Map refundCountMap = MapUtils.listToMap(
                refundMapper.selectOrderRefundReportCount(startTime, endTime, platformProdId, null,
                        null), "hour", "number");
        Map refundNumberMap = MapUtils.listToMap(
                refundMapper.selectOrderRefundReportNumber(startTime, endTime, platformProdId, null,
                        null), "hour", "number");
        Map reverseCountMap = MapUtils.listToMap(
                reverseMapper.selectOrderReverseReportCount(startTime, endTime, platformProdId,
                        null), "hour", "number");
        Map reverseNumberMap = MapUtils.listToMap(
                reverseMapper.selectOrderReverseReportNumber(startTime, endTime, platformProdId,
                        null), "hour", "number");


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONDAY) + 1;
        Integer day = calendar.get(Calendar.DAY_OF_MONTH);
        Integer week = calendar.get(Calendar.DAY_OF_WEEK);


        for (Integer i = 0; i < 24; i++) {
            if (platformReportMapper.selectPlatformReportCount(platformId, platformProdId, year, month, day, i) == 0) {
                PlatformReport platformReport = getPlatformReport(i, orderCountMap, orderNumberMap,
                        consumeCountMap,
                        consumeNumberMap, refundCountMap, refundNumberMap, reverseCountMap, reverseNumberMap);

                platformReport.setYear(year);
                platformReport.setMonth(month);
                platformReport.setDay(day);
                platformReport.setHour(i);
                platformReport.setWeek(week);
                platformReport.setPlatformId(platformId);
                platformReport.setPlatformProdId(platformProdId);
                platformReportMapper.insertSelective(platformReport);
            }

        }

    }

    public PlatformReport getPlatformReport(Integer hour,
                                            Map orderCountMap,
                                            Map orderNumberMap,
                                            Map consumeCountMap,
                                            Map consumeNumberMap,
                                            Map refundCountMap,
                                            Map refundNumberMap,
                                            Map reverseCountMap,
                                            Map reverseNumberMap) {
        PlatformReport platformReport = new PlatformReport();
        Object key = hour;
        platformReport.setOrderCount(getMapValue(orderCountMap, key, 0));
        platformReport.setOrderNumber(getMapValue(orderNumberMap, key, 0));
        platformReport.setConsumeCount(getMapValue(consumeCountMap, key, 0));
        platformReport.setConsumeNumber(getMapValue(consumeNumberMap, key, 0));
        platformReport.setRefundCount(getMapValue(refundCountMap, key, 0));
        platformReport.setRefundNumbe(getMapValue(refundNumberMap, key, 0));
        platformReport.setReverseCount(getMapValue(reverseCountMap, key, 0));
        platformReport.setReverseNumber(getMapValue(reverseNumberMap, key, 0));
        return platformReport;
    }

    public Integer getMapValue(Map map,
                               Object key,
                               Integer defaultValue) {
        if (map != null && map.containsKey(key)) {
            return CommonUtil.objectParseInteger(map.get(key), defaultValue);
        }
        return defaultValue;
    }
}
