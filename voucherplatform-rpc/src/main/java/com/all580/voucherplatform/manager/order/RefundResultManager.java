package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.RefundMapper;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Refund;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.lang.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Linv2 on 2017-06-05.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class RefundResultManager {
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private GroupOrderMapper groupOrderMapper;

    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;

    private Order order;
    private Refund refund;


    public void setRefund(String voucherRefCode) throws Exception {
        refund = refundMapper.selectByRefCode(voucherRefCode);
        if (refund != null) {
            checkRefund();
        }

    }

    public void setRefund(Integer refundId) throws Exception {
        refund = refundMapper.selectByPrimaryKey(refundId);
        if (refund != null) {
            checkRefund();
        }

    }

    public void setRefund(Integer platformId, String platformRefId) throws Exception {
        refund = refundMapper.selectBySeqId(null, null, platformId, platformRefId, VoucherConstant.ProdType.GENERAL);
        checkRefund();
    }

    private void checkRefund() throws Exception {
        if (refund == null) {
            throw new Exception("要处理的退票数据不存在");
        }
        if (refund.getRefStatus() != VoucherConstant.RefundStatus.WAIT_CONFIRM) {
            throw new Exception("当前退票请求已处理");
        }

        order = orderMapper.selectByPrimaryKey(refund.getOrder_id());
    }

    /**
     * 根据订单状态剩余数量自动处理退票
     * 基本上是POS专用的接口
     *
     * @throws Exception
     */
    public void automatic() throws Exception {
        Integer number = order.getNumber();
        Integer consume = order.getConsume() == null ? 0 : order.getConsume();
        Integer refunded = order.getRefund() == null ? 0 : order.getRefund();
        Integer reverse = order.getReverse() == null ? order.getReverse() : order.getReverse();
        Integer validNum = number - consume - refunded + reverse;
        if (validNum >= refund.getRefNumber()) {
            refundSuccess(String.valueOf(UUIDGenerator.generateUUID()), new Date());
        } else {
            refundFail();
        }
    }

    public void refundSuccess(String supplyRefId, Date procTime) throws Exception {

        Refund refundUpdate = new Refund();
        refundUpdate.setId(refund.getId());
        refundUpdate.setRefStatus(VoucherConstant.RefundStatus.SUCCESS);
        refundUpdate.setSupplyRefSeqId(supplyRefId);
        refundUpdate.setSuccessTime(procTime);
        refundMapper.updateByPrimaryKeySelective(refundUpdate);

        if (refund.getProdType() == VoucherConstant.ProdType.GENERAL) {
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setRefunding((order.getRefunding() == null ? 0 : order.getRefunding()) - refund.getRefNumber());
            updateOrder.setRefund((order.getRefund() == null ? 0 : order.getRefund()) + refund.getRefNumber());
            orderMapper.updateByPrimaryKeySelective(updateOrder);
        }

        notifyPlatform(order.getPlatform_id(), refund.getId());
    }

    public void refundFail() {
        Refund refundUpdate = new Refund();
        refundUpdate.setId(refund.getId());
        refundUpdate.setRefStatus(VoucherConstant.RefundStatus.FAIL);
        refundUpdate.setSuccessTime(new Date());
        refundMapper.updateByPrimaryKeySelective(refundUpdate);
        if (refund.getProdType() == VoucherConstant.ProdType.GENERAL) {
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setRefunding((updateOrder.getRefunding() == null ? 0 : updateOrder.getRefunding()) - refund.getRefNumber());
            orderMapper.updateByPrimaryKeySelective(updateOrder);
        }
        notifyPlatform(order.getPlatform_id(), refund.getId());
    }

    public void notifyPlatform(final Integer platformId, final Integer refundId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.refundOrder(refundId);
                }
            }
        });
    }

    public Order getOrder() {
        return order;
    }


    public Refund getRefund() {
        return refund;
    }
}
