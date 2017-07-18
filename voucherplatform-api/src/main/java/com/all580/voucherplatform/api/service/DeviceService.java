package com.all580.voucherplatform.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-17.
 */
public interface DeviceService {
    Result addGroup(Map map);

    Result<PageRecord<Map>> selectGroupList(Integer supplyId, String code, String name, Integer recordStart, Integer recordCount);

    Result addDevice(Map map);

    Result delDevice(String code);

    Result<PageRecord<Map>> selectDeviceList(Integer groupId, Integer supplyId, String code, String name, Integer recordStart, Integer recordCount);

    Result setProd(Integer groupId, List<Map> list);

    Result getProd(Integer groupId);

    Result apply(Map map);
}
