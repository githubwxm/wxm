package com.all580.voucherplatform.adapter.supply.pos;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.manager.order.OrderSupplyReceiveManager;
import com.all580.voucherplatform.manager.order.RefundResultManager;
import com.all580.voucherplatform.manager.order.grouporder.GroupOrderSupplyReceiveManager;
import com.all580.voucherplatform.manager.order.grouporder.GroupRefundResultManager;
import com.framework.common.lang.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Linv2 on 2017-06-06.
 */
@Service
public class PosAdapterImpl extends SupplyAdapterService {

    @Autowired
    private OrderSupplyReceiveManager orderSupplyReceiveManager;

    @Autowired
    private GroupOrderSupplyReceiveManager groupOrderSupplyReceiveManager;
    @Autowired
    private AdapterLoader adapterLoader;

    @Override
    public void queryProd(Integer supplyId) {

    }

    @Override
    public void sendOrder(Integer... orderId) {
        List<Map> mapList = new ArrayList<>();
        for (Integer id : orderId) {
            Map map = new HashMap();
            map.put("orderId", id);
            map.put("supplyOrderId", String.valueOf(UUIDGenerator.generateUUID()));
            mapList.add(map);
        }
        try {
            orderSupplyReceiveManager.submit(mapList);
        } catch (Exception ex) {

        }
    }

    @Override
    public void sendGroupOrder(Integer groupOrderId) {
        groupOrderSupplyReceiveManager.Receive(groupOrderId, String.valueOf(UUIDGenerator.generateUUID()));
    }

    @Override
    public void queryOrder(Integer orderId) {

    }

    @Override
    public void consume(Integer consumeId) {

    }

    @Override
    public void refund(int refundId) {
        try {
            RefundResultManager refundResultManager = adapterLoader.getBean(RefundResultManager.class);
            refundResultManager.setRefund(refundId);
            refundResultManager.automatic();
        } catch (Exception ex) {

        }
    }

    @Override
    public void update(Integer orderId) {

    }

    @Override
    public void refundGroup(Integer groupRefId) {
        try {
            GroupRefundResultManager refundResultManager = adapterLoader.getBean(GroupRefundResultManager.class);
            refundResultManager.setRefund(groupRefId);
            refundResultManager.automatic();
        } catch (Exception ex) {

        }
    }

    @Override
    public void updateGroup(Integer groupOrderId, String... seqId) {

    }
}
