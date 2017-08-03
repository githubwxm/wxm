package com.all580.voucherplatform.adapter.supply.pos.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.dao.DeviceMapper;
import com.all580.voucherplatform.dao.PosMapper;
import com.all580.voucherplatform.entity.Device;
import com.all580.voucherplatform.entity.DeviceGroup;
import com.all580.voucherplatform.entity.Supply;
import com.framework.common.util.CommonUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-07.
 */
@Service("pos.QueryOrderProcessorImpl")
public class QueryOrderProcessorImpl implements ProcessorService<Supply> {

    private static final String ACTION = "list";
    @Autowired
    private PosMapper posMapper;

    @Override
    public Object processor(Supply supply,
                            Map map) {
        DeviceGroup deviceGroup = (DeviceGroup) map.get("deviceGroup");
        String voucher = CommonUtil.emptyStringParseNull(map.get("voucher"));
        Integer pageIndex = CommonUtil.objectParseInteger(map.get("pageIndex"), 1);
        Integer pageSize = CommonUtil.objectParseInteger(map.get("pageSize"), 10);
        pageIndex = (--pageIndex < 0) ? 0 : pageIndex;

        return getResult(voucher, deviceGroup.getId(), pageIndex, pageSize);
    }

    private List<Map> getResult(
            String voucher,
            Integer deviceGroupId,
            Integer pageIndex,
            Integer pageSize) {
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

        //int count = posMapper.selectOrderCount(voucherNumber, mobile, idNumber, deviceGroupId);
        List<Map> list = posMapper.selectOrderList(voucherNumber, mobile, idNumber, deviceGroupId,
                pageIndex * pageSize, pageSize);
        return list;
    }

    @Override
    public String getAction() {
        return ACTION;
    }

}
