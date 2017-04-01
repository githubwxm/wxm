package com.all580.base.util;

import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/3/31 17:40
 */
@Slf4j
public class Utils {

    public static Date[] checkDate(String start_time, String end_time) {
        Date[] result = new Date[]{null, null};
        try {
            if (start_time != null) {
                result[0] = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, start_time);
            }
            if (end_time != null) {
                result[1] = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, end_time);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Date[] checkDateTime(String start, String end) {
        Date start_time = null;
        Date end_time = null;
        try {
            start_time = start == null ? null : DateFormatUtils.parseString(DateFormatUtils.DATE_FORMAT, start);
            end_time = end == null ? null : DateFormatUtils.parseString(DateFormatUtils.DATE_FORMAT, end);
            start_time = start_time == null ? null : DateFormatUtils.setHms(start_time, "00:00:00");
            end_time = end_time == null ? null : DateFormatUtils.setHms(end_time, "23:59:59");
        } catch (Exception e) {
            log.warn("时间格式化异常", e);
        }
        return new Date[]{start_time, end_time};
    }

    public static boolean isSortType(String type) {
        return "asc".equalsIgnoreCase(type) || "desc".equalsIgnoreCase(type);
    }
}
