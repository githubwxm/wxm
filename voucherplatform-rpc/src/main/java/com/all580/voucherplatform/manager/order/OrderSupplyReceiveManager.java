package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.manager.MessageManager;
import com.all580.voucherplatform.utils.sign.async.AsyncService;
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
    @Autowired
    private AdapterLoadder adapterLoadder;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private MessageManager messageManager;

    public Order Receive(String orderCode, String supplyOrderId) {
        Order order = orderMapper.selectByOrderCode(orderCode);
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setSupplyOrderId(supplyOrderId);
        orderMapper.updateByPrimaryKeySelective(updateOrder);
        messageManager.sendOrderMessage(order);
        return order;
    }

    public void notifyPlatform(final Integer platformId, final Integer... orderId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoadder.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.sendOrder(orderId);
                }
            }
        });
    }

}
