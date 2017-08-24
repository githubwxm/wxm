package com.all580.voucherplatform.manager.report;

import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.SupplyReport;
import com.all580.voucherplatform.utils.MapUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-08-12.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class SupplyReportManager {
    @Autowired
    private SupplyReportMapper supplyReportMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ConsumeMapper consumeMapper;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private ReverseMapper reverseMapper;
    @Autowired
    private SupplyProductMapper supplyProductMapper;

    public SupplyReportManager() {}

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
        Integer total = supplyProductMapper.selectSupplyProdCount(null, null);
        Integer record_start = 0;
        Integer record_count = 10;
        Integer pageSize = total % record_count > 0 ? (total / record_count) : (total % record_count + 1);
        pageSize = pageSize > 0 ? pageSize : 1;
        for (Integer i = 0; i < pageSize; i++) {
            record_start = record_count * i;
            List<Map> mapList = supplyProductMapper.selectSupplyProdList(null, null,
                    record_start,
                    record_count);
            for (Map map : mapList) {
                Integer supplyProdId = CommonUtil.objectParseInteger(map.get("id"));
                Integer supplyId = CommonUtil.objectParseInteger(map.get("supply_id"));
                SupplyProdReport(startTime, endTime, supplyId, supplyProdId);
            }
        }
    }

    private void SupplyProdReport(Date startTime,
                                  Date endTime,
                                  Integer supplyId,
                                  Integer supplyProdId) {

        Map orderCountMap = MapUtils.listToMap(
                orderMapper.selectOrderReportCount(startTime, endTime, null, supplyProdId), "hour", "number");
        Map orderNumberMap = MapUtils.listToMap(
                orderMapper.selectOrderReportNumber(startTime, endTime, null, supplyProdId), "hour", "number");
        Map consumeCountMap = MapUtils.listToMap(
                consumeMapper.selectOrderConsumeReportCount(startTime, endTime, null,
                        supplyProdId), "hour", "number");
        Map consumeNumberMap = MapUtils.listToMap(
                consumeMapper.selectOrderConsumeReportNumber(startTime, endTime, null,
                        supplyProdId), "hour", "number");
        Map refundCountMap = MapUtils.listToMap(
                refundMapper.selectOrderRefundReportCount(startTime, endTime, null, supplyProdId,
                        null), "hour", "number");
        Map refundNumberMap = MapUtils.listToMap(
                refundMapper.selectOrderRefundReportNumber(startTime, endTime, null, supplyProdId,
                        null), "hour", "number");
        Map reverseCountMap = MapUtils.listToMap(
                reverseMapper.selectOrderReverseReportCount(startTime, endTime, null,
                        supplyProdId), "hour", "number");
        Map reverseNumberMap = MapUtils.listToMap(
                reverseMapper.selectOrderReverseReportNumber(startTime, endTime, null,
                        supplyProdId), "hour", "number");


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONDAY) + 1;
        Integer day = calendar.get(Calendar.DAY_OF_MONTH);
        Integer week = calendar.get(Calendar.DAY_OF_WEEK);


        for (Integer i = 0; i < 24; i++) {
            if (supplyReportMapper.selectSupplyReportCount(supplyId, supplyProdId, year, month, day, i) == 0) {
                SupplyReport supplyReport = getSupplyReport(i, orderCountMap, orderNumberMap,
                        consumeCountMap,
                        consumeNumberMap, refundCountMap, refundNumberMap, reverseCountMap, reverseNumberMap);

                supplyReport.setYear(year);
                supplyReport.setMonth(month);
                supplyReport.setDay(day);
                supplyReport.setHour(i);
                supplyReport.setWeek(week);
                supplyReport.setSupplyId(supplyId);
                supplyReport.setSupplyProdId(supplyProdId);
                supplyReportMapper.insertSelective(supplyReport);
            }

        }

    }

    public SupplyReport getSupplyReport(Integer hour,
                                        Map orderCountMap,
                                        Map orderNumberMap,
                                        Map consumeCountMap,
                                        Map consumeNumberMap,
                                        Map refundCountMap,
                                        Map refundNumberMap,
                                        Map reverseCountMap,
                                        Map reverseNumberMap) {
        SupplyReport supplyReport = new SupplyReport();
        Object key = hour;
        supplyReport.setOrderCount(getMapValue(orderCountMap, key, 0));
        supplyReport.setOrderNumber(getMapValue(orderNumberMap, key, 0));
        supplyReport.setConsumeCount(getMapValue(consumeCountMap, key, 0));
        supplyReport.setConsumeNumber(getMapValue(consumeNumberMap, key, 0));
        supplyReport.setRefundCount(getMapValue(refundCountMap, key, 0));
        supplyReport.setRefundNumbe(getMapValue(refundNumberMap, key, 0));
        supplyReport.setReverseCount(getMapValue(reverseCountMap, key, 0));
        supplyReport.setReverseNumber(getMapValue(reverseNumberMap, key, 0));
        return supplyReport;
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