package com.all580.order.adapter;

import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.PackageOrderItem;
import com.all580.order.entity.RefundOrder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by xiangzw on 2017/7/20.
 */
public interface RefundPackageOrderService {

    RefundOrderApply validateAndParseParams(long itemNo, Map params);

    void checkAuth(RefundOrderApply apply, Map params);

    void canBeRefund(RefundOrderApply apply, PackageOrderItem item, Map params);

    int getRefundQuantity(RefundOrderApply apply, Map params);

    int[] calcRefundMoneyAndFee(RefundOrderApply apply, PackageOrderItem item, Map params);

    int[] getRefundAudit(RefundOrderApply apply, Map params);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    RefundOrder insertRefundOrder(RefundOrderApply apply, int quantity, int money, int fee, int ticketAudit, int moneyAudit, Map params);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    void hasRemainAndInsertRefundVisitor(RefundOrderApply apply, RefundOrder refundOrder, PackageOrderItem item, Map params);
}
