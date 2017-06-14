package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.manager.order.CreateOrderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */
@Service
@Slf4j
public class SendOrderProcessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "sendTicket";
    @Autowired
    private AdapterLoadder adapterLoadder;

    @Override
    public Object processor(Platform platform, Map map) {
        CreateOrderManager createOrderManager = adapterLoadder.getBean(CreateOrderManager.class);
        try {
            map.put("prodId", map.get("productId"));
            createOrderManager.setProd(platform.getId(), map);
            List<Map> visitorList = (List<Map>) map.get("visitors");
            createOrderManager.setVisitor(visitorList);
            createOrderManager.saveOrder();
            createOrderManager.notitySupply();
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
