package com.all580.order.dao;

import java.util.List;
import java.util.Map;

public interface OrderExceptionMapper {
    List<Map<String,Object>> selectOrderSendException();
    List<Map<String,Object>> selectOrderFefundException();
    List<Map<String,Object>> selectOrderNumException();
    List<Map<String,Object>> selectOrderException(Map<String, Object> map);
    int selectOrderExceptionCount(Map<String, Object> map);
    int insertOrderException(List<Map<String, Object>> list);
}