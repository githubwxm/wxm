package com.all580.voucherplatform.adapter.supply.ticketV3.processor;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.order.ConsumeOrderManager;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-14.
 */
@Service
@Slf4j
public class ConsumeProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "consumeOrder";
    @Autowired
    AdapterLoadder adapterLoadder;

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
        ConsumeOrderManager consumeOrderManager = adapterLoadder.getBean(ConsumeOrderManager.class);
        consumeOrderManager.setOrder(voucherId);
        try {
            consumeOrderManager.Comsume(consumeSeqId, consumeNumber, consumeAddress, consumeTime, deviceId);
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
