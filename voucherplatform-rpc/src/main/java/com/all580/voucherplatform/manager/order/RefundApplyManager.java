package com.all580.voucherplatform.manager.order;

/**
 * Created by Linv2 on 2017-06-05.
 */

import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.ConsumeMapper;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.RefundMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Refund;
import com.framework.common.lang.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Linv2 on 2017-05-27.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class RefundApplyManager {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private GroupOrderMapper groupOrderMapper;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private ConsumeMapper consumeMapper;
    private Order order;
    private GroupOrder groupOrder;
    private Integer prodType = VoucherConstant.ProdType.GENERAL;

    public RefundApplyManager() {}

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setOrder(String orderCode) {
        this.order = orderMapper.selectByOrderCode(orderCode);
    }

    public void setOrder(Integer orderId) {
        this.order = orderMapper.selectByPrimaryKey(orderId);
    }

    public void setOrder(Integer supplyId, String supplyOrderId) {
        this.order = orderMapper.selectBySupply(supplyId, supplyOrderId);
    }

    public void setOrder(Integer platformId, String platformOrderCode, String seqId) {
        this.order = orderMapper.selectByPlatform(platformId, platformOrderCode, seqId);
    }

    public void setGroupOrder(Integer platformId, String platformOrderCode) {
        prodType = VoucherConstant.ProdType.GROUP;
        this.groupOrder = groupOrderMapper.selectByPlatform(platformId, platformOrderCode);
    }

    public void apply(String refId, Integer refNumber, Date refTime, String refReason) throws Exception {
        if (prodType == VoucherConstant.ProdType.GENERAL) {
            if (order == null) {
                throw new Exception("订单不存在");
            }
        } else {
            if (groupOrder == null) {
                throw new Exception("订单不存在");
            }
        }
        if (StringUtils.isEmpty(refId)) {
            throw new IllegalArgumentException("无效的退票流水号");
        }
        if (refNumber == null || refNumber < 1) {
            throw new IllegalArgumentException("无效的退票数量");
        }
        Refund refund = refundMapper.selectBySeqId(null, null, order.getPlatform_id(), refId, prodType);
        if (refund != null) {
            throw new Exception("重复的退票操作请求");
        }
        refund = new Refund();
        refund.setRefundCode(String.valueOf(UUIDGenerator.generateUUID()));
        refund.setOrder_id(order.getId());
        refund.setOrder_code(order.getOrderCode());
        refund.setPlatformRefId(refId);
        refund.setRefNumber(refNumber);
        refund.setRefTime(refTime);
        refund.setRefCause(refReason);
        refund.setRefStatus(VoucherConstant.RefundStatus.WAIT_CONFIRM);
        refund.setPlatform_id(order.getPlatform_id());
        refund.setPlatformprod_id(order.getPlatformprod_id());
        refund.setSupply_id(order.getSupply_id());
        refund.setSupplyprod_id(order.getSupplyProdId());
        refund.setCreateTime(new Date());
        refund.setProdType(prodType);
        refundMapper.insertSelective(refund);
        if (prodType == VoucherConstant.ProdType.GENERAL) {
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setRefunding(order.getRefunding() + refund.getRefNumber());
            orderMapper.updateByPrimaryKeySelective(updateOrder);

        }
    }
}
