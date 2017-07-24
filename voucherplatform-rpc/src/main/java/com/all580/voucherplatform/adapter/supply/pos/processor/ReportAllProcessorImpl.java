package com.all580.voucherplatform.adapter.supply.pos.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.dao.PosMapper;
import com.all580.voucherplatform.entity.Device;
import com.all580.voucherplatform.entity.Supply;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-21.
 */

@Service("pos.ReportAllProcessorImpl")
public class ReportAllProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "reportall";

    @Autowired
    private PosMapper posMapper;

    @Override public Object processor(Supply supply,
                                      Map map) {
        Device device = (Device) map.get("device");
        Date startTime = DateFormatUtils.converToDateTime(CommonUtil.emptyStringParseNull(map.get("startTime")));
        Date endTime = DateFormatUtils.converToDateTime(CommonUtil.emptyStringParseNull(map.get("endTime")));
        return posMapper.selectDeviceReportByProduct(device.getCode(), startTime, endTime);
    }

    @Override public String getAction() {
        return ACTION;
    }
}
