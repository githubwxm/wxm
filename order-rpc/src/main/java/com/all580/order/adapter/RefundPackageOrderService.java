package com.all580.order.adapter;

import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.Order;
import com.all580.order.entity.PackageOrderItem;
import com.all580.order.entity.RefundPackageOrder;
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
    RefundPackageOrder insertRefundOrder(RefundOrderApply apply, int quantity, int money, int fee, int ticketAudit, int moneyAudit, Map params);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    void hasRemainAndInsertRefundVisitor(RefundOrderApply apply, RefundPackageOrder refundOrder, PackageOrderItem item, Map params);

    void preRefundSplitAccount(RefundOrderApply apply, Order order, PackageOrderItem item, RefundPackageOrder refundPackageOrder);
}
