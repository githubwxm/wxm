package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.supply.ticketV3.manager.OrderManager;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.Order;
import com.sun.tools.corba.se.idl.constExpr.Or;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Linv2 on 2017-06-14.
 */

@Slf4j
@Service
public class OrderSupplyReceiveManager {
    @Autowired
    private OrderMapper orderMapper;

    public void Receive(String orderCode, String supplyOrderId) {
        Order order = orderMapper.selectByOrderCode(orderCode);
        Receive(order.getId(), supplyOrderId);
    }

    public void Receive(Integer orderId, String supplyOrderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setSupplyOrderId(supplyOrderId);
        orderMapper.updateByPrimaryKeySelective(order);
    }

}
