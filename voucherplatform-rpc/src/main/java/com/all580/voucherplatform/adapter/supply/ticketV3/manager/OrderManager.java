package com.all580.voucherplatform.adapter.supply.ticketV3.manager;

import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.SupplyProduct;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-13.
 */

@Service
@Slf4j
public class OrderManager {
    @Autowired
    public OrderMapper orderMapper;
    @Autowired
    public SupplyProductMapper supplyProductMapper;

    public Map getMap(Integer... orderId) throws Exception {
        List<Order> orderList = orderMapper.selectOrderListByPrimaryKey(orderId);
        Map map = new HashMap();
        String batchNo = orderList.get(0).getOrderCode();
        if (orderList.get(0).getSupplyProdId() == null) {
            log.debug("订单未绑定票务产品");
            throw new Exception("订单未绑定票务产品");
        }
        SupplyProduct supplyProduct = supplyProductMapper.selectByPrimaryKey(orderList.get(0).getSupplyProdId());

        if (supplyProduct == null) {
            log.debug("订单未绑定票务产品");
            throw new Exception("订单未绑定票务产品");
        }
        map.put("batch", batchNo);
        map.put("orders", getVisitorMap(orderList, supplyProduct.getCode()));
        return map;
    }

    private List<Map> getVisitorMap(List<Order> orderList, String prodId) {
        List<Map> mapList = new ArrayList<>();
        for (Order order : orderList) {
            Map mapOrder = new HashMap();
            mapOrder.put("voucherId", order.getOrderCode());
            mapOrder.put("otaOrderId", order.getPlatformOrderId());
            mapOrder.put("productId", prodId);
            mapOrder.put("payment", 1);
            mapOrder.put("consumeType", order.getConsumeType());
            mapOrder.put("validTime", DateFormatUtils.converToStringDate(order.getValidTime()));
            mapOrder.put("invalidTime", DateFormatUtils.converToStringDate(order.getInvalidTime()));
            mapOrder.put("validWeek", order.getValidWeek());
            mapOrder.put("invalidDate", order.getInvalidDate());
            mapOrder.put("qrCode", order.getVoucherNumber());
            mapOrder.put("customName", order.getCustomName());
            mapOrder.put("mobile", order.getMobile());
            mapOrder.put("idNumber", order.getIdNumber());
            mapOrder.put("number", order.getNumber());
            mapList.add(mapOrder);
        }
        return mapList;
    }
}
