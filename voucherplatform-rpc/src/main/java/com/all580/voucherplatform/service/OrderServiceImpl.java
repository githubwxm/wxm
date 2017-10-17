package com.all580.voucherplatform.service;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.api.service.OrderService;
import com.all580.voucherplatform.dao.ConsumeSyncMapper;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.ConsumeSync;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.manager.order.OrderManager;
import com.all580.voucherplatform.manager.order.OrderReverseManager;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Linv2 on 2017/5/14.
 */

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private GroupOrderMapper groupOrderMapper;
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private OrderManager orderManager;
    @Autowired
    private ConsumeSyncMapper consumeSyncMapper;

    @Override
    public Result getOrder(int id) {
        Result<Order> result = new Result<>(true);
        Order order = orderManager.getOrder(id);
        if (order != null) {
            result.put(order);
        }
        return result;
    }

    @Override
    public Result getOrder(String orderCode) {
        Result<Order> result = new Result<>(true);
        Order order = orderManager.getOrder(orderCode);
        if (order != null) {
            result.put(order);
        }
        return result;
    }


    @Override
    public Result<PageRecord<Map>> selectOrderList(
            Integer platformId,
            Integer supplyId,
            String orderCode,
            String platformOrderId,
            String mobile,
            String idNumber,
            String voucherNumber,
            Integer status,
            Date startTime,
            Date endTime,
            Integer recordStart,
            Integer recordCount
                                                  ) {
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = orderMapper.selectOrderCount(platformId, supplyId, orderCode, platformOrderId, mobile, idNumber,
                voucherNumber, status,
                startTime, endTime);
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(
                    orderMapper.selectOrderList(platformId, supplyId, orderCode, platformOrderId, mobile, idNumber,
                            voucherNumber, status, startTime, endTime, recordStart, recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;
    }

    @Override
    public Result<PageRecord<Map>> selectOrderConsumeList(
            Integer platformId,
            Integer supplyId,
            String orderCode,
            String platformOrderId,
            String mobile,
            String idNumber,
            String voucherNumber,
            Integer status,
            Date startTime,
            Date endTime,
            String consumeCode,
            String supplyConsumeCode,
            String deviceId,
            Integer orderId,
            Integer recordStart,
            Integer recordCount
                                                         ) {
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = orderMapper.selectOrderConsumeCount(platformId, supplyId, orderCode, platformOrderId, mobile,
                idNumber, voucherNumber, status, startTime, endTime, consumeCode, supplyConsumeCode, deviceId, orderId);
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(
                    orderMapper.selectOrderConsumeList(platformId, supplyId, orderCode, platformOrderId, mobile,
                            idNumber, voucherNumber, status, startTime, endTime, consumeCode, supplyConsumeCode,
                            deviceId, orderId, recordStart, recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;
    }

    @Override
    public Result<PageRecord<Map>> selectOrderRefundList(
            Integer platformId,
            Integer supplyId,
            String orderCode,
            String platformOrderId,
            String mobile,
            String idNumber,
            String voucherNumber,
            Integer status,
            Date startTime,
            Date endTime,
            String platformRefId,
            String voucherRefId,
            String supplyRefId,
            Integer orderId,
            Integer prodType,
            Integer recordStart,
            Integer recordCount) {
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = orderMapper.selectOrderRefCount(platformId, supplyId, orderCode, platformOrderId, mobile,
                idNumber, voucherNumber, status, startTime, endTime, platformRefId, voucherRefId, supplyRefId,
                orderId, prodType);
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(
                    orderMapper.selectOrderRefList(platformId, supplyId, orderCode, platformOrderId, mobile,
                            idNumber, voucherNumber, status, startTime, endTime, platformRefId, voucherRefId,
                            supplyRefId,
                            orderId, prodType, recordStart, recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;
    }

    @Override public Result<PageRecord<Map>> selectGroupOrderList(Integer platformId,
                                                                  Integer supplyId,
                                                                  String orderCode,
                                                                  String platformOrderId,
                                                                  String mobile,
                                                                  String idNumber,
                                                                  String voucherNumber,
                                                                  Integer status,
                                                                  Date startTime,
                                                                  Date endTime,
                                                                  Integer recordStart,
                                                                  Integer recordCount) {
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = groupOrderMapper.selectOrderCount(platformId, supplyId, orderCode, platformOrderId, mobile,
                idNumber, voucherNumber, status, startTime, endTime);
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(
                    groupOrderMapper.selectOrderList(platformId, supplyId, orderCode, platformOrderId, mobile, idNumber,
                            voucherNumber, status, startTime, endTime, recordStart, recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;
    }

    @Override
    public Result reConsume(Integer consumeId) {
        OrderReverseManager orderReverseManager = adapterLoader.getBean(OrderReverseManager.class);
        orderReverseManager.setConsume(consumeId);
        orderReverseManager.submit(UUID.randomUUID().toString(), new Date());
        return new Result(true);
    }

    @Override
    public void consumeSync(Map params, boolean isGroup) {
        if (isGroup) {
            validateGroup(params);
            orderManager.submitConsumeGroup(params);
            return;
        }
        validate(params);
        orderManager.submitConsume(params);
    }

    @Override
    public void consumeSyncComplete(int id) {
        ConsumeSync consumeSync = consumeSyncMapper.selectByPrimaryKey(id);
        if (consumeSync == null) {
            log.warn("核销同步ID {} 不存在", id);
            return;
        }
        consumeSync.setStatus(4);
        consumeSyncMapper.updateByPrimaryKey(consumeSync);
    }

    @Override
    public Result<PageRecord<Map>> selectConsumeSyncByPage(List<String> auths, Date startTime, Date endTime, Integer recordStart, Integer recordCount) {
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = consumeSyncMapper.selectByTimeAndPlatformRoleCount(auths, startTime, endTime);
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(
                    consumeSyncMapper.selectByTimeAndPlatformRole(auths, startTime, endTime, recordStart, recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;
    }

    private void validateGroup(Map params) {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{"voucherId", "idNumbers", "product", "product.number"}, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"product.number"}, new ValidRule[]{new ValidRule.Digits()});
        ParamsMapValidate.validate(params, rules);
    }
    private void validate(Map params) {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{"voucherId", "consumeSeqId", "consumeTime", "consumeNumber", "consumeAddress", "deviceId"}, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{"consumeNumber"}, new ValidRule[]{new ValidRule.Digits()});
        ParamsMapValidate.validate(params, rules);
    }
}
