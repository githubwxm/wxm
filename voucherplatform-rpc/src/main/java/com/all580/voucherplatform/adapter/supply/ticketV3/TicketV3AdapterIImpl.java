package com.all580.voucherplatform.adapter.supply.ticketV3;

import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.adapter.supply.ticketV3.manager.GroupOrderManager;
import com.all580.voucherplatform.adapter.supply.ticketV3.manager.OrderManager;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.SupplyProduct;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.sun.tools.corba.se.idl.constExpr.Or;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-06.
 */
@Service
@Slf4j
public class TicketV3AdapterIImpl extends SupplyAdapterService {

    @Autowired
    public OrderMapper orderMapper;
    @Autowired
    public SupplyProductMapper supplyProductMapper;
    @Autowired
    private GroupOrderManager groupOrderManager;
    @Autowired
    private OrderManager orderManager;

    protected String generateMnsFormat(String action, Object value) {
        Map map = new HashMap();
        map.put("action", action);
        if (value == null) {
            map.put("content", "{}");
        } else {
            map.put("createTime", JsonUtils.toJson(value));
        }
        return JsonUtils.toJson(map);
    }

    @Override
    public void queryProd(Integer supplyId) {
        String content = generateMnsFormat("queryProduct", null);
    }

    @Override
    public void sendOrder(Integer... orderId) {
        try {
            Map map = orderManager.getMap(orderId);
            String content = generateMnsFormat("saveOrder", map);
        } catch (Exception ex) {

        }

    }

    @Override
    public void sendGroupOrder(Integer groupOrderId) {
        try {
            Map map = groupOrderManager.getMap(groupOrderId);
            String content = generateMnsFormat("sendGroupOrder", map);
        } catch (Exception ex) {

        }
    }

    @Override
    public void queryOrder(Integer orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Map map = new HashMap();
        map.put("voucherId", order.getOrderCode());
        String content = generateMnsFormat("queryOrder", map);
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
    public void updateGroup(Integer groupOrderId) {

    }
}
