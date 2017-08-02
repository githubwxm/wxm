package com.all580.voucherplatform.adapter.supply.pos.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.dao.ConsumeMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.RefundMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.entity.*;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-21.
 */
@Service("pos.QueryDetailProcessorImpl")
public class QueryDetailProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "detail";
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SupplyProductMapper supplyProductMapper;

    @Autowired
    private ConsumeMapper consumeMapper;
    @Autowired
    private RefundMapper refundMapper;

    @Override
    public Object processor(Supply supply,
                            Map map) {
        DeviceGroup deviceGroup = (DeviceGroup) map.get("device");
        String voucherId = CommonUtil.emptyStringParseNull(map.get("voucherId"));
        return getResult(voucherId);
    }

    private Map getResult(String orderCode) {

        Map map = new HashMap();
        Order order = orderMapper.selectByOrderCode(orderCode);
        SupplyProduct supplyProduct = supplyProductMapper.selectByPrimaryKey(order.getSupplyProdId());
        map.put("voucherId", order.getOrderCode());
        map.put("otaOrderId", order.getPlatformOrderId());
        map.put("customName", order.getCustomName());
        map.put("voucherNumber", order.getVoucherNumber());
        map.put("mobile", order.getMobile());
        map.put("idNumber", order.getIdNumber());
        map.put("productName", supplyProduct.getName());
        map.put("number", order.getNumber());
        map.put("consumeNumber", order.getConsume());
        map.put("refundNumber", order.getRefund());
        map.put("consumeType", order.getConsumeType());
        map.put("validTime", DateFormatUtils.converToStringTime(order.getValidTime()));
        map.put("invalidTime", DateFormatUtils.converToStringTime(order.getInvalidTime()));
        map.put("validWeek", order.getValidWeek());
        map.put("invalidDate", order.getInvalidDate());
        map.put("createTime", DateFormatUtils.converToStringTime(order.getCreateTime()));
        map.put("consumes", consumes(order));
        map.put("refunds", refund(order));
        return map;
    }

    private List<Map<String, Object>> consumes(Order order) {


        List<Consume> list = consumeMapper.selectConsumeByOrder(order.getId(), order.getOrderCode());
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>(list.size());
        for (Consume consumeItem : list) {
            Map<String, Object> consumeMap = new HashMap<String, Object>();
            consumeMap.put("consumeId", consumeItem.getConsumeCode());
            consumeMap.put("consumeTime", DateFormatUtils.converToStringTime(consumeItem.getConsumeTime()));
            consumeMap.put("number", consumeItem.getConsumeNumber());
            consumeMap.put("beforeNumber", consumeItem.getPrevNumber());
            consumeMap.put("afterNumber", consumeItem.getAfterNumber());
            consumeMap.put("deviceId", consumeItem.getDeviceId());
            resultList.add(consumeMap);
        }
        return resultList;
    }

    private List<Map> refund(Order order) {
        List<Refund> list = refundMapper.selectRefundByOrder(order.getId(), order.getOrderCode());
        List<Map> resultList = new ArrayList<>(list.size());
        for (Refund refundItem : list) {
            Map<String, Object> refoundMap = new HashMap<String, Object>();
            refoundMap.put("refundId", refundItem.getRefundCode());
            refoundMap.put("number", refundItem.getRefNumber());
            refoundMap.put("refundDate", DateFormatUtils.converToStringTime(refundItem.getRefTime()));
            refoundMap.put("refundCause", refundItem.getRefCause());
            refoundMap.put("status", refundItem.getRefStatus());
            resultList.add(refoundMap);
        }
        return resultList;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
