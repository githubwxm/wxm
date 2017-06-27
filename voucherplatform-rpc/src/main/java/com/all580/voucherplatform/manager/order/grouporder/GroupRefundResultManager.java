package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.RefundMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.Refund;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.lang.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Linv2 on 2017-06-21.
 */

@Component
@Scope(value = "prototype")
@Slf4j
public class GroupRefundResultManager {
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private GroupOrderMapper groupOrderMapper;

    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;

    private GroupOrder groupOrder;
    private Refund refund;

    public void setRefund(String voucherRefCode) throws Exception {
        refund = refundMapper.selectByRefCode(voucherRefCode);

        checkRefund();


    }

    public void setRefund(Integer refundId) throws Exception {
        refund = refundMapper.selectByPrimaryKey(refundId);
        checkRefund();


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
        groupOrder = groupOrderMapper.selectByPrimaryKey(refund.getOrder_id());

    }

    /**
     * 根据订单状态剩余数量自动处理退票
     * 基本上是POS专用的接口
     *
     * @throws Exception
     */
    public void automatic() throws Exception {
        Integer number = groupOrder.getNumber();
        Integer consume = groupOrder.getActivateNum() == null ? 0 : groupOrder.getActivateNum();
        Integer validNum = number - consume;
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
        notifyPlatform(groupOrder.getPlatform_id(), refund.getId());
    }

    public void refundFail() {
        Refund refundUpdate = new Refund();
        refundUpdate.setId(refund.getId());
        refundUpdate.setRefStatus(VoucherConstant.RefundStatus.FAIL);
        refundUpdate.setSuccessTime(new Date());
        refundMapper.updateByPrimaryKeySelective(refundUpdate);

        notifyPlatform(groupOrder.getPlatform_id(), refund.getId());
    }

    public void notifyPlatform(final Integer platformId, final Integer refundId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.refundGroup(refundId);
                }
            }
        });
    }

    public GroupOrder getGroupOrder() {
        return groupOrder;
    }

    public Refund getRefund() {
        return refund;
    }
}
