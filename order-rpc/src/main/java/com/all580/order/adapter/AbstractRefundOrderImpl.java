package com.all580.order.adapter;

import com.alibaba.fastjson.JSONObject;
import com.all580.ep.api.conf.EpConstant;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dto.RefundDay;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.util.AccountUtil;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.util.CommonUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/8 10:59
 */
public abstract class AbstractRefundOrderImpl implements RefundOrderInterface {
    protected OrderItemMapper orderItemMapper;
    protected OrderMapper orderMapper;
    protected RefundOrderManager refundOrderManager;

    public abstract void setOrderItemMapper(OrderItemMapper orderItemMapper);
    public abstract void setOrderMapper(OrderMapper orderMapper);
    public abstract void setRefundOrderManager(RefundOrderManager refundOrderManager);
    @Override
    public RefundOrderApply validateAndParseParams(long itemNo, Map params) {
        RefundOrderApply apply = new RefundOrderApply();
        apply.setItemNo(itemNo);
        OrderItem orderItem = orderItemMapper.selectBySN(itemNo);
        Assert.notNull(orderItem, "子订单不存在");
        apply.setItem(orderItem);
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        Assert.notNull(order, "订单不存在");
        apply.setOrder(order);

        if (ArrayUtils.indexOf(new int[]{
                OrderConstant.OrderItemStatus.SEND,
                OrderConstant.OrderItemStatus.NON_SEND,
                OrderConstant.OrderItemStatus.TICKET_FAIL
        }, orderItem.getStatus()) < 0 ||
                order.getStatus() != OrderConstant.OrderStatus.PAID) {
            throw new ApiException("订单不在可退订状态");
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
        // 检查权限
        refundOrderManager.checkApplyRefund(apply.getItem(), apply.getOrder(), apply.getFrom(), apply.getEpId());
    }

    @Override
    public void canBeRefund(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {
        for (OrderItemDetail detail : detailList) {
            String rule = apply.getFrom() == ProductConstants.RefundEqType.SELLER ? detail.getCust_refund_rule() : detail.getSaler_refund_rule();
            JSONObject jsonObject = JSONObject.parseObject(rule);
            Object tmp = jsonObject.get("refund");
            boolean refund = true;
            if (tmp != null) {
                String cs = tmp.toString();
                refund = StringUtils.isNumeric(cs) ? BooleanUtils.toBoolean(Integer.parseInt(cs)) : BooleanUtils.toBoolean(cs);
            }
            if (!refund) {
                throw new ApiException("该订单为不可退退货规则");
            }
        }
    }

    @Override
    public Collection<RefundDay> getRefundDays(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {
        return AccountUtil.decompileRefundDay((List) params.get("days"));
    }

    @Override
    public void validateRefundVisitor(RefundOrderApply apply, Collection<RefundDay> refundDays, Map params) {
        for (RefundDay refundDay : refundDays) {
            if (refundDay.getVisitors() != null && refundDay.getVisitors().size() > 1) {
                throw new ApiException("暂不支持一次退多人");
            }
        }
    }

    @Override
    public int getRefundQuantity(RefundOrderApply apply, Collection<RefundDay> refundDays, Map params) {
        return apply.getQuantity();
    }

    @Override
    public int[] calcRefundMoneyAndFee(RefundOrderApply apply, List<OrderItemDetail> detailList, Collection<RefundDay> refundDays, Map params) {
        return refundOrderManager.calcRefundMoneyAndFee(apply.getItem(), apply.getOrder(), apply.getFrom(), refundDays, detailList, apply.getDate());
    }

    @Override
    public int[] getRefundAudit(RefundOrderApply apply, Map params) {
        return refundOrderManager.getAuditConfig(apply.getOrder(), apply.getItem());
    }

    @Override
    public RefundOrder insertRefundOrder(RefundOrderApply apply, Collection<RefundDay> refundDays, int quantity, int money, int fee, int ticketAudit, int moneyAudit, Map params) {
        return refundOrderManager.generateRefundOrder(apply.getItem(), refundDays, quantity, money, fee, apply.getCause(), ticketAudit, moneyAudit, apply.getOrder().getPayee_ep_id(), apply.getUserId(), apply.getUserName(), apply.getOuter());
    }
}
