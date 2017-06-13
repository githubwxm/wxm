package com.all580.voucherplatform.adapter.platform.all580V3;

import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Refund;
import com.all580.voucherplatform.entity.Reverse;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Linv2 on 2017-06-07.
 */
@Service
@Slf4j
public class All580V3AdapterIImpl extends PlatformAdapterService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ConsumeMapper consumeMapper;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private ReverseMapper reverseMapper;
    @Autowired
    private GroupOrderMapper groupOrderMapper;

    @Override
    public Object sendOrder(Integer... orderIds) {
        Map map = new HashMap();
        String orderId = null;
        List<Map> mapData = new ArrayList<>();
        for (Integer id : orderIds) {
            Order order = orderMapper.selectByPrimaryKey(id);
            if (!StringUtils.isEmpty(orderId)) {
                orderId = order.getPlatformOrderId();
            }
            Map mapSub = new HashMap();
            mapSub.put("visitorSeqId", order.getSeqId());
            mapSub.put("imageUrl", order.getImgUrl());
            mapSub.put("voucherNumber", order.getVoucherNumber());
            mapSub.put("ticketId", order.getOrderCode());
            mapSub.put("maProductId", order.getPlatformprod_id());
            mapSub.put("sid", order.getIdNumber());
            mapSub.put("phone", order.getMobile());
            mapData.add(mapSub);
        }
        map.put("data", mapData);
        map.put("orderSn", orderId);
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        map.put("status", 1);
        return null;
    }

    @Override
    public Object consumeTicketRet(Integer consumeId) {
        Consume consume = consumeMapper.selectByPrimaryKey(consumeId);
        Order order = orderMapper.selectByPrimaryKey(consume.getOrder_id());
        Map map = new HashMap();
        map.put("orderSn", order.getPlatformOrderId());
        map.put("visitorSeqId", order.getSeqId());
        map.put("validateSn", consume.getConsumeCode());
        map.put("voucherNumber", order.getVoucherNumber());
        map.put("ticketId", order.getOrderCode());
        map.put("consumeQuantity", consume.getConsumeNumber());
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        return null;
    }

    @Override
    public Object refundOrder(Integer refundId) {
        Refund refund = refundMapper.selectByPrimaryKey(refundId);
        Order order = orderMapper.selectByPrimaryKey(refund.getOrder_id());
        Map map = new HashMap();
        map.put("orderSn", order.getPlatformOrderId());
        map.put("visitorSeqId", order.getSeqId());
        map.put("refId", refund.getPlatformRefId());
        map.put("success", refund.getRefStatus().equals(VoucherConstant.RefundStatus.SUCCESS));
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        return null;
    }

    @Override
    public Object reverse(Integer reverseId) {
        Reverse reverse = reverseMapper.selectByPrimaryKey(reverseId);
        Consume consume = consumeMapper.selectByPrimaryKey(reverse.getConsume_id());
        Order order = orderMapper.selectByPrimaryKey(consume.getOrder_id());
        Map map = new HashMap();
        map.put("orderSn", order.getPlatformOrderId());
        map.put("visitorSeqId", order.getSeqId());
        map.put("validateSn", consume.getConsumeCode());
        map.put("reValidateSn", reverse.getId());
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        return null;
    }

    @Override
    public Object update(Integer orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Map map = new HashMap();
        map.put("orderId", order.getPlatformOrderId());
        map.put("visitorSeqId", order.getSeqId());
        map.put("voucherNumber", order.getVoucherNumber());
        map.put("success", true);
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        return null;
    }

    @Override
    public Object sendGroupOrder(Integer groupOrderId) {
        return null;
    }

    @Override
    public Object refundGroup(Integer groupOrderId) {
        return null;
    }

    @Override
    public Object updateGroup(Integer groupOrderId) {
        return null;
    }

    @Override
    public Object activateGroupOrder(Integer groupOrderId) {
        return null;
    }

    @Override
    public Object reverseGroupOrder(Integer groupOrderId) {
        return null;
    }

}
