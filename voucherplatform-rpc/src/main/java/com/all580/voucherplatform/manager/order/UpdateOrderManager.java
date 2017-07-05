package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.utils.async.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Linv2 on 2017-06-13.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class UpdateOrderManager {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;
    private Order order;

    private Order updateOrder;

    public void setOrder(Integer platformId, String platformOrderCode, String seqId) throws Exception {
        this.updateOrder = new Order();
        this.order = orderMapper.selectByPlatform(platformId, platformOrderCode, seqId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        updateOrder.setId(order.getId());
    }

    public void setMobile(String mobile) {
        updateOrder.setMobile(mobile);
    }

    public void setIdNumber(String idNumber) {
        updateOrder.setSeqId(idNumber);
    }

    public void setValidTime(Date validTime) {
        updateOrder.setValidTime(validTime);
    }

    public void setInvalidTime(Date invalidTime) {
        updateOrder.setInvalidTime(invalidTime);
    }

    public void submit() {
        saveOrder();
        notifyPlatform(order.getPlatform_id(), order.getId());
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
    private void saveOrder() {
        orderMapper.updateByPrimaryKeySelective(updateOrder);
    }

    private void notifyPlatform(final Integer platformId, final Integer orderId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.update(orderId);
                }
            }
        });
    }
}
