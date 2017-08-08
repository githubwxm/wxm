package com.all580.order.adapter;

import com.all580.ep.api.conf.EpConstant;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.dto.RefundDay;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.*;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.util.AccountUtil;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.lang.exception.ApiException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzw on 2017/7/20.
 */
@Component
public class PackageRefundOrderImpl implements RefundPackageOrderService{
    @Autowired
    private PackageOrderItemMapper packageOrderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private RefundPackageOrderMapper refundPackageOrderMapper;
    @Autowired
    private PackageOrderItemAccountMapper packageOrderItemAccountMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private RefundPackageAccountMapper refundPackageAccountMapper;

    @Override
    public RefundOrderApply validateAndParseParams(long itemNo, Map params) {
        RefundOrderApply apply = new RefundOrderApply();
        apply.setItemNo(itemNo);
        PackageOrderItem packageOrderItem = packageOrderItemMapper.selectByNumber(itemNo);
        Assert.notNull(packageOrderItem, "套票订单不存在");
        apply.setPackageOrderItem(packageOrderItem);
        Order order = orderMapper.selectBySN(packageOrderItem.getOrder_number());
        Assert.notNull(order, "订单不存在");
        apply.setOrder(order);
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        Assert.notNull(orderItems, "元素子订单不存在");
        apply.setOrderItems(orderItems);

        RefundPackageOrder refundPackageOrder = refundPackageOrderMapper.selectByItemIdAndOuter(
                packageOrderItem.getId(), order.getOuter_id());
        if (refundPackageOrder != null){
            throw new ApiException("订单已发起退订");
        }

        for (OrderItem item : orderItems){
            if (ArrayUtils.indexOf(new int[]{
                    OrderConstant.OrderItemStatus.SEND,
                    OrderConstant.OrderItemStatus.NON_SEND,
                    OrderConstant.OrderItemStatus.TICKET_FAIL
            }, item.getStatus()) < 0 ||
                    order.getStatus() != OrderConstant.OrderStatus.PAID) {
                throw new ApiException("订单不在可退订状态");
            }
            List<RefundOrder> refundOrders = refundOrderMapper.selectByItemId(item.getId());
            if (!CollectionUtils.isEmpty(refundOrders)){
                throw new ApiException("元素订单已发起退订");
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
        //apply.setQuantity(CommonUtil.objectParseInteger(params.get("quantity")));
        apply.setQuantity(packageOrderItem.getQuantity());
        apply.setDate(new Date());
        apply.setUserId(CommonUtil.objectParseInteger(params.get("operator_id")));
        apply.setUserName(CommonUtil.objectParseString(params.get("operator_name")));
        apply.setOuter(CommonUtil.objectParseString(params.get("outer_id")));
        return apply;
    }

    @Override
    public void checkAuth(RefundOrderApply apply, Map params) {
        Integer epId = apply.getEpId();
        Order order = apply.getOrder();
        Integer applyFrom = apply.getFrom();
        OrderItem orderItem = new OrderItem();
        orderItem.setSupplier_ep_id(apply.getPackageOrderItem().getEp_id());
        refundOrderManager.checkApplyRefund(orderItem, order, applyFrom, epId);
    }

    @Override
    public void canBeRefund(RefundOrderApply apply, PackageOrderItem item, Map params) {
        String rule = apply.getFrom() == ProductConstants.RefundEqType.SELLER ? item.getCust_refund_rule() : item.getSaler_refund_rule();
        refundOrderManager.checkRefundRule(rule);
    }

    @Override
    public int getRefundQuantity(RefundOrderApply apply, Map params) {
        return apply.getQuantity();
    }

    @Override
    public int[] calcRefundMoneyAndFee(RefundOrderApply apply, PackageOrderItem item, Map params) {
        int [] calcResult = new int[]{0, 0};
        if (item.getPayment_flag() != ProductConstants.PayType.PAYS) {
            PackageOrderItemAccount itemAccount = packageOrderItemAccountMapper.selectByOrderItemAndEp(item.getId(),
                    apply.getOrder().getBuy_ep_id(), apply.getOrder().getPayee_ep_id());
            if (itemAccount == null) {
                throw new ApiException("数据异常,分账记录不存在");
            }
            //todo 组装参数
            OrderItemAccount account = new OrderItemAccount();
            account.setData(itemAccount.getData());

            OrderItemDetail detail = new OrderItemDetail();
            detail.setCust_refund_rule(item.getCust_refund_rule());
            detail.setSaler_refund_rule(item.getSaler_refund_rule());
            detail.setDay(item.getStart());

            RefundDay refundDay = new RefundDay();
            refundDay.setDay(apply.getDate());
            refundDay.setQuantity(apply.getQuantity());

            calcResult = AccountUtil.calcRefundMoneyAndFee(account, apply.getFrom(), Arrays.asList(refundDay), Arrays.asList(detail), apply.getDate());

            if (calcResult[0] < 0) {
                throw new ApiException("销售价小于退货手续费");
            }
        }
        return calcResult;
    }

    @Override
    public int[] getRefundAudit(RefundOrderApply apply, Map params) {
        PackageOrderItem item = apply.getPackageOrderItem();
        Order order = apply.getOrder();
        // 获取退订审核
        int[] auditSupplierConfig = refundOrderManager.getAuditConfig(item.getProduct_sub_id(), item.getCore_ep_id());
        int auditTicket = auditSupplierConfig[0];
        // 获取退款审核
        int auditMoney = 0;
        if (item.getCore_ep_id() == order.getPayee_ep_id().intValue()) {
            auditMoney = auditSupplierConfig[1];
        } else {
            auditMoney = refundOrderManager.getAuditConfig(item.getProduct_sub_id(), order.getPayee_ep_id())[1];
        }
        return new int[]{auditTicket, auditMoney};
    }

    @Override
    public RefundPackageOrder insertRefundOrder(RefundOrderApply apply, int quantity, int money, int fee, int ticketAudit, int moneyAudit, Map params) {
        RefundPackageOrder refundPackageOrder = new RefundPackageOrder();
        refundPackageOrder.setChannel_fee(refundOrderManager.getChannelRate(
                apply.getPackageOrderItem().getCore_ep_id(), apply.getOrder().getPayee_ep_id()));
        refundPackageOrder.setPackage_order_item_id(apply.getPackageOrderItem().getId());
        refundPackageOrder.setNumber(UUIDGenerator.generateUUID());
        refundPackageOrder.setQuantity(quantity);
        refundPackageOrder.setCreate_time(new Date());
        refundPackageOrder.setMoney(money);
        refundPackageOrder.setFee(fee);
        refundPackageOrder.setCause(apply.getCause());
        refundPackageOrder.setApply_user_id(apply.getUserId());
        refundPackageOrder.setApply_user_name(apply.getUserName());
        refundPackageOrder.setOuter_id(StringUtils.isEmpty(apply.getOuter()) ? "_" + refundPackageOrder.getNumber() : apply.getOuter());
        try {
            refundPackageOrderMapper.insertSelective(refundPackageOrder);
        } catch (DuplicateKeyException e) {
            if (e.getMessage().contains("'package_order_item_id'") && StringUtils.isNotEmpty(apply.getOuter())) {
                throw new ApiException(Result.UNIQUE_KEY_ERROR, "重复操作", refundPackageOrderMapper.selectByItemIdAndOuter(apply.getPackageOrderItem().getId(), apply.getOuter()));
            }
            throw e;
        }

        return refundPackageOrder;
    }

    @Override
    public void hasRemainAndInsertRefundVisitor(RefundOrderApply apply, RefundPackageOrder refundOrder, PackageOrderItem item, Map params) {
        //todo: 修改退订数
        PackageOrderItem packageOrderItem = apply.getPackageOrderItem();
        packageOrderItem.setRefund_quantity(refundOrder.getQuantity());
        packageOrderItem.setUpdate_time(new Date());
        packageOrderItemMapper.updateByPrimaryKeySelective(packageOrderItem);
    }

    @Override
    public void preRefundSplitAccount(RefundOrderApply apply, Order order, PackageOrderItem item, RefundPackageOrder refundPackageOrder) {
        List<PackageOrderItemAccount> accounts = packageOrderItemAccountMapper.selectByOrderItem(item.getId());
        List<RefundPackageAccount> refundAccounts = AccountUtil.refundPackageSplitAccount(order, accounts, item, apply.getFrom(), refundPackageOrder.getId(), apply.getDate());
        for (RefundPackageAccount refundAccount : refundAccounts) {
            // 余额 则要把最终销售商的钱退还
            if (order.getPayment_type() == PaymentConstant.PaymentType.BALANCE.intValue()) {
                if (refundAccount.getEp_id().intValue() == order.getBuy_ep_id() && refundAccount.getCore_ep_id().intValue() == order.getPayee_ep_id()) {
                    refundAccount.setMoney(refundAccount.getMoney() + refundPackageOrder.getMoney());
                    refundAccount.setProfit(refundAccount.getProfit() + refundPackageOrder.getMoney());
                }
            }
            refundPackageAccountMapper.insertSelective(refundAccount);
        }
    }
}
