package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.manager.order.grouporder.UpdateGroupOrderManager;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Linv2 on 2017-06-13.
 */

@Service
@Slf4j
public class UpdateGroupOrderProcessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "updateGroupTicket";
    @Autowired
    private AdapterLoadder adapterLoadder;

    @Override
    public Object processor(Platform platform, Map map) {
        UpdateGroupOrderManager updateGroupOrderManager = adapterLoadder.getBean(UpdateGroupOrderManager.class);
        String orderId = CommonUtil.objectParseString(map.get("orderId"));
        updateGroupOrderManager.setOrder(platform.getId(), orderId);
        updateGroupOrderManager.setData(map);
        updateGroupOrderManager.saveOrder();
        return null;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
