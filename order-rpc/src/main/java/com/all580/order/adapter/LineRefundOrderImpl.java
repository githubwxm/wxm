package com.all580.order.adapter;

import com.all580.order.api.OrderConstant;
import com.all580.order.dto.RefundDay;
import com.all580.order.dto.RefundOrderApply;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description: 线路退订
 * @date 17-5-24 下午2:40
 */
@Component(OrderConstant.REFUND_ADAPTER + OrderConstant.OrderAdapter.LINE)
public class LineRefundOrderImpl extends TicketRefundOrderImpl {
    @Override
    public void validateRefundVisitor(RefundOrderApply apply, Collection<RefundDay> refundDays, Map params) {

    }
}
