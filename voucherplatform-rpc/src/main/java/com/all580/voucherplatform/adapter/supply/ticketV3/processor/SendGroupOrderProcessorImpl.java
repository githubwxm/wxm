package com.all580.voucherplatform.adapter.supply.ticketV3.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.order.grouporder.GroupOrderSupplyReceiveManager;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Linv2 on 2017-06-14.
 */

@Service(value = "ticketV3SendGroupOrderProcessorImpl")
@Slf4j
public class SendGroupOrderProcessorImpl implements ProcessorService<Supply> {
    private static final String ACTION = "sendGroupOrderRsp";
    @Autowired
    private GroupOrderSupplyReceiveManager orderSupplyReceiveManager;

    @Override
    public Object processor(Supply supply, Map map) {
        String voucherId = CommonUtil.objectParseString(map.get("voucherId"));
        String ticketOrderId = CommonUtil.objectParseString(map.get("ticketOrderId"));
        orderSupplyReceiveManager.Receive(voucherId, ticketOrderId);

        return null;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
