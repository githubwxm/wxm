package com.all580.voucherplatform.adapter.supply.ticketV3.processor;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.order.RefundResultManager;
import com.all580.voucherplatform.manager.order.grouporder.GroupRefundResultManager;
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

@Service(value = "ticketV3RefundGroupOrderProcessorImpl")
@Slf4j
public class RefundGroupOrderProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "cancelGroupOrderRsp";

    @Autowired
    private AdapterLoader adapterLoader;
    @Override
    public Object processor(Supply supply, Map map) {
        String refId = CommonUtil.objectParseString(map.get("refId"));
        Boolean success = Boolean.valueOf(CommonUtil.objectParseString(map.get("success")));
        GroupRefundResultManager groupRefundResultManager = adapterLoader.getBean(GroupRefundResultManager.class);
        try {
            groupRefundResultManager.setRefund(refId);
            if (success) {
                String ticketRefId = CommonUtil.objectParseString(map.get("ticketRefId"));
                // Integer refNumber = CommonUtil.objectParseInteger(map.get("refNumber"));
                Date procTime = DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("procTime")));
                groupRefundResultManager.submitSuccess(ticketRefId, procTime);
            } else {
                groupRefundResultManager.submitFaild();
            }
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
