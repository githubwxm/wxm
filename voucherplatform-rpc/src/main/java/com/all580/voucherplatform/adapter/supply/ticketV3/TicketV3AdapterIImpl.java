package com.all580.voucherplatform.adapter.supply.ticketV3;

import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.adapter.supply.ticketV3.manager.GroupOrderManager;
import com.all580.voucherplatform.adapter.supply.ticketV3.manager.OrderManager;
import com.all580.voucherplatform.adapter.supply.ticketV3.manager.UpdateGroupManager;
import com.all580.voucherplatform.dao.ConsumeMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.RefundMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Refund;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
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
    private RefundMapper refundMapper;
    @Autowired
    private ConsumeMapper consumeMapper;


    @Autowired
    private GroupOrderManager groupOrderManager;
    @Autowired
    private OrderManager orderManager;
    @Autowired
    private UpdateGroupManager updateGroupManager;

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
        Consume consume = consumeMapper.selectByPrimaryKey(consumeId);

        Map map = new HashMap();
        map.put("voucherId", consume.getOrder_code());
        map.put("voucherSeqId", consume.getConsumeCode());
        map.put("consumeTime", DateFormatUtils.converToStringDate(consume.getConsumeTime()));
        map.put("consumeNumber", consume.getConsumeNumber());
        map.put("consumeReason", consume.getAddress());
        String content = generateMnsFormat("verifyOrder", map);

    }

    @Override
    public void refund(int refundId) {
        generalRefund(refundId, "cancelOrder");
    }

    private void generalRefund(int refundId, String action) {

        Refund refund = refundMapper.selectByPrimaryKey(refundId);
        Map map = new HashMap();
        map.put("voucherId", refund.getOrder_code());
        map.put("refId", refund.getRefundCode());
        map.put("refNumber", refund.getRefNumber());
        map.put("refTime", DateFormatUtils.converToStringDate(refund.getRefTime()));
        map.put("refReason", refund.getRefCause());
        String content = generateMnsFormat(action, map);
    }

    @Override
    public void update(Integer orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Map map = new HashMap();
        map.put("voucherId", order.getOrderCode());
        map.put("mobile", order.getMobile());
        map.put("idNumber", order.getIdNumber());
        map.put("qrCode", order.getVoucherNumber());
        map.put("validTime", DateFormatUtils.converToStringDate(order.getValidTime()));
        map.put("invalidTime", DateFormatUtils.converToStringDate(order.getInvalidTime()));
        String content = generateMnsFormat("updateOrder", map);
    }

    @Override
    public void refundGroup(Integer groupRefId) {
        generalRefund(groupRefId, "cancelGroupOrder");
    }

    @Override
    public void updateGroup(Integer groupOrderId, Integer... seqId) {
        Map map = updateGroupManager.getMap(groupOrderId, seqId);
        String content = generateMnsFormat("updateGroupOrder", map);
    }
}
