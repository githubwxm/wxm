package com.all580.voucherplatform.adapter.supply.ticketV3.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.api.service.SupplyService;
import com.all580.voucherplatform.entity.Supply;
import com.framework.common.lang.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-14.
 */
@Service
@Slf4j
public class QueryProdProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "queryProductRsp";

    @Autowired
    private SupplyService supplyService;

    @Override
    public Object processor(Supply supply, Map map) {
        List list = (List) map.get("data");
        List<Map> mapList = new ArrayList<>();
        for (Object value : list) {
            Map mapProd = (Map) value;
            mapProd.put("code", mapProd.get("productId"));
            mapProd.put("name", mapProd.get("productName"));
            mapProd.put("data", JsonUtils.toJson(mapProd.get("attachs")));
            mapList.add(mapProd);
        }
        supplyService.setProd(supply.getId(), mapList);
        return null;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
