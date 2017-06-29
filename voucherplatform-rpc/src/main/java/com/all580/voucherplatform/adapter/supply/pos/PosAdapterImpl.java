package com.all580.voucherplatform.adapter.supply.pos;

import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.manager.order.OrderSupplyReceiveManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Linv2 on 2017-06-06.
 */
@Service
public class PosAdapterImpl extends SupplyAdapterService {

    @Autowired
    private OrderSupplyReceiveManager orderSupplyReceiveManager;

    @Override
    public void queryProd(Integer supplyId) {

    }

    @Override
    public void sendOrder(Integer... orderId) {
//        Integer platformId = null;
//        for (Integer id : orderId) {
//            Order order = orderSupplyReceiveManager.Receive(id, "");
//            if (platformId == null) {
//                platformId = order.getPlatform_id();
//            }
//        }
    }

    @Override
    public void sendGroupOrder(Integer groupOrderId) {

    }

    @Override
    public void queryOrder(Integer orderId) {

    }

    @Override
    public void consume(Integer consumeId) {

    }

    @Override
    public void refund(int refundId) {

    }

    @Override
    public void update(Integer orderId) {

    }

    @Override
    public void refundGroup(Integer groupRefId) {

    }

    @Override
    public void updateGroup(Integer groupOrderId, String... seqId) {

    }
}
