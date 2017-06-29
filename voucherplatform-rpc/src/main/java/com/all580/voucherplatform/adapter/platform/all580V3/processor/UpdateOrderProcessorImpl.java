package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.manager.order.UpdateOrderManager;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-13.
 */

@Service
@Slf4j
public class UpdateOrderProcessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "updateTicket";
    @Autowired
    private AdapterLoadder adapterLoadder;

    @Override
    public Object processor(Platform platform, Map map) {
        String orderId = CommonUtil.objectParseString(map.get("orderId"));
        String visitorSeqId = CommonUtil.objectParseString(map.get("visitorSeqId"));
        String mobile = CommonUtil.objectParseString(map.get("mobile"));
        String idNumber = CommonUtil.objectParseString(map.get("idNumber"));
        String validTime = CommonUtil.objectParseString(map.get("validTime"));
        String invalidTime = CommonUtil.objectParseString(map.get("invalidTime"));
        UpdateOrderManager updateOrderManager = adapterLoadder.getBean(UpdateOrderManager.class);
        try {
            updateOrderManager.setOrder(platform.getId(), orderId, visitorSeqId);
            updateOrderManager.setMobile(mobile);
            updateOrderManager.setIdNumber(idNumber);
            if (!StringUtils.isEmpty(validTime)) {
                updateOrderManager.setValidTime(DateFormatUtils.converToDateTime(invalidTime));
            } if (!StringUtils.isEmpty(validTime)) {
                updateOrderManager.setInvalidTime(DateFormatUtils.converToDateTime(invalidTime));
                updateOrderManager.saveOrder();
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
