package com.all580.voucherplatform.adapter.supply.ticketV3;

import com.aliyun.mns.model.Message;
import com.all580.order.api.OrderConstant;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.adapter.supply.ticketV3.manager.ConfManager;
import com.all580.voucherplatform.adapter.supply.ticketV3.manager.GroupOrderManager;
import com.all580.voucherplatform.adapter.supply.ticketV3.manager.OrderManager;
import com.all580.voucherplatform.adapter.supply.ticketV3.manager.UpdateGroupManager;
import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Refund;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.OrderLogManager;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.mns.QueuePushManager;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    private OrderMapper orderMapper;
    @Autowired
    private SupplyMapper supplyMapper;
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
    @Autowired
    private QueuePushManager queuePushManager;
    @Autowired
    private ConfManager confManager;
    @Autowired
    private OrderLogManager orderLogManager;



    private void sendMnsMessage(Integer supplyId, String action, Object value) {
        Supply supply = supplyMapper.selectByPrimaryKey(supplyId);
        sendMnsMessage(supply, action, value);
    }

    private void sendMnsMessage(Supply supply, String action, Object value) {
        Map map = new HashMap();
        map.put("action", action);
        map.put("createTime", DateFormatUtils.converToStringTime(new Date()));
        if (value == null) {
            map.put("content", "{}");
        } else {
            map.put("content", JsonUtils.toJson(value));
        }
        Map mapConf = JsonUtils.json2Map(supply.getConf());
        if (mapConf != null) {
            String queueName = CommonUtil.objectParseString(mapConf.get("queueName"));
            String content = JsonUtils.toJson(map);
            if (!StringUtils.isEmpty(queueName)) {
                log.info("发送消息到MNS队列 supplyId={},queueName={},content={}", new Object[]{supply.getId(), queueName, content});
                Message message = queuePushManager.push(queueName, content);
                log.info("发送消息到MNS队列  messageId {} content{}",message.getMessageId(),content);
            }
        }
    }

    @Override
    public Map getConf(Integer supplyId) {
        return confManager.getConfMap(supplyId);
    }

    @Override
    public void queryProd(Integer supplyId) {
        sendMnsMessage(supplyId, "queryProduct", null);
    }

    @Override
    public void sendOrder(Integer... orderId) {
        Map map=null;
        try {
            map = orderManager.getMap(orderId);
            Integer supplyId = CommonUtil.objectParseInteger(map.get("supplyId"));
            sendMnsMessage(supplyId, "saveOrder", map);
            List<Map> list =(List<Map>) map.get("orders");
            log.info(OrderConstant.LogOperateCode.NAME, orderLogManager.orderLog(null,
                    CommonUtil.objectParseString(list.get(0).get("otaOrderId")),
                    0,"saveOrder",
                    OrderConstant.LogOperateCode.SEND_TICKET,
                    CommonUtil.objectParseInteger(list.get(0).get("number")), String.format("发送到凭证:参数:%s", JsonUtils.toJson(map)),
                    CommonUtil.objectParseString(list.get(0).get("voucherId"))));
        } catch (Exception ex) {
            log.error("action  saveOrder map {} exception {}",map,ex.getMessage());
        }

    }

    @Override
    public void sendGroupOrder(Integer groupOrderId) {
        Map map=null;
        try {
            map= groupOrderManager.getMap(groupOrderId);
            Integer supplyId = CommonUtil.objectParseInteger(map.get("supplyId"));
            sendMnsMessage(supplyId, "sendGroupOrder", map);
            log.info(OrderConstant.LogOperateCode.NAME, orderLogManager.orderLog(null,
                    CommonUtil.objectParseString(map.get("otaOrderId")),
                    0,"sendGroupOrder",
                    OrderConstant.LogOperateCode.SEND_TICKET,
                    CommonUtil.objectParseInteger(map.get("number")), String.format("发送到凭证:参数:%s", JsonUtils.toJson(map)),
                    CommonUtil.objectParseString(map.get("voucherId")) ));
        } catch (Exception ex) {
            log.error("action  sendGroupOrder  map {} exception {}",map,ex.getMessage());
        }
    }

    @Override
    public void queryOrder(Integer orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Map map = new HashMap();
        map.put("voucherId", order.getOrderCode());
        sendMnsMessage(order.getSupply_id(), "queryOrder", map);
    }

    @Override
    public void consume(Integer consumeId) {
        Consume consume = consumeMapper.selectByPrimaryKey(consumeId);

        Map map = new HashMap();
        map.put("voucherId", consume.getOrder_code());
        map.put("voucherSeqId", consume.getConsumeCode());
        map.put("consumeTime", DateFormatUtils.converToStringTime(consume.getConsumeTime()));
        map.put("consumeNumber", consume.getConsumeNumber());
        map.put("consumeReason", consume.getAddress());
        sendMnsMessage(consume.getSupply_id(), "verifyOrder", map);
        log.info(OrderConstant.LogOperateCode.NAME, orderLogManager.orderLog(null,
                CommonUtil.objectParseString(map.get("otaOrderId")),
                0,"sendGroupOrder",
                OrderConstant.LogOperateCode.SEND_XIAOMISHU,
                CommonUtil.objectParseInteger(map.get("number")), String.format("发送到凭证:参数:%s", JsonUtils.toJson(map)),
                consume.getOrder_code() ));

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
        map.put("refTime", DateFormatUtils.converToStringTime(refund.getRefTime()));
        map.put("refReason", refund.getRefCause());
        sendMnsMessage(refund.getSupply_id(), action, map);
        log.info(OrderConstant.LogOperateCode.NAME, orderLogManager.orderLog(null,
                orderLogManager.getOrderId(refund.getOrder_code()),
                0,action,
                OrderConstant.LogOperateCode.SEND_XIAOMISHU,
                CommonUtil.objectParseInteger(map.get("refNumber")), String.format("发送到凭证:参数:%s", JsonUtils.toJson(map)),
                refund.getRefundCode() ));

    }

    @Override
    public void update(Integer orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Map map = new HashMap();
        map.put("voucherId", order.getOrderCode());
        map.put("mobile", order.getMobile());
        map.put("idNumber", order.getIdNumber());
        map.put("qrCode", order.getVoucherNumber());
        map.put("validTime", DateFormatUtils.converToStringTime(order.getValidTime()));
        map.put("invalidTime", DateFormatUtils.converToStringTime(order.getInvalidTime()));
        sendMnsMessage(order.getSupply_id(), "updateOrder", map);
    }

    @Override
    public void refundGroup(Integer groupRefId) {
        generalRefund(groupRefId, "cancelGroupOrder");
    }

    @Override
    public void updateGroup(Integer groupOrderId, String... seqId) {
        Map map = updateGroupManager.getMap(groupOrderId, seqId);
        Integer supplyId = CommonUtil.objectParseInteger(map.get("supplyId"));
        sendMnsMessage(supplyId, "updateGroupOrder", map);
        log.info(OrderConstant.LogOperateCode.NAME, orderLogManager.orderLog(null,
                orderLogManager.getOrderId(CommonUtil.objectParseString(map.get("voucherId"))),
                0,"updateGroupOrder",
                OrderConstant.LogOperateCode.SEND_XIAOMISHU,
                CommonUtil.objectParseInteger(map.get("number")), String.format("发送到凭证:参数:%s", JsonUtils.toJson(map)),
               null ));
    }
}
