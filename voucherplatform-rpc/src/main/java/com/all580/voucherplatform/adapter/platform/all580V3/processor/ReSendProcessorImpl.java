package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.manager.order.ResendManager;
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
public class ReSendProcessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "resendTicket";

    @Autowired
    private AdapterLoadder adapterLoadder;

    @Override
    public Object processor(Platform platform, Map map) {
        String orderId = CommonUtil.objectParseString(map.get("orderId"));
        String visitorSeqId = CommonUtil.objectParseString(map.get("visitorSeqId"));
        String mobile = CommonUtil.objectParseString(map.get("mobile"));
        ResendManager resendManager = adapterLoadder.getBean(ResendManager.class);
        resendManager.setOrder(platform.getId(), orderId, visitorSeqId);
        try {
            resendManager.send(mobile);
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
