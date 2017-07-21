package com.all580.order.adapter;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.PackageOrderItemMapper;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.PackageOrderItem;
import com.all580.order.entity.RefundOrder;
import com.framework.common.util.CommonUtil;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzw on 2017/7/20.
 */
@Component
public class PackageRefundOrderImpl implements RefundPackageOrderService{
    @Autowired
    private EpService epService;
    @Autowired
    private PackageOrderItemMapper packageOrderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public RefundOrderApply validateAndParseParams(long itemNo, Map params) {
        RefundOrderApply apply = new RefundOrderApply();
        apply.setItemNo(itemNo);
        PackageOrderItem packageOrderItem = packageOrderItemMapper.selectByNumber(itemNo);
        Assert.notNull(packageOrderItem, "订单不存在");
        apply.setPackageOrderItem(packageOrderItem);
        Order order = orderMapper.selectBySN(packageOrderItem.getOrder_number());
        Assert.notNull(order, "订单不存在");
        apply.setOrder(order);
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        Assert.notNull(orderItems, "元素子订单不存在");
        apply.setOrderItems(orderItems);

        for (OrderItem item : orderItems){
            if (ArrayUtils.indexOf(new int[]{
                    OrderConstant.OrderItemStatus.SEND,
                    OrderConstant.OrderItemStatus.NON_SEND,
                    OrderConstant.OrderItemStatus.TICKET_FAIL
            }, item.getStatus()) < 0 ||
                    order.getStatus() != OrderConstant.OrderStatus.PAID) {
                throw new ApiException("订单不在可退订状态");
            }
        }

        if (!params.containsKey(EpConstant.EpKey.EP_ID)) {
            throw new ApiException("非法请求:企业ID为空");
        }

        // 供应侧/销售侧
        Integer applyFrom = CommonUtil.objectParseInteger(params.get("apply_from"));
        apply.setFrom(applyFrom);
        apply.setEpId(CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)));
        apply.setCause(CommonUtil.objectParseString(params.get("cause")));
        apply.setQuantity(CommonUtil.objectParseInteger(params.get("quantity")));
        apply.setDate(new Date());
        apply.setUserId(CommonUtil.objectParseInteger(params.get("operator_id")));
        apply.setUserName(CommonUtil.objectParseString(params.get("operator_name")));
        apply.setOuter(CommonUtil.objectParseString(params.get("outer_id")));
        return apply;
    }

    @Override
    public void checkAuth(RefundOrderApply apply, Map params) {

    }

    @Override
    public void canBeRefund(RefundOrderApply apply, PackageOrderItem item, Map params) {

    }

    @Override
    public int getRefundQuantity(RefundOrderApply apply, Map params) {
        return 0;
    }

    @Override
    public int[] calcRefundMoneyAndFee(RefundOrderApply apply, PackageOrderItem item, Map params) {
        return new int[0];
    }

    @Override
    public int[] getRefundAudit(RefundOrderApply apply, Map params) {
        return new int[0];
    }

    @Override
    public RefundOrder insertRefundOrder(RefundOrderApply apply, int quantity, int money, int fee, int ticketAudit, int moneyAudit, Map params) {
        return null;
    }

    @Override
    public void hasRemainAndInsertRefundVisitor(RefundOrderApply apply, RefundOrder refundOrder, PackageOrderItem item, Map params) {

    }
}
