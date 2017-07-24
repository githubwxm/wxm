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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-21.
 */

@Service("pos.ReportProcessorImpl")
public class ReportProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "report";

    @Autowired
    private PosMapper posMapper;

    @Override
    public Object processor(Supply supply,
                            Map map) {

        Device device = (Device) map.get("device");
        Date startTime = DateFormatUtils.converToDateTime(CommonUtil.emptyStringParseNull(map.get("startTime")));
        Date endTime = DateFormatUtils.converToDateTime(CommonUtil.emptyStringParseNull(map.get("endTime")));
        List<Map> list = posMapper.selectDeviceReportByDate(device.getCode(), startTime, endTime);
        return getResult(list);
    }

    private Map<String, Map<String, Object>> getResult(List<Map> list) {

        Map<String, Map<String, Object>> retMap = new HashMap<String, Map<String, Object>>();
        for (Map map : list) {
            String key = CommonUtil.emptyStringParseNull(map.get("date"));
            Map<String, Object> rowMap = null;
            if (retMap.containsKey(key)) {
                rowMap = retMap.get(key);
            } else {
                rowMap = new HashMap();
            }
            rowMap.put("total", CommonUtil.objectParseInteger(map.get("number")));
            rowMap.put("productName", CommonUtil.emptyStringParseNull(map.get("name")));
            retMap.put(key, rowMap);
        }
        return retMap;
    }

    @Override public String getAction() {
        return ACTION;
    }
}
