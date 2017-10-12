package com.all580.voucherplatform.adapter.supply.pos.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.dao.ConsumeMapper;
import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Device;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.order.OrderManager;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-04.
 */
@Service("pos.ConsumeProcessorImpl")
public class ConsumeProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "consume";

    @Autowired
    private ConsumeMapper consumeMapper;
    @Autowired
    private OrderManager orderManager;

    @Override
    public Object processor(Supply supply,
                            Map map) {
        Device device = (Device) map.get("device");
        String orderCode = CommonUtil.objectParseString(map.get("voucherId"));
        String seqId = CommonUtil.objectParseString(map.get("seqId"));
        Integer consumeNumber = CommonUtil.objectParseInteger(map.get("consumeNumber"));
        String consumeAddress = CommonUtil.objectParseString(map.get("deviceId"));

        Order order = orderManager.getOrder(orderCode);
        Assert.notNull(order, "订单不存在");
        Integer consumeId = orderManager.submitConsume(seqId, consumeNumber, consumeAddress, new Date(), device.getCode(), order);
        return getResult(order, consumeId);

    }

    private Map getResult(Order order,
                          Integer consumeId) {
        Consume consume = consumeMapper.selectByPrimaryKey(consumeId);
        if (consume == null) {
            throw new ApiException("消费失败，没用找到消费数据");
        }
        Map map = new HashMap();
        map.put("consumeId", consume.getConsumeCode());
        map.put("clientSeqId", consume.getSupplyConsumeSeqId());
        map.put("consumeNumber", consume.getConsumeCode());
        map.put("consumeTime", DateFormatUtils.converToStringTime(consume.getConsumeTime()));
        map.put("beforeNumber", consume.getPrevNumber());
        map.put("afterNumber", consume.getAfterNumber());
        map.put("deviceId", consume.getDeviceId());
        map.put("printText", order.getPrintText());
        return map;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
