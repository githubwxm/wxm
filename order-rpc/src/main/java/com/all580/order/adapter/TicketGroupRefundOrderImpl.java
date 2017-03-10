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
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/8 14:07
 */
@Component(OrderConstant.REFUND_ADAPTER + "TICKET_GROUP")
public class TicketGroupRefundOrderImpl extends AbstractRefundOrderImpl {
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
    public RefundOrderApply validateAndParseParams(long itemNo, Map params) {
        RefundOrderApply apply = super.validateAndParseParams(itemNo, params);
        if (!(apply.getItem().getGroup_id() != null && apply.getItem().getGroup_id() != 0 &&
                apply.getItem().getPro_sub_ticket_type() != null && apply.getItem().getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM)) {
            throw new ApiException("该订单不是团队订单");
        }
        return apply;
    }

    @Override
    public void canBeRefund(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {
        super.canBeRefund(apply, detailList, params);
        List<RefundOrder> refundOrders = refundOrderMapper.selectByItemId(apply.getItem().getId());
        if (refundOrders != null && refundOrders.size() > 0) {
            throw new ApiException("该订单只能退一次");
        }
        if (apply.getItem().getUsed_quantity() != null && apply.getItem().getUsed_quantity() > 0) {
            Date expiryDate = detailList.get(detailList.size() - 1).getExpiry_date();
            if (apply.getDate().before(expiryDate)) {
                throw new ApiException("已使用后的必须过期才能退订");
            }
        }
    }

    @Override
    public Collection<RefundDay> getRefundDays(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {
        Collection<RefundDay> refundDays = AccountUtil.parseRefundDayForDetail(detailList);
        if (refundDays == null || refundDays.size() == 0 && apply.getItem().getLow_quantity() != null && apply.getItem().getLow_quantity() > 0) {
            throw new ApiException(Result.REFUNDABLE_LACK, "没有可退的票").dataMap().putData("low", apply.getItem().getLow_quantity());
        }
        return refundDays;
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
