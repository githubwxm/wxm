package com.all580.order.service;

import com.all580.order.api.service.OrderService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务
 * @date 2016/10/22 17:32
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result<Integer> getPayeeEpIdByOutTransId(String outTranId) {
        Order order = orderMapper.selectByThirdSn(outTranId);
        if (order == null) {
            return new Result<>(false, "订单不存在");
        }
        Result<Integer> result = new Result<>(true);
        result.put(order.getPayeeEpId());
        return result;
    }
}
