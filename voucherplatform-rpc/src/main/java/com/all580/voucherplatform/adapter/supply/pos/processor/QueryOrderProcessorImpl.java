package com.all580.voucherplatform.adapter.supply.pos.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-07.
 */
@Service
public class QueryOrderProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "query";

    @Override
    public Object processor(Supply supply, Map map) {

        return null;
    }

    @Override
    public String getAction() {
        return ACTION;
    }

}
