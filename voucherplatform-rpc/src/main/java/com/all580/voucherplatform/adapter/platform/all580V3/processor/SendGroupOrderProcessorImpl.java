package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.manager.order.grouporder.CreateGroupOrderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-13.
 */

@Service
@Slf4j
public class SendGroupOrderProcessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "sendGroupTicket";
    @Autowired
    private AdapterLoader adapterLoader;

    @Override
    public Object processor(Platform platform, Map map) {
        CreateGroupOrderManager createOrderManager = adapterLoader.getBean(CreateGroupOrderManager.class);
        try {
            createOrderManager.setProd(platform.getId(), map);
            createOrderManager.saveOrder();
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
