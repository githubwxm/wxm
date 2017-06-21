package com.all580.voucherplatform.adapter.supply.ticketV3.processor;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.order.grouporder.ConsumeGroupOrderManager;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-14.
 */
@Service
@Slf4j
public class ActivateGroupOrderProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "activateGroupOrder";
    @Autowired
    private AdapterLoader adapterLoader;

    @Override
    public Object processor(Supply supply, Map map) {
        String voucherId = CommonUtil.objectParseString(map.get("voucherId"));
        String idNumbers = CommonUtil.objectParseString(map.get("idNumbers"));
        Map mapProd = (Map) map.get("product");
        Integer number = CommonUtil.objectParseInteger(mapProd.get("number"));

        ConsumeGroupOrderManager consumeGroupOrderManager = adapterLoader.getBean(ConsumeGroupOrderManager.class);
        consumeGroupOrderManager.setOrder(voucherId);
        try {
            consumeGroupOrderManager.Consume(number, StringUtils.split("idNumbers", ","));
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
