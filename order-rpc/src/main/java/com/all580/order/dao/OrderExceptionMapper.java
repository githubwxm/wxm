package com.all580.order.dao;

import com.all580.order.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderExceptionMapper {
    List<Map<String,Object>> selectOrderSendException();
    List<Map<String,Object>> selectOrderFefundException();
    int insertOrderException(List<Map<String,Object>> list);
}