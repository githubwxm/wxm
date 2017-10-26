package com.all580.order.adapter;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dto.RefundDay;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
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
 * @date 2017/2/8 14:25
 */
@Component(OrderConstant.REFUND_ADAPTER + OrderConstant.OrderAdapter.TICKET)
public class TicketRefundOrderImpl extends AbstractRefundOrderImpl {
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
    public void hasRemainAndInsertRefundVisitor(RefundOrderApply apply, RefundOrder refundOrder, List<OrderItemDetail> detailList, Collection<RefundDay> refundDays, Map params) {
        // 判断余票 并修改明细退票数量 创建游客退票信息
        int tmpQuantity = refundOrderManager.canRefundForDays(refundDays, detailList, refundOrder.getOrder_item_id(), refundOrder.getId());
        if (tmpQuantity != apply.getQuantity()) {
            throw new ApiException("退票总数与每天退票数不符");
        }
    }
}
