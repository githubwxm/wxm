package com.all580.voucherplatform.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface OrderLogMapper {
    /**
     * 日志记录的查询
     * @return
     */
    Map selectByLog(@Param("orderId") String orderId, @Param("itemId") String itemId);
    String getPlatformOrderId(@Param("voucherId") String voucherId);
    String getGroupPlatformOrderId(@Param("voucherId") String voucherId);
}