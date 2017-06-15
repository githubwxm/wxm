package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.RefundMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Refund;
import com.all580.voucherplatform.utils.sign.async.AsyncService;
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
    private AdapterLoadder adapterLoadder;
    @Autowired
    private AsyncService asyncService;

    private Order order;
    private GroupOrder groupOrder;
    private Refund refund;

    public void setRefund(String voucherRefCode) throws Exception {
        refund = refundMapper.selectByRefCode(voucherRefCode);
        if (refund != null) {
            checkRefund();
        }

    }

    public void setRefund(Integer platformId, String platformRefId, int prodType) throws Exception {
        refund = refundMapper.selectBySeqId(null, null, platformId, platformRefId, prodType);
        checkRefund();
    }

    private void checkRefund() throws Exception {
        if (refund == null) {
            throw new Exception("要处理的退票数据不存在");
        }
        if (refund.getRefStatus() != VoucherConstant.RefundStatus.WAIT_CONFIRM) {
            throw new Exception("当前退票请求已处理");
        }
        if (refund.getProdType() == VoucherConstant.ProdType.GENERAL) {
            order = orderMapper.selectByPrimaryKey(refund.getOrder_id());
        } else if (refund.getProdType() == VoucherConstant.ProdType.GROUP) {
            groupOrder = groupOrderMapper.selectByPrimaryKey(refund.getOrder_id());
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
            updateOrder.setRefunding((updateOrder.getRefunding() == null ? 0 : updateOrder.getRefunding()) - refund.getRefNumber());
            updateOrder.setRefund((updateOrder.getRefund() == null ? 0 : updateOrder.getRefund()) + refund.getRefNumber());
            orderMapper.updateByPrimaryKeySelective(updateOrder);
        }

        notifyPlatform(order.getPlatform_id(), refund.getId(), refund.getProdType());
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
        notifyPlatform(order.getPlatform_id(), refund.getId(), refund.getProdType());
    }

    public void notifyPlatform(final Integer platformId, final Integer refundId, final Integer prodType) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoadder.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    if (prodType == VoucherConstant.ProdType.GENERAL) {
                        platformAdapterService.refundOrder(refundId);
                    } else {
                        platformAdapterService.refundGroup(refundId);
                    }
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
