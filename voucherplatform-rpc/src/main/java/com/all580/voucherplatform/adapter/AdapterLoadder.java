package com.all580.voucherplatform.adapter;

import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.dao.TicketSysMapper;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.entity.TicketSys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */
@Service
public class AdapterLoadder {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TicketSysMapper ticketSysMapper;
    private Map<Integer, SupplyAdapterService> serviceMap = new HashMap<>();

    private synchronized SupplyAdapterService loadTicketSystem(Integer ticketSysId) {
        if (!serviceMap.containsKey(ticketSysId)) {
            TicketSys ticketSys = ticketSysMapper.selectByPrimaryKey(ticketSysId);
            if (ticketSys != null) {
                SupplyAdapterService supplyAdapterService = applicationContext.getBean(ticketSys.getImplPacket(), SupplyAdapterService.class);
                if (supplyAdapterService != null) {
                    serviceMap.put(ticketSysId, supplyAdapterService);
                }
                return supplyAdapterService;
            }
            return null;
        } else {
            return serviceMap.get(ticketSysId);
        }
    }


    public PlatformAdapterService getPlatformAdapterService(Platform platform) {
        try {
            Class cls = Class.forName(platform.getImplPackage());
            return (PlatformAdapterService) applicationContext.getBean(cls);
        } catch (Exception ex) {
            return null;
        }
//        return applicationContext.getBean(platform.getImplPackage(), PlatformAdapterService.class);
    }

    public SupplyAdapterService getSupplyAdapterService(Supply supply) {
        if (!serviceMap.containsKey(supply.getTicketsys_id())) {
            return loadTicketSystem(supply.getTicketsys_id());
        } else {
            return serviceMap.get(supply.getTicketsys_id());
        }
    }

    public <T> T getBean(Class<T> cls) {
        return applicationContext.getBean(cls);
    }
}
