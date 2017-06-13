package com.all580.voucherplatform.service;

import com.all580.voucherplatform.api.service.OrderService;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.manager.order.CreateOrderManager;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
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
    @Autowired
    ApplicationContext applicationContext;

    @Override
    public Result createOrder(Integer platformId, Map map) {
        CreateOrderManager createOrderManager = applicationContext.getBean(CreateOrderManager.class);
        try {
            createOrderManager.setProd(platformId, map);
            createOrderManager.setVisitor((List<Map>) map.get("visitor"));
            createOrderManager.saveOrder();
        } catch (Exception ex) {
            throw new ApiException(ex.getMessage(), ex);
        }
        return new Result(true);
    }

    @Override
    public Result getOrder(int id) {
        Result result = new Result();
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order != null) {
            result.put(order);
        }
        return result;
    }

    @Override
    public Result getOrder(String orderCode) {

        Result result = new Result();
        Order order = orderMapper.selectByOrderCode(orderCode);
        if (order != null) {
            result.put(order);
        }
        return result;
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
    public List<Map> getOrderList(Integer platformId, Integer supplierId, String orderCode, String platformOrderId, String mobile, String idNumber, String voucherNumber, Integer status, Date startTime, Date endTime, Integer recordStart, Integer recordCount) {
        return orderMapper.getOrderList(platformId, supplierId, orderCode, platformOrderId, mobile, idNumber, voucherNumber, status, startTime, endTime, recordStart, recordCount);
    }
}
