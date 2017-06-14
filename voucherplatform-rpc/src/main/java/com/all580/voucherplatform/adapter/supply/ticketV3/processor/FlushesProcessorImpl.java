package com.all580.voucherplatform.adapter.supply.ticketV3.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.entity.Supply;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-14.
 */

@Service
@Slf4j
public class FlushesProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "flushesOrder";

    @Override
    public Object processor(Supply supply, Map map) {
        String voucherId = CommonUtil.objectParseString(map.get("voucherId"));
        String consumeSeqId = CommonUtil.objectParseString(map.get("consumeSeqId"));
        String flushesSeqId = CommonUtil.objectParseString(map.get("flushesSeqId"));
        String flushesTime = CommonUtil.objectParseString(map.get("flushesTime"));
        Date procTime = DateFormatUtils.converToDateTime(CommonUtil.objectParseString(map.get("procTime")));
        return null;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
