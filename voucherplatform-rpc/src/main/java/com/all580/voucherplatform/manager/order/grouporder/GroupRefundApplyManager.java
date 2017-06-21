package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.RefundMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Refund;
import com.all580.voucherplatform.utils.sign.async.AsyncService;
import com.framework.common.lang.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class GroupRefundApplyManager {
    @Autowired
    private GroupOrderMapper groupOrderMapper;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private AdapterLoader adapterLoader;

    @Autowired
    private AsyncService asyncService;

    private GroupOrder groupOrder;

    public void setGroupOrder(Integer platformId, String platformOrderCode) {
        this.groupOrder = groupOrderMapper.selectByPlatform(platformId, platformOrderCode);
    }


    public void apply(String refId, Integer refNumber, Date refTime, String refReason) throws Exception {
        checkRef(refId, refNumber);
        Refund refund = new Refund();
        refund.setRefundCode(String.valueOf(UUIDGenerator.generateUUID()));
        refund.setOrder_id(groupOrder.getId());
        refund.setOrder_code(groupOrder.getOrderCode());
        refund.setPlatformRefId(refId);
        refund.setRefNumber(refNumber);
        refund.setRefTime(refTime);
        refund.setRefCause(refReason);
        refund.setRefStatus(VoucherConstant.RefundStatus.WAIT_CONFIRM);
        refund.setPlatform_id(groupOrder.getPlatform_id());
        refund.setPlatformprod_id(groupOrder.getPlatformprod_id());
        refund.setSupply_id(groupOrder.getSupply_id());
        refund.setSupplyprod_id(groupOrder.getSupplyProdId());
        refund.setCreateTime(new Date());
        refund.setProdType(VoucherConstant.ProdType.GROUP);
        refundMapper.insertSelective(refund);
        notifySupply(groupOrder.getTicketsys_id(), refund.getId());

    }

    private void checkRef(String refId, Integer refNumber) throws Exception {
        if (groupOrder == null) {
            throw new Exception("订单不存在");
        }

        if (StringUtils.isEmpty(refId)) {
            throw new IllegalArgumentException("无效的退票流水号");
        }
        if (refNumber == null || refNumber < 1) {
            throw new IllegalArgumentException("无效的退票数量");
        }
        Refund refund = refundMapper.selectBySeqId(null, null, groupOrder.getPlatform_id(), refId, VoucherConstant.ProdType.GROUP);
        if (refund != null) {
            throw new Exception("重复的退票操作请求");
        }
        refund = new Refund();
        refund.setOrder_id(groupOrder.getId());
        refund.setPlatformprod_id(groupOrder.getPlatform_id());
        refund.setOrder_code(groupOrder.getOrderCode());
        refund.setProdType(VoucherConstant.ProdType.GROUP);
        refund.setRefStatus(VoucherConstant.RefundStatus.WAIT_CONFIRM);
        if (refundMapper.selectCount(refund) > 0) {
            throw new Exception("当前存在未处理的退票");
        }
    }


    private void notifySupply(final Integer ticketSysId, final Integer refundId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(ticketSysId);
                if (supplyAdapterService != null) {
                    supplyAdapterService.refundGroup(refundId);

                }
            }
        });
    }
}
