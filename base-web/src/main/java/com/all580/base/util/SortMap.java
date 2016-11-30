package com.all580.base.util;

import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by wxming on 2016/11/30 0030.
 */
public class SortMap {
    /** * 对Map按key进行排序，支持map和list混合嵌套 * * @param map * @return */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null ;
        }
        Map<String, Object> sortMap = new TreeMap<String, Object>(
                new MapComparator());
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                sortMap.put(entry.getKey(),
                        sortMapByKey((Map) entry.getValue()));
                continue;
            }
            if (entry.getValue() instanceof List) {
                sortMap.put(entry.getKey(), sortListByValue((List) entry
                        .getValue()));
                continue;
            }
            if (entry.getValue() instanceof String[]) {
                String[] temp = (String[]) entry.getValue();
                sortMap.put(entry.getKey(),
                        sortListByValue(Arrays.asList(temp)));
                continue;
            }
            if (entry.getValue() instanceof String) {
                sortMap.put(entry.getKey(), entry.getValue());
                continue;
            }
            if (entry.getValue() instanceof Boolean) {
                sortMap.put(entry.getKey(), entry.getValue().toString());
                continue;
            }
            if (entry.getValue() instanceof Number) {
                sortMap.put(entry.getKey(), entry.getValue());
                continue;
            }
            if (entry.getValue() instanceof Timestamp) {
                sortMap.put(entry.getKey(), DateFormatUtils.converToDateTime(entry.getValue().toString()));
                continue;
            }
            if (entry.getValue() instanceof Object) {
                sortMap.put(entry.getKey(),
                        sortMapByKey( JsonUtils.obj2map(entry.getValue()) ));
                continue;
            }
            if (entry.getValue() == null) {
                sortMap.put(entry.getKey(),CommonUtil.objectParseString(entry.getValue()));
                continue;
            }
        }
        return sortMap;
    }

    /** * 对list进行排序，支持map和list混合嵌套 * * @param list * @return */
    public static List<?> sortListByValue(List<?> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<Object> sortList = new ArrayList<Object>();
        for (Object item : list) {
            if (item instanceof Map) {
                sortList.add(sortMapByKey((Map) item));
                continue;
            }
            if (item instanceof List) {
                sortList.add(sortListByValue((List) item));
                continue;
            }
            if (item instanceof String[]) {
                sortList.add(sortListByValue(Arrays.asList((String[]) item)));
                continue;
            }
            if (item instanceof String) {
                sortList.add(item);
                continue;
            }
            if (item instanceof Boolean) {
                sortList.add(item);
                continue;
            }
            if (item instanceof Number) {
                sortList.add(item);
                continue;
            }
        }
       // Collections.sort(sortList, new MapComparator());
        return sortList;
    }

}
