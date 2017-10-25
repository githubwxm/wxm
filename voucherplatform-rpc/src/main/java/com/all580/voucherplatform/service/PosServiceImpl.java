package com.all580.voucherplatform.service;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.api.service.PosService;
import com.all580.voucherplatform.dao.DeviceApplyMapper;
import com.all580.voucherplatform.dao.DeviceGroupMapper;
import com.all580.voucherplatform.dao.DeviceMapper;
import com.all580.voucherplatform.dao.SupplyMapper;
import com.all580.voucherplatform.entity.Device;
import com.all580.voucherplatform.entity.DeviceApply;
import com.all580.voucherplatform.entity.DeviceGroup;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.utils.sign.SignInstance;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-20.
 */
@Service
@Slf4j
public class PosServiceImpl implements PosService {

    @Autowired
    private DeviceApplyMapper deviceApplyMapper;
    @Autowired
    private DeviceGroupMapper deviceGroupMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private SupplyMapper supplyMapper;
    @Autowired
    private AdapterLoader adapterLoader;

    @Autowired
    private SignInstance signInstance;

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @Override public Result apply(Map map) {

        String code = CommonUtil.emptyStringParseNull(map.get("deviceId"));
        if (deviceApplyMapper.selectByCode(code) != null) {
            return new Result(true, "申请资料已提交，请勿重复申请");
        }
        DeviceApply deviceApply = JsonUtils.map2obj(map, DeviceApply.class);
        deviceApply.setCode(code);
        deviceApply.setStatus(0);
        deviceApply.setCreateTime(new Date());
        deviceApplyMapper.insertSelective(deviceApply);
        return new Result(true);
    }

    @Override
    public Result query(Map map) {
        Device device = deviceMapper.selectByCode(CommonUtil.emptyStringParseNull(map.get("deviceId")));
        if (device == null) {
            return new Result(false, "设备未注册");
        }
        DeviceGroup deviceGroup = deviceGroupMapper.selectByPrimaryKey(device.getDevice_group_id());
        if (deviceGroup == null) {
            return new Result(false, "传入参数异常");
        }
        Supply supply = supplyMapper.selectByPrimaryKey(deviceGroup.getSupply_id());
        if (supply == null) {
            return new Result(false, "传入参数异常");
        }
        Map mapRet = new HashMap();
        mapRet.put("merchantName", supply.getName());
        mapRet.put("deviceName", device.getName());
        mapRet.put("privateKey", device.getPrivateKey());
        Result result = new Result(true);
        result.put(mapRet);
        return result;
    }

    private Map getSupply(String code) {
        Device device = deviceMapper.selectByCode(code);
        if (device == null) {
            throw new ApiException("传入参数异常");
        }
        DeviceGroup deviceGroup = deviceGroupMapper.selectByPrimaryKey(device.getDevice_group_id());
        if (deviceGroup == null) {
            throw new ApiException("传入参数异常");
        }
        Supply supply = supplyMapper.selectByPrimaryKey(deviceGroup.getSupply_id());
        if (supply == null) {
            throw new ApiException("传入参数异常");
        }
        Map map = new HashMap();
        map.put("device", device);
        map.put("deviceGroup", deviceGroup);
        map.put("supply", supply);
        return map;
    }

    @Override
    public Result request(Map map) {
        String identity = CommonUtil.emptyStringParseNull(map.get("identity"));
        String action = CommonUtil.emptyStringParseNull(map.get("action"));
        String signed = CommonUtil.emptyStringParseNull(map.get("signed"));
        String content = CommonUtil.emptyStringParseNull(map.get("content"));
        Map data = getSupply(identity);
        Supply supply = (Supply) data.get("supply");
        if (!signInstance.checkSign(supply.getSignType(), supply.getPublicKey(), supply.getPrivateKey(), content,
                signed)) {
            // return new Result(false, "签名数据校检失败");
        }
        Map mapParam = getMapFormContent(content);
        mapParam.putAll(data);
        try {
            SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(supply);
            return supplyAdapterService.process(action, supply, mapParam);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApiException(ex);
        }
    }

    private Map getMapFormContent(String content) {
        Map mapContent = null;
        if (content.startsWith("[") && content.endsWith("]")) {
            List list = JsonUtils.json2List(content);
            mapContent = new HashMap();
            mapContent.put("data", list);
        } else {
            mapContent = JsonUtils.json2Map(content);
        }
        return mapContent;
    }
}
