package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.manager.order.RefundApplyManager;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-09.
 */

@Service
@Slf4j
public class RefundProcessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "cancelTicket";

    @Autowired
    private AdapterLoadder adapterLoadder;

    @Override
    public Object processor(Platform platform, Map map) {
        String orderId = CommonUtil.objectParseString(map.get("orderId"));
        String visitorSeqId = CommonUtil.objectParseString(map.get("visitorSeqId"));
        String refId = CommonUtil.objectParseString(map.get("refId"));
        Integer refNumber = CommonUtil.objectParseInteger(map.get("refNumber"));
        String refTime = CommonUtil.objectParseString(map.get("refTime"));
        String refReason = CommonUtil.objectParseString(map.get("refReason"));
        RefundApplyManager refundApplyManager = adapterLoadder.getBean(RefundApplyManager.class);
        try {
            refundApplyManager.setOrder(platform.getId(), orderId, visitorSeqId);
            refundApplyManager.apply(refId, refNumber, DateFormatUtils.converToDateTime(refTime), refReason);
        } catch (Exception ex) {
            throw new ApiException(ex);
        }

        return null;
    }

    @Override
    public String getAction() {
        return null;
    }
}
