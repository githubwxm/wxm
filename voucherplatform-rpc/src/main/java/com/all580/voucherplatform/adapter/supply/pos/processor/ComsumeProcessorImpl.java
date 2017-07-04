package com.all580.voucherplatform.adapter.supply.pos.processor;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.order.ConsumeOrderManager;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-04.
 */
@Service("pos.ComsumeProcessorImpl")
public class ComsumeProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "consume";
    @Autowired
    AdapterLoader adapterLoader;
    @Override
    public Object processor(Supply supply, Map map) {
        String voucherId = CommonUtil.objectParseString(map.get("voucherId"));
        String orderId = CommonUtil.objectParseString(map.get("orderId"));
        String consumeSeqId = CommonUtil.objectParseString(map.get("consumeSeqId"));
        Date consumeTime = DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("consumeTime")));
        Integer consumeNumber = CommonUtil.objectParseInteger(map.get("consumeNumber"));
        String consumeAddress = CommonUtil.objectParseString(map.get("consumeAddress"));
        String deviceId = CommonUtil.objectParseString(map.get("deviceId"));
        String voucherSeqId = CommonUtil.objectParseString(map.get("voucherSeqId"));
        ConsumeOrderManager consumeOrderManager = adapterLoader.getBean(ConsumeOrderManager.class);
        try {
            consumeOrderManager.setOrder(voucherId);
            consumeOrderManager.submiConsume(consumeSeqId, consumeNumber, consumeAddress, consumeTime, deviceId);
        } catch (Exception ex) {
            throw new ApiException(ex);
        }
        return null;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}