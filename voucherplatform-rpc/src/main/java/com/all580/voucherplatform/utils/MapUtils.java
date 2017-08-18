package com.all580.voucherplatform.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-08-15.
 */
public class MapUtils {

    public static Map listToMap(List<Map> listMap,
                                String key,
                                String value) {
        Map retMap = new HashMap();
        for (Map map : listMap) {
            retMap.put(map.get(key), map.get(value));
        }
        return retMap;
    }
}
