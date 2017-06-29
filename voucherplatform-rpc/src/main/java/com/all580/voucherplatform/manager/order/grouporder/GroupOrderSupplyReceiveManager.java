package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.utils.sign.async.AsyncService;
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
    @Autowired
    private AdapterLoadder adapterLoadder;
    @Autowired
    private AsyncService asyncService;

    public void Receive(String orderCode, String supplyOrderId) {
        GroupOrder order = orderMapper.selectByOrderCode(orderCode);
        Receive(order, supplyOrderId);
    }

    private void Receive(Integer orderId, String supplyOrderId) {
        GroupOrder order = orderMapper.selectByPrimaryKey(orderId);
        Receive(order, supplyOrderId);
    }
    public void Receive(GroupOrder order, String supplyOrderId){
        GroupOrder groupOrder = new GroupOrder();
        groupOrder.setId(order.getId());
        groupOrder.setSupplyOrderId(supplyOrderId);
        orderMapper.updateByPrimaryKeySelective(groupOrder);
        notifyPlatform(order.getPlatform_id(),order.getId());
    }


    public void notifyPlatform(final Integer platformId, final Integer groupOrderId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoadder.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.sendGroupOrder(groupOrderId);
                }
            }
        });
    }

}
