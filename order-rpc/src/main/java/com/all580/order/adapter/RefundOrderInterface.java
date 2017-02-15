package com.all580.order.adapter;

import com.all580.order.dto.RefundDay;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.RefundOrder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/7 11:46
 */
public interface RefundOrderInterface {

    RefundOrderApply validateAndParseParams(long itemNo, Map params);

    void checkAuth(RefundOrderApply apply, Map params);

    void canBeRefund(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params);

    Collection<RefundDay> getRefundDays(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params);

    int getRefundQuantity(RefundOrderApply apply, Collection<RefundDay> refundDays, Map params);

    int[] calcRefundMoneyAndFee(RefundOrderApply apply, List<OrderItemDetail> detailList, Collection<RefundDay> refundDays, Map params);

    int[] getRefundAudit(RefundOrderApply apply, Map params);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    RefundOrder insertRefundOrder(RefundOrderApply apply, Collection<RefundDay> refundDays, int quantity, int money, int fee, int ticketAudit, int moneyAudit, Map params);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    void hasRemainAndInsertRefundVisitor(RefundOrderApply apply, RefundOrder refundOrder, List<OrderItemDetail> detailList, Collection<RefundDay> refundDays, Map params);
}
