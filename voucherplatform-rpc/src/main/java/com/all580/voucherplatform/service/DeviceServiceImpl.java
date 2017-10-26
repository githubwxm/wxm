package com.all580.voucherplatform.service;

import com.all580.voucherplatform.api.service.DeviceService;
import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.Device;
import com.all580.voucherplatform.entity.DeviceApply;
import com.all580.voucherplatform.entity.DeviceGroup;
import com.all580.voucherplatform.entity.DeviceProduct;
import com.all580.voucherplatform.utils.sign.SignInstance;
import com.all580.voucherplatform.utils.sign.SignKey;
import com.all580.voucherplatform.utils.sign.SignService;
import com.all580.voucherplatform.utils.sign.SignType;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Linv2 on 2017-07-18.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceApplyMapper deviceApplyMapper;
    @Autowired
    private DeviceGroupMapper deviceGroupMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceProductMapper deviceProductMapper;
    @Autowired
    private SupplyProductMapper supplyProductMapper;
    @Autowired
    private SignInstance signInstance;

    @Override
    public Result addGroup(Map map) {
        DeviceGroup deviceGroup = JsonUtils.map2obj(map, DeviceGroup.class);
        SignService signService = signInstance.getSignService(deviceGroup.getSignType());
        SignKey signKey = signService.generate();
        deviceGroup.setSignType(signService.getSignType().getValue());
        deviceGroup.setPrivateKey(signKey.getPrivateKey());
        deviceGroup.setPublicKey(signKey.getPublicKey());
        deviceGroup.setCreateTime(new Date());
        deviceGroup.setStatus(true);
        deviceGroup.setCode(UUIDGenerator.getUUID());
        deviceGroupMapper.insert(deviceGroup);
        return new Result(true);
    }

    @Override
    public Result<PageRecord<Map>> selectGroupList(Integer supplyId,
                                                   String code,
                                                   String name,
                                                   Integer recordStart,
                                                   Integer recordCount) {
        PageRecord<Map> record = new PageRecord<>();
        int count = deviceGroupMapper.selectGroupCount(supplyId, code, name);
        record.setTotalCount(count);
        if (count > 0) {
            List<Map> list = deviceGroupMapper.selectGroupList(supplyId, code, name, recordStart, recordCount);
            record.setList(list);
        } else {

            record.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);
        return result;
    }

    @Override
    public Result addDevice(Map map) {
        String code = CommonUtil.emptyStringParseNull(map.get("code"));
        if (deviceMapper.selectByCode(code) != null) {
            return new Result(true, "该设备已在设备组存在，请解除关系后重新添加");
        }
        Device device = JsonUtils.map2obj(map, Device.class);
        SignService signService = signInstance.getSignService(SignType.rsa.getValue());
        SignKey signKey = signService.generate();
        // device.setCode(UUIDGenerator.getUUID());
        device.setSignType(signService.getSignType().getValue());
        device.setPrivateKey(signKey.getPrivateKey());
        device.setPublicKey(signKey.getPublicKey());
        device.setCreateTime(new Date());
        device.setStatus(true);
        deviceMapper.insertSelective(device);
        return new Result(true);
    }

    @Override
    public Result delDevice(String code) {
        Device device = deviceMapper.selectByCode(code);
        if (device != null) {
            Device del = new Device();
            del.setId(device.getId());
            del.setStatus(false);
            deviceMapper.updateByPrimaryKeySelective(del);
        }
        return new Result(true);
    }

    @Override
    public Result renameDevice(Map map) {
        String name = CommonUtil.objectParseString(map.get("name"));
        Integer id = CommonUtil.objectParseInteger(map.get("id"));
        Device device = new Device();
        device.setId(id);
        device.setName(name);
        deviceMapper.updateByPrimaryKeySelective(device);
        return new Result(true);
    }

    @Override
    public Result<PageRecord<Map>> selectDeviceList(Integer groupId,
                                                    Integer supplyId,
                                                    String code,
                                                    String name,
                                                    Integer recordStart,
                                                    Integer recordCount) {
        PageRecord<Map> record = new PageRecord<>();
        int count = deviceMapper.selectDeviceCount(groupId, supplyId, code, name);
        record.setTotalCount(count);
        if (count > 0) {
            List<Map> list = deviceMapper.selectDeviceList(groupId, supplyId, code, name, recordStart, recordCount);
            record.setList(list);
        } else {

            record.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);
        return result;
    }

    @Override
    public Result setProd(Integer groupId,
                          List<Map> list) {
        DeviceGroup deviceGroup = deviceGroupMapper.selectByPrimaryKey(groupId);
        if (deviceGroup == null || !deviceGroup.getStatus()) {
            return new Result(false, "设备组错误");
        }
        for (Map map : list) {
            String status = CommonUtil.objectParseString(map.get("status"));
            Integer prodId = CommonUtil.objectParseInteger(map.get("prodId"));
            DeviceProduct deviceProduct = deviceProductMapper.selectByProdId(groupId, prodId);
            if (status == "true" || status == "1") {
                if (deviceProduct == null) {
                    deviceProduct = new DeviceProduct();
                    deviceProduct.setDevice_group_id(groupId);
                    deviceProduct.setSupplyprod_id(prodId);
                    deviceProduct.setCreateTime(new Date());
                    deviceProduct.setStatus(true);
                    deviceProductMapper.insertSelective(deviceProduct);
                }
            } else {
                DeviceProduct delProd = new DeviceProduct();
                delProd.setId(deviceProduct.getId());
                delProd.setStatus(false);
                deviceProductMapper.updateByPrimaryKeySelective(delProd);
            }
        }
        return new Result(true);
    }

    @Override
    public Result<PageRecord<Map>> getProd(Integer groupId,
                                           Integer recordStart,
                                           Integer recordCount) {
        DeviceGroup deviceGroup = deviceGroupMapper.selectByPrimaryKey(groupId);
        if (deviceGroup == null) {
            return new Result<>(false);
        }
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = supplyProductMapper.selectSupplyProdCount(deviceGroup.getSupply_id(), null);
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(
                    deviceProductMapper.selectProdList(deviceGroup.getId(), deviceGroup.getSupply_id(), recordStart,
                            recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;
    }


    @Override
    public Result deviceAudit(Map map) {
        Integer applyId = CommonUtil.objectParseInteger(map.get("applyId"));
        Integer groupId = CommonUtil.objectParseInteger(map.get("groupId"), 0);
        DeviceApply deviceApply = deviceApplyMapper.selectByPrimaryKey(applyId);
        if (deviceApply == null || deviceApply.getStatus() != 0) {
            return new Result(false, "要审核的数据状态不正确，请刷新页面重试");
        }
        if (groupId < 1) {
            updateApplyStatus(applyId, 2);
            return new Result(true);
        } else {
            updateApplyStatus(applyId, 1);
            Map addMap = new HashMap();
            addMap.put("name", deviceApply.getCode());
            addMap.put("code", deviceApply.getCode());
            addMap.put("device_group_id", groupId);
            return addDevice(addMap);
        }
    }


    private void updateApplyStatus(Integer applyId,
                                   Integer status) {
        DeviceApply deviceApply = new DeviceApply();
        deviceApply.setId(applyId);
        deviceApply.setStatus(status);
        deviceApplyMapper.updateByPrimaryKeySelective(deviceApply);
    }

    public Result<PageRecord<Map>> selectApplyList(String code,
                                                   Integer status,
                                                   Integer recordStart,
                                                   Integer recordCount) {
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = deviceApplyMapper.selectApplyCount(code, status);
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(deviceApplyMapper.selectApplyList(code, status, recordStart, recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;
    }
}
