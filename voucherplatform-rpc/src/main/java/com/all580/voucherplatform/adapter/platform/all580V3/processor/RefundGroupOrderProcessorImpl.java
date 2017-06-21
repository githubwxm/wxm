package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.manager.order.RefundApplyManager;
import com.all580.voucherplatform.manager.order.grouporder.GroupRefundApplyManager;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-13.
 */

@Service
@Slf4j
public class RefundGroupOrderProcessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "cancelGroupTicket";
    @Autowired
    private AdapterLoader adapterLoader;

    @Override
    public Object processor(Platform platform, Map map) {
        String orderId = CommonUtil.objectParseString(map.get("orderId"));
        String refId = CommonUtil.objectParseString(map.get("refId"));
        Integer refNumber = CommonUtil.objectParseInteger(map.get("refNumber"));
        Date refTime = DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("refTime")));
        String refReason = CommonUtil.objectParseString(map.get("refReason"));

        GroupRefundApplyManager refundApplyManager = adapterLoader.getBean(GroupRefundApplyManager.class);
        try {
            refundApplyManager.setGroupOrder(platform.getId(), orderId);
            refundApplyManager.apply(refId, refNumber, refTime, refReason);
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
