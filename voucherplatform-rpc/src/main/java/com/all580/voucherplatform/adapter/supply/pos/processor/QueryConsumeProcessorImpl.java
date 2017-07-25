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
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-21.
 */

@Service("pos.QueryConsumeProcessorImpl")
public class QueryConsumeProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "consumelist";

    @Autowired
    private PosMapper posMapper;

    @Override
    public Object processor(Supply supply,
                            Map map) {
        Device device = (Device) map.get("device");
        Date requestTime = DateFormatUtils.converToDateTime(CommonUtil.emptyStringParseNull(map.get("requestTime")));
        String voucher = CommonUtil.emptyStringParseNull(map.get("voucher"));
        Date startTime = DateFormatUtils.converToDateTime(CommonUtil.emptyStringParseNull(map.get("startTime")));
        Date endTime = DateFormatUtils.converToDateTime(CommonUtil.emptyStringParseNull(map.get("endTime")));
        Integer pageIndex = CommonUtil.objectParseInteger(map.get("pageIndex"), 0);
        Integer pageSize = CommonUtil.objectParseInteger(map.get("pageSize"), 10);

        String voucherNumber = null;
        String mobile = null;
        String idNumber = null;
        if (voucher.length() == 11) {
            mobile = voucher;
        } else if (voucher.length() == 15 || voucher.length() == 18) {
            idNumber = voucher;
        } else {
            voucherNumber = voucher;
        }
        List<Map> list = posMapper.selectOrderConsumeList(voucherNumber, mobile, idNumber, device.getCode(),
                startTime, endTime,
                (pageIndex - 1) * pageSize, pageSize);
        return list;
    }


    @Override
    public String getAction() {
        return ACTION;
    }
}
