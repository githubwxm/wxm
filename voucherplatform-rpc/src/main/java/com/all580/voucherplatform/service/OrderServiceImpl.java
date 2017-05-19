package com.all580.voucherplatform.service;

import com.all580.voucherplatform.api.service.OrderService;
import com.all580.voucherplatform.dao.OrderMapper;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/14.
 */

@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result createOrder(Map map) {

        return null;
    }

    @Override
    public Result getOrder(int id) {
        return null;
    }

    @Override
    public Result getOrder(String orderCode) {
        return null;
    }

    @Override
    public Result getOrder(int platformId, String platformOrderId) {
        return null;
    }

    @Override
    public Result updateOrder(Map map) {
        return null;
    }

    @Override
    public int getOrderCount(Integer platformId, Integer supplierId, String orderCode, String platformOrderId, String mobile, String idNumber, String voucherNumber, Integer status, Date startTime, Date endTime) {
        return orderMapper.getOrderCount(platformId, supplierId, orderCode, platformOrderId, mobile, idNumber, voucherNumber, status, startTime, endTime);
    }

    @Override
    public Result getOrderList(Integer platformId, Integer supplierId, String orderCode, String platformOrderId, String mobile, String idNumber, String voucherNumber, Integer status, Date startTime, Date endTime, Integer recordStart, Integer recordCount) {
        Result result = new Result(true);
        List<Map> list = orderMapper.getOrderList(platformId, supplierId, orderCode, platformOrderId, mobile, idNumber, voucherNumber, status, startTime, endTime, recordStart, recordCount);
        result.put(list);
        return result;
    }
}
