package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.ConsumeMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.lang.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Linv2 on 2017-05-27.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class ConsumeOrderManager {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ConsumeMapper consumeMapper;

    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;


    private Order order;


    public ConsumeOrderManager() {}

    public void setOrder(Order order) throws Exception {
        this.order = order;
        checkData();
    }

    public void setOrder(String orderCode) throws Exception {
        this.order = orderMapper.selectByOrderCode(orderCode);
        checkData();
    }

    public void setOrder(Integer orderId) throws Exception {
        this.order = orderMapper.selectByPrimaryKey(orderId);
        checkData();
    }

    public void setOrder(Integer supplyId,
                         String supplyOrderId) throws Exception {
        this.order = orderMapper.selectBySupply(supplyId, supplyOrderId);
        checkData();
    }

    private void checkData() throws Exception {

        if (this.order == null) {
            throw new Exception("订单不存在");
        }
    }

    public Integer submitConsume(String seqId,
                                 Integer number,
                                 String address,
                                 Date consumeTime,
                                 String deviceId) {
        DistributedReentrantLock distributedReentrantLock = distributedLockTemplate.execute(
                VoucherConstant.DISTRIBUTEDLOCKORDER + order.getOrderCode(), lockTimeOut);
        try {
            Integer consumeId = Consume(seqId, number, address, consumeTime, deviceId);
            notifyPlatform(order.getPlatform_id(), consumeId);
            return consumeId;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return number;
        } finally {
            distributedReentrantLock.unlock();
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
    private Integer Consume(String seqId,
                            Integer number,
                            String address,
                            Date consumeTime,
                            String deviceId) throws Exception {


        if (StringUtils.isEmpty(seqId)) {
            throw new IllegalArgumentException("无效的验证流水号");
        }
        if (number == null || number < 1) {
            throw new IllegalArgumentException("无效的验证数量");
        }
        Consume consume = consumeMapper.selectBySeqId(number, null, order.getSupply_id(), seqId);
        if (consume != null) {
            throw new Exception("重复的验证操作请求");
        }
        Integer availableNumber = order.getNumber() -
                (order.getConsume() == null ? 0 : order.getConsume())
                + (order.getReverse() == null ? 0 : order.getReverse())
                - (order.getRefunding() == null ? 0 : order.getRefunding())
                - (order.getRefund() == null ? 0 : order.getRefund());
        if (number > availableNumber) {
            throw new Exception("消费数量大于订单剩余数量");
        }
        consume = new Consume();
        consume.setConsumeCode(String.valueOf(UUIDGenerator.generateUUID()));
        consume.setOrder_id(order.getId());
        consume.setOrder_code(order.getOrderCode());
        consume.setSupplyConsumeSeqId(seqId);
        consume.setConsumeNumber(number);
        consume.setPrevNumber(availableNumber);
        consume.setAfterNumber(consume.getPrevNumber() - consume.getConsumeNumber());
        consume.setConsumeTime(consumeTime);
        consume.setAddress(address);
        consume.setDeviceId(deviceId);
        consume.setSupply_id(order.getSupply_id());
        consume.setSupplyprod_id(order.getSupplyProdId());
        consume.setPlatform_id(order.getPlatform_id());
        consume.setPlatformprod_id(order.getPlatformprod_id());
        consume.setReverseStatus(false);
        consume.setCreateTime(new Date());
        consumeMapper.insertSelective(consume);
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setConsume(order.getConsume() + consume.getConsumeNumber());
        orderMapper.updateByPrimaryKeySelective(updateOrder);
        return consume.getId();
    }

    public void notifyPlatform(final Integer platformId,
                               final Integer consumeId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.consume(consumeId);
                }
            }
        });
    }

    public Order getOrder() {
        return order;
    }
}
