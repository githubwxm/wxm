package com.all580.order.adapter;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.dto.RefundDay;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.util.AccountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/8 14:07
 */
@Component(OrderConstant.REFUND_ADAPTER + "HOTEL")
public class TicketHotelRefundOrderImpl extends AbstractRefundOrderImpl {
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Autowired
    @Override
    public void setOrderItemMapper(OrderItemMapper orderItemMapper) {
        super.orderItemMapper = orderItemMapper;
    }

    @Autowired
    @Override
    public void setOrderMapper(OrderMapper orderMapper) {
        super.orderMapper = orderMapper;
    }

    @Autowired
    @Override
    public void setRefundOrderManager(RefundOrderManager refundOrderManager) {
        super.refundOrderManager = refundOrderManager;
    }

    @Override
    public void canBeRefund(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {
        super.canBeRefund(apply, detailList, params);
        List<RefundOrder> refundOrders = refundOrderMapper.selectByItemId(apply.getItem().getId());
        if (refundOrders != null && refundOrders.size() > 0) {
            throw new ApiException("该订单只能退一次");
        }
    }

    @Override
    public Collection<RefundDay> getRefundDays(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {
        return AccountUtil.parseRefundDayForDetail(detailList);
    }

    @Override
    public int getRefundQuantity(RefundOrderApply apply, Collection<RefundDay> refundDays, Map params) {
        int totalRefundQuantity = 0;
        for (RefundDay refundDay : refundDays) {
            totalRefundQuantity += refundDay.getQuantity();
        }
        return totalRefundQuantity;
    }

    @Override
    public void hasRemainAndInsertRefundVisitor(RefundOrderApply apply, RefundOrder refundOrder, List<OrderItemDetail> detailList, Collection<RefundDay> refundDays, Map params) {
        orderItemDetailMapper.refundRemain(apply.getItem().getId());
    }
}
