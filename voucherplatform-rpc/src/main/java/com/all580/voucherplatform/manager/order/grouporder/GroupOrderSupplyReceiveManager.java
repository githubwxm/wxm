package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Linv2 on 2017-06-14.
 */

@Slf4j
@Service
public class GroupOrderSupplyReceiveManager {
    @Autowired
    private GroupOrderMapper orderMapper;

    public void Receive(String orderCode, String supplyOrderId) {
        GroupOrder order = orderMapper.selectByOrderCode(orderCode);
        Receive(order.getId(), supplyOrderId);
    }

    public void Receive(Integer orderId, String supplyOrderId) {
        GroupOrder order = new GroupOrder();
        order.setId(orderId);
        order.setSupplyOrderId(supplyOrderId);
        orderMapper.updateByPrimaryKeySelective(order);
    }

}
