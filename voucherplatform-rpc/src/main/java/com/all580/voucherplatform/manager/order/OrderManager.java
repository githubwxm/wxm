package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class OrderManager {
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private OrderMapper orderMapper;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    public Integer submitConsume(String seqId,
                                 Integer number,
                                 String address,
                                 Date consumeTime,
                                 String deviceId, final Order order) {

        ConsumeOrderManager consumeOrderManager = adapterLoader.getBean(ConsumeOrderManager.class);
        if (consumeOrderManager == null) throw new ApiException("没有找到核销订单管理器");
        DistributedReentrantLock distributedReentrantLock = distributedLockTemplate.execute(
                VoucherConstant.DISTRIBUTEDLOCKORDER + order.getOrderCode(), lockTimeOut);
        try {
            final Integer consumeId = consumeOrderManager.consume(seqId, number, address, consumeTime, deviceId, order);
            asyncService.run(new Runnable() {
                @Override
                public void run() {
                    try {
                        PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(order.getPlatform_id());
                        if (platformAdapterService != null) {
                            platformAdapterService.consume(consumeId);
                        }
                    } catch (Throwable e) {
                        log.error("异步调用异常", e);
                    }
                }
            });
            return consumeId;
        } catch (Throwable ex) {
            log.error("核销订单异常", ex);
            throw new ApiException(ex);
        } finally {
            distributedReentrantLock.unlock();
        }
    }

    public Integer submitConsume(Map params, Order order) {
        String consumeSeqId = CommonUtil.objectParseString(params.get("consumeSeqId"));
        String voucherId = CommonUtil.objectParseString(params.get("voucherId"));
        Date consumeTime = DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("consumeTime")));
        Integer consumeNumber = CommonUtil.objectParseInteger(params.get("consumeNumber"));
        String consumeAddress = CommonUtil.objectParseString(params.get("consumeAddress"));
        String deviceId = CommonUtil.objectParseString(params.get("deviceId"));
        if (order == null) order = getOrder(voucherId);
        return submitConsume(consumeSeqId, consumeNumber, consumeAddress, consumeTime, deviceId, order);
    }

    public Integer submitConsume(Map params) {
        return submitConsume(params, null);
    }

    public Order getOrder(String orderCode) {
        return orderMapper.selectByOrderCode(orderCode);
    }

    public Order getOrder(Integer orderId) {
        return orderMapper.selectByPrimaryKey(orderId);
    }

    public Order getOrder(Integer supplyId,
                         String supplyOrderId) throws Exception {
        return orderMapper.selectBySupply(supplyId, supplyOrderId);
    }
}
