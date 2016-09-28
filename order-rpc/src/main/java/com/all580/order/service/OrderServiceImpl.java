package com.all580.order.service;

import com.all580.order.api.service.OrderService;
import com.all580.order.dao.OrderMapper;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务实现
 * @date 2016/9/28 9:23
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result<?> create(Map params) {
        orderMapper.selectByPrimaryKey(0L);
        return null;
    }

    @Override
    public Result<?> audit(Map params) {
        return null;
    }

    @Override
    public Result<?> payment(Map params) {
        return null;
    }

    @Override
    public Result<?> resendTicket(Map params) {
        return null;
    }

    @Override
    public Result<?> cancel(Map params) {
        return null;
    }

    @Override
    public Result<?> refundApply(Map params) {
        return null;
    }

    @Override
    public Result<?> refundAudit(Map params) {
        return null;
    }
}
