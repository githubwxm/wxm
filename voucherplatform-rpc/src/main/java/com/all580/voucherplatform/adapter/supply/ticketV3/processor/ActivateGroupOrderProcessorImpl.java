package com.all580.voucherplatform.adapter.supply.ticketV3.processor;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.manager.order.grouporder.ConsumeGroupOrderManager;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-14.
 */
@Service
@Slf4j
public class ActivateGroupOrderProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "activateGroupOrderRsp";
    @Autowired
    private AdapterLoader adapterLoader;

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    public Object processor(Supply supply, Map map) {
        String voucherId = CommonUtil.objectParseString(map.get("voucherId"));
        String idNumbers = CommonUtil.objectParseString(map.get("idNumbers"));
        Map mapProd = (Map) map.get("product");
        Integer number = CommonUtil.objectParseInteger(mapProd.get("number"));
        DistributedReentrantLock lock = distributedLockTemplate.execute(voucherId, lockTimeOut);
        ConsumeGroupOrderManager consumeGroupOrderManager = adapterLoader.getBean(ConsumeGroupOrderManager.class);
        consumeGroupOrderManager.setOrder(voucherId);
        try {
            if(idNumbers ==null  || idNumbers.equals("[]")){
                consumeGroupOrderManager.submiConsume(number, null);
            }else{
                consumeGroupOrderManager.submiConsume(number, StringUtils.split(idNumbers, ","));
            }
        } catch (Exception ex) {
            throw new ApiException(ex);
        }finally {
            lock.unlock();
        }

        return null;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
