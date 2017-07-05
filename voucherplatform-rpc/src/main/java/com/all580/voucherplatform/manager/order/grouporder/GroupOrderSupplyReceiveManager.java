package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.utils.async.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Linv2 on 2017-06-14.
 */

@Slf4j
@Service
public class GroupOrderSupplyReceiveManager {
    @Autowired
    private GroupOrderMapper orderMapper;
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;

    public void Receive(String orderCode, String supplyOrderId) {
        GroupOrder order = orderMapper.selectByOrderCode(orderCode);
        Receive(order, supplyOrderId);
        notifyPlatform(order.getPlatform_id(),order.getId());
    }

    public void Receive(Integer orderId, String supplyOrderId) {
        GroupOrder order = orderMapper.selectByPrimaryKey(orderId);
        Receive(order, supplyOrderId);
        notifyPlatform(order.getPlatform_id(),order.getId());
    }
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
    private void Receive(GroupOrder order, String supplyOrderId){
        GroupOrder groupOrder = new GroupOrder();
        groupOrder.setId(order.getId());
        groupOrder.setStatus(VoucherConstant.OrderSyncStatus.SYNCED);
        groupOrder.setSupplyOrderId(supplyOrderId);
        orderMapper.updateByPrimaryKeySelective(groupOrder);
    }


    private void notifyPlatform(final Integer platformId, final Integer groupOrderId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.sendGroupOrder(groupOrderId);
                }
            }
        });
    }

}
