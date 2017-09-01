package com.all580.voucherplatform.adapter.platform.all580V3;

import com.all580.voucher.api.service.VoucherCallbackService;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.*;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.JobClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private GroupVisitorMapper groupVisitorMapper;

    @Autowired
    private VoucherCallbackService voucherCallbackService;

    @Autowired
    private JobClient jobClient;
    @Value("${task.tracker}")
    private String taskTracker;

    @Value("${task.maxRetryTimes}")
    private Integer maxRetryTimes;

    private final String MSGID = "70a7bce2-548d-11e7-b114-b2f933d5fe66";

    private void sendMessage(String action, Object value) {
        String content = JsonUtils.toJson(value);
        try {
            voucherCallbackService.process(action, MSGID, content, new Date());
        } catch (Exception ex) {
            log.error("调用小秘书RPC异常:", ex);
            Job job = new Job();
            job.setTaskId("V-PLATFORM-" + UUIDGenerator.getUUID());
            job.setParam("$ACTION$", "ALL580V3_RETRY_ACTION");
            job.setParam("action", action);
            job.setParam("messageId", MSGID);
            job.setParam("content", content);
            job.setTaskTrackerNodeGroup(taskTracker);
            job.setMaxRetryTimes(maxRetryTimes);
            job.setNeedFeedback(false);
            jobClient.submitJob(job);
        }
    }


    @Override
    public Object sendOrder(Integer... orderIds) {
        Map map = new HashMap();
        String orderId = null;
        List<Map> mapList = new ArrayList<>();
        for (Integer id : orderIds) {
            Order order = orderMapper.selectByPrimaryKey(id);
            if (StringUtils.isEmpty(orderId)) {
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
            mapList.add(mapSub);
        }
        map.put("data", mapList);
        map.put("orderSn", orderId);
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        map.put("status", 1);
        sendMessage("SEND", map);
        return null;
    }

    @Override
    public Object consume(Integer consumeId) {
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
        sendMessage("CONSUME", map);
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
        sendMessage("REFUND", map);
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
        sendMessage("RE_CONSUME", map);
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
        sendMessage("", map);
        return null;
    }

    @Override
    public Object sendGroupOrder(Integer groupOrderId) {
        GroupOrder order = groupOrderMapper.selectByPrimaryKey(groupOrderId);
        Map map = new HashMap();
        map.put("orderSn", order.getPlatformOrderId());
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        map.put("status", 1);
        map.put("imageUrl", order.getImgUrl());
        map.put("voucherNumber", order.getVoucherNumber());
        map.put("voucherId", order.getOrderCode());
        map.put("sid", order.getGuideIdNumber());
        map.put("phone", order.getGuideMobile());
        sendMessage("SEND_GROUP", map);
        return null;
    }

    @Override
    public Object refundGroup(Integer groupOrderId) {
        Refund refund = refundMapper.selectByPrimaryKey(groupOrderId);
        GroupOrder order = groupOrderMapper.selectByPrimaryKey(refund.getOrder_id());
        Map map = new HashMap();
        map.put("orderSn", order.getPlatformOrderId());
        map.put("refId", refund.getPlatformRefId());
        map.put("success", refund.getRefStatus().equals(VoucherConstant.RefundStatus.SUCCESS));
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        sendMessage("REFUND_GROUP", map);
        return null;
    }

    @Override
    public Object updateGroup(Integer groupOrderId) {
        GroupOrder order = groupOrderMapper.selectByPrimaryKey(groupOrderId);
        Map map = new HashMap();
        map.put("orderSn", order.getPlatformOrderId());
        map.put("success", true);
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        sendMessage("MODIFY_GROUP", map);
        return null;
    }

    @Override
    public Object activateGroupOrder(Integer groupOrderId) {
        GroupOrder order = groupOrderMapper.selectByPrimaryKey(groupOrderId);
        List<GroupVisitor> visitorList = groupVisitorMapper.selectByActivate(order.getId());
        Map map = new HashMap();
        map.put("orderSn", order.getPlatformOrderId());
        map.put("validateSn", order.getOrderCode());
        StringBuilder builder = new StringBuilder();
        for (GroupVisitor visitor : visitorList) {
            builder.append(visitor.getIdNumber());
            builder.append(",");
        }
        if (!StringUtils.isEmpty(builder.toString())) {
            builder.deleteCharAt(builder.length() - 1);
        }
        map.put("sids", builder.toString());
        map.put("consumeQuantity", order.getActivateNum());
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        sendMessage("CONSUME_GROUP", map);
        return null;
    }

    @Override
    public Object reverseGroupOrder(Integer groupOrderId) {
        GroupOrder order = groupOrderMapper.selectByPrimaryKey(groupOrderId);
        Map map = new HashMap();
        map.put("orderSn", order.getPlatformOrderId());
        map.put("validateSn", order.getOrderCode());
        map.put("reValidataSn", order.getOrderCode());
        map.put("quantity", order.getActivateNum());
        map.put("procTime", DateFormatUtils.converToStringTime(new Date()));
        sendMessage("RE_CONSUME_GROUP", map);
        return null;
    }

}
