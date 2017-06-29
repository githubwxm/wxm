package com.all580.voucherplatform.adapter.supply.ticketV3.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.order.grouporder.UpdateResultGroupOrderManager;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-15.
 */

@Service(value = "ticketV3UpdateGroupOrderProcessorImpl")
@Slf4j
public class UpdateGroupOrderProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "updateGroupOrderRsp";

    @Autowired
    private UpdateResultGroupOrderManager updateResultGroupOrderManager;

    @Override
    public Object processor(Supply supply, Map map) {
        String voucherId = CommonUtil.objectParseString(map.get("voucherId"));
        String ticketOrderId = CommonUtil.objectParseString(map.get("ticketOrderId"));
        Date procTime = DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("procTime")));
        updateResultGroupOrderManager.success(voucherId);
        return null;
    }

    @Override
    public String getAction() {
        return null;
    }
}
