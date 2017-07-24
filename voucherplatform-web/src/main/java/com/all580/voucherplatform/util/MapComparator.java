package com.all580.voucherplatform.util;

import java.util.Comparator;

/**
 * Created by wxming on 2016/11/30 0030.
 */
public class MapComparator implements Comparator<Object> {
    public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    }
}