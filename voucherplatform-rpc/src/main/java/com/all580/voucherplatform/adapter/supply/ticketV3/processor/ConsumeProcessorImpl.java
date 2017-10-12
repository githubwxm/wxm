package com.all580.voucherplatform.adapter.supply.ticketV3.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.order.OrderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-14.
 */
@Service
@Slf4j
public class ConsumeProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "consumeOrderRsp";
    @Autowired
    private OrderManager orderManager;

    @Override
    public Object processor(Supply supply,
                            Map map) {
        try {
            orderManager.submitConsume(map);
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
