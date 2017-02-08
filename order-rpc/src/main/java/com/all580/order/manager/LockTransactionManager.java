package com.all580.order.manager;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.order.adapter.RefundOrderInterface;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.*;
import com.all580.order.dao.*;
import com.all580.order.dto.AccountDataDto;
import com.all580.order.dto.RefundDay;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.*;
import com.all580.order.util.AccountUtil;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventManager;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import com.github.ltsopensource.core.domain.Action;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 锁事务分离的处理类
 * @date 2017/1/3 16:59
 */
@Component
@Slf4j
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class LockTransactionManager {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private RefundSerialMapper refundSerialMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private SmsManager smsManager;
    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private EpService epService;
    @Autowired
    private ThirdPayService thirdPayService;

    /**
     * 支付回调
     * @param orderId
     * @param outTransId
     * @param serialNum
     */
    @MnsEvent
    public void paymentCallback(int orderId, String outTransId, String serialNum) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("支付成功任务,订单不存在");
            throw new ApiException("订单不存在");
        }

        if (order.getStatus() != OrderConstant.OrderStatus.PAYING) {
            log.warn("支付成功任务,订单:{}状态:{}不是支付中", order.getNumber(), order.getStatus());
            throw new ApiException("订单不在支付中状态");
        }
        order.setThird_serial_no(outTransId);
        order.setStatus(OrderConstant.OrderStatus.PAID_HANDLING); // 已支付,处理中
        // 到付 和 余额支付直接支付成功 其它的在分账的时候设置
        if (order.getPayment_type() == null && order.getPay_amount() == 0 ||
                (order.getPayment_type() != null && order.getPayment_type() == PaymentConstant.PaymentType.BALANCE.intValue())) {
            order.setStatus(OrderConstant.OrderStatus.PAID); // 已支付
        }
        orderMapper.updateByPrimaryKeySelective(order);

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        List<ProductSearchParams> lockParams = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            lockParams.add(bookingOrderManager.parseParams(orderItem));
        }

        // 加已售
        com.framework.common.Result result = productSalesPlanRPCService.addSoldProductStocks(lockParams);
        if (!result.isSuccess()) {
            log.warn("支付成功加已售失败");
            throw new ApiException(result.getError());
        }

        // 子订单状态为未出票
        orderItemMapper.setStatusByOrderId(orderId, OrderConstant.OrderItemStatus.NON_SEND);

        // 触发事件
        MnsEventManager.addEvent(OrderConstant.EventType.PAID, order.getId());

        if (order.getStatus() == OrderConstant.OrderStatus.PAID) {
            // 出票
            MnsEventManager.addEvent(OrderConstant.EventType.SPLIT_CREATE_ACCOUNT, order.getId());
        }
    }

    /**
     * 退款回调
     * @param orderId
     * @param ordCode
     * @param serialNum
     * @param success
     * @throws Exception
     */
    @MnsEvent
    public void refundMoneyCallback(int orderId, Long ordCode, String serialNum, boolean success) throws Exception {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("退款成功回调,订单不存在");
            throw new Exception("订单不存在");
        }

        MnsEventManager.addEvent(OrderConstant.EventType.REFUND_MONEY, new RefundMoneyEventParam(orderId, serialNum, success));
        if (order.getStatus() == OrderConstant.OrderStatus.PAID_HANDLING) {
            if (!success) {
                addRefundMoneyJob(ordCode, serialNum);
                return;
            }
            // 已支付,处理中(分账失败)退订 直接取消
            // 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", order.getId().toString());
            bookingOrderManager.addJob(OrderConstant.Actions.CANCEL_CALLBACK, jobParams);
            return;
        }

        refundOrderManager.refundMoneyAfter(Long.valueOf(serialNum), success);
        if (!success) {
            addRefundMoneyJob(ordCode, serialNum);
        }
    }

    /**
     * 支付分账
     * @param orderId
     * @return
     */
    @MnsEvent
    public com.github.ltsopensource.tasktracker.Result paymentSplitAccount(int orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("支付分账,订单不存在");
            throw new ApiException("订单不存在");
        }

        if (order.getStatus() != OrderConstant.OrderStatus.PAID_HANDLING) {
            log.warn("支付分账,订单:{}状态:{}不是分账中", order.getNumber(), order.getStatus());
            throw new ApiException("订单不在分账中状态");
        }

        // 更新状态已支付
        order.setStatus(OrderConstant.OrderStatus.PAID);
        orderMapper.updateByPrimaryKeySelective(order);
        // 分账
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        List<BalanceChangeInfo> balanceChangeInfoList = bookingOrderManager.packagingPaySplitAccount(order, orderItems);
        Result<BalanceChangeRsp> result = bookingOrderManager.changeBalances(PaymentConstant.BalanceChangeType.PAY_SPLIT, String.valueOf(order.getNumber()), balanceChangeInfoList);
        if (!result.isSuccess()) {
            log.warn("支付分账失败:{}", JsonUtils.toJson(result.get()));
            throw new ApiException(result.getError());
        }

        // 出票
        MnsEventManager.addEvent(OrderConstant.EventType.SPLIT_CREATE_ACCOUNT, order.getId());
        return new com.github.ltsopensource.tasktracker.Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 申请退订
     * @param params
     * @return
     * @throws Exception
     */
    @MnsEvent
    public Result<?> applyRefund(Map params, long itemNo, RefundOrderInterface refundOrderInterface) throws Exception {
        RefundOrderApply apply = refundOrderInterface.validateAndParseParams(itemNo, params);

        refundOrderInterface.checkAuth(apply, params);

        // 每日订单详情
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(apply.getItem().getId());
        Assert.notEmpty(detailList, "每天详情异常");
        refundOrderInterface.canBeRefund(apply, detailList, params);
        // 每日退票详情
        Collection<RefundDay> refundDays = refundOrderInterface.getRefundDays(apply, detailList, params);
        Assert.notEmpty(refundDays, "缺少退票详情");
        // 总退票数量
        Integer quantity = refundOrderInterface.getRefundQuantity(apply, refundDays, params);
        Assert.notNull(quantity, "退票数量异常");

        int[] calcResult = refundOrderInterface.calcRefundMoneyAndFee(apply, detailList, refundDays, params);
        // 计算退款金额
        int money = calcResult[0];
        // 手续费
        int fee = calcResult[1];

        int[] auditSupplierConfig = refundOrderInterface.getRefundAudit(apply, params);
        int auditTicket = auditSupplierConfig[0];
        int auditMoney = auditSupplierConfig[1];
        // 创建退订订单
        RefundOrder refundOrder = refundOrderInterface.insertRefundOrder(apply, refundDays, quantity, money, fee, auditTicket, auditMoney, params);

        refundOrderInterface.hasRemainAndInsertRefundVisitor(apply, refundOrder, detailList, refundDays, params);

        // 退订分账 到付退订不分帐
        if (apply.getItem().getPayment_flag() != ProductConstants.PayType.PAYS) {
            refundOrderManager.preRefundSplitAccount(apply.getFrom(), refundOrder.getId(), apply.getOrder(), apply.getDate(), refundOrder.getOrder_item_id(), detailList, refundDays);
        }

        // 触发事件
        MnsEventManager.addEvent(OrderConstant.EventType.ORDER_REFUND_APPLY, refundOrder.getId());
        return new Result<>(true);
    }

    /**
     * 取消已支付未分账的订单
     * @param orderSn
     * @return
     */
    public Result<?> cancelNoSplit(String orderSn) {
        Order order = orderMapper.selectBySN(Long.valueOf(orderSn));
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (order.getStatus() != OrderConstant.OrderStatus.PAID_HANDLING) {
            throw new ApiException("订单不在已支付处理中状态");
        }
        // 退款,取消在退款回调中执行
        refundOrderManager.refundMoney(order, order.getPay_amount(), null, -1);
        return new Result<>(true);
    }

    /**
     * 退订审核
     * @param params
     * @param refundSn
     * @return
     * @throws Exception
     */
    @MnsEvent
    public Result<?> auditRefund(Map params, String refundSn) throws Exception {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(refundSn));
        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        if (refundOrder.getStatus() != OrderConstant.RefundOrderStatus.AUDIT_WAIT) {
            throw new ApiException("退订订单不在待审核状态");
        }
        refundOrder.setAudit_time(new Date());
        refundOrder.setAudit_user_id(CommonUtil.objectParseInteger(params.get("operator_id")));
        refundOrder.setAudit_user_name(CommonUtil.objectParseString(params.get("operator_name")));
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.CORE_EP_ID)).equals(String.valueOf(orderItem.getSupplier_core_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能审核该退订订单");
        }
        boolean status = Boolean.parseBoolean(params.get("status").toString());

        refundOrderMapper.updateByPrimaryKeySelective(refundOrder);
        MnsEventManager.addEvent(OrderConstant.EventType.ORDER_REFUND_AUDIT, new RefundAuditEventParam(refundOrder.getId(), status));
        return new Result<>(true);
    }

    /**
     * 预定审核
     * @param params
     * @param orderItemSn
     * @return
     */
    @MnsEvent
    public Result<?> auditBooking(Map params, long orderItemSn) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderItemSn);
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        if (orderItem.getStatus() != OrderConstant.OrderItemStatus.AUDIT_WAIT) {
            throw new ApiException("订单不在待审状态");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT) {
            throw new ApiException("订单不在待审状态");
        }
        if (!params.containsKey(EpConstant.EpKey.EP_ID)) {
            throw new ApiException("非法请求:企业ID为空");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.EP_ID)).equals(String.valueOf(orderItem.getSupplier_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能审核该订单");
        }

        orderItem.setAudit_user_id(CommonUtil.objectParseInteger(params.get("operator_id")));
        orderItem.setAudit_user_name(CommonUtil.objectParseString(params.get("operator_name")));
        orderItem.setAudit_time(new Date());
        boolean status = Boolean.parseBoolean(params.get("status").toString());
        if (status) {
            orderItem.setStatus(OrderConstant.OrderItemStatus.AUDIT_SUCCESS);
            boolean allAudit = bookingOrderManager.isOrderAllAudit(orderItem.getOrder_id(), orderItem.getId());
            if (allAudit) {
                order.setStatus(OrderConstant.OrderStatus.PAY_WAIT);
                order.setAudit_time(new Date());
                // 判断是否需要支付
                if (order.getPay_amount() <= 0) { // 不需要支付
                    bookingOrderManager.addPaymentCallback(order);
                }
                orderMapper.updateByPrimaryKeySelective(order);
            }
        }
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
        MnsEventManager.addEvent(OrderConstant.EventType.ORDER_AUDIT, new OrderAuditEventParam(orderItem.getId(), status));
        return new Result<>(true);
    }

    /**
     * 支付
     * @param params
     * @param sn
     * @param payType
     * @return
     */
    public Result<?> payment(Map params, Long sn, Integer payType) {
        Order order = orderMapper.selectBySN(sn);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (order.getStatus() != OrderConstant.OrderStatus.PAY_WAIT &&
                order.getStatus() != OrderConstant.OrderStatus.PAY_FAIL &&
                order.getStatus() != OrderConstant.OrderStatus.PAYING) {
            throw new ApiException("订单不在待支付状态");
        }
        if (order.getPay_amount() <= 0) {
            throw new ApiException("该订单不需要支付");
        }
        if (!params.containsKey(EpConstant.EpKey.EP_ID)) {
            throw new ApiException("非法请求:企业ID为空");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.EP_ID)).equals(String.valueOf(order.getBuy_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能支付该订单");
        }

        order.setStatus(OrderConstant.OrderStatus.PAYING);
        order.setLocal_payment_serial_no(String.valueOf(UUIDGenerator.generateUUID()));
        order.setPayment_type(payType);
        order.setPay_time(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        // 调用支付RPC
        // 余额支付
        if (payType == PaymentConstant.PaymentType.BALANCE.intValue()) {
            // 余额支付需要扣除销售商的钱
            List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderAnEp(order.getId(), order.getBuy_ep_id(), order.getPayee_ep_id());
            for (OrderItemAccount account : accounts) {
                // 把data JSON 反编译为JAVA类型
                Collection<AccountDataDto> dataDtoList = AccountUtil.decompileAccountData(account.getData());
                // 获取总出货价
                int totalOutPrice = AccountUtil.getTotalOutPrice(dataDtoList);
                account.setMoney(account.getMoney() + (-totalOutPrice));
                account.setProfit(account.getMoney());
                orderItemAccountMapper.updateByPrimaryKeySelective(account);
            }

            // 获取余额变动信息
            List<BalanceChangeInfo> balanceChangeInfoList = bookingOrderManager.packagingPaySplitAccount(order);

            // 支付
            Result result = bookingOrderManager.changeBalances(
                    PaymentConstant.BalanceChangeType.BALANCE_PAY,
                    order.getNumber().toString(), balanceChangeInfoList);
            if (!result.isSuccess()) {
                log.warn("余额支付失败:{}", JsonUtils.toJson(result));
                throw new ApiException(result.getError());
            }
            return new Result<>(true);
        }
        // 第三方支付
        // 获取商品名称
        List<String> names = orderItemMapper.getProductNamesByOrderId(order.getId());
        List<Long> ids = orderItemMapper.getProductIdsByOrderId(order.getId());
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("prodName", StringUtils.join(names, ","));
        payParams.put("totalFee", order.getPay_amount());
        payParams.put("serialNum", order.getNumber().toString());
        payParams.put("prodId", StringUtils.join(ids, ","));

        Result result = thirdPayService.reqPay(order.getNumber(),
                bookingOrderManager.getCoreEpId(epService.selectPlatformId(order.getBuy_ep_id())),
                payType, payParams);
        if (!result.isSuccess()) {
            log.warn("第三方支付异常:{}", JsonUtils.toJson(result));
            throw new ApiException(result.getError());
        }
        return result;
    }

    /**
     * 退票
     * @param info
     * @param procTime
     * @param orderItem
     * @param refundOrder
     * @return
     */
    @MnsEvent
    public Result refundTicket(RefundTicketInfo info, Date procTime, OrderItem orderItem, RefundOrder refundOrder) {
        RefundSerial refundSerial = refundSerialMapper.selectByLocalSn(Long.valueOf(info.getRefId()));
        if (refundSerial == null) {
            return new Result(false, "退票流水错误");
        }

        if (refundSerial.getRefund_time() != null) {
            return new Result(false, "退票流水:" + info.getRefId() + "重复操作");
        }

        // 退票失败
        if (!info.isSuccess()) {
            return refundFail(refundOrder);
        }

        refundOrder.setRefund_ticket_time(procTime);

        //refundSerial.setRemoteSerialNo(refundSn);
        refundSerial.setRefund_time(procTime);

        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        visitor.setReturn_quantity(visitor.getReturn_quantity() + refundSerial.getQuantity());
        visitorMapper.updateByPrimaryKeySelective(visitor);

        RefundVisitor refundVisitor = refundVisitorMapper.selectByRefundIdAndVisitorId(refundOrder.getId(), info.getVisitorSeqId());
        refundVisitor.setReturn_quantity(refundVisitor.getReturn_quantity() + refundSerial.getQuantity());
        refundVisitor.setPre_quantity(0);
        refundVisitorMapper.updateByPrimaryKeySelective(refundVisitor);
        orderItem.setRefund_quantity(orderItem.getRefund_quantity() + refundSerial.getQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
        refundSerialMapper.updateByPrimaryKeySelective(refundSerial);

        // 退款
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), refundOrder);

        MnsEventManager.addEvent(OrderConstant.EventType.REFUND_TICKET, new RefundTicketEventParam(refundOrder.getId(), info.isSuccess()));
        return new Result(true);
    }

    /**
     * 退票(团队)
     * @param info
     * @param procTime
     * @param orderItem
     * @param refundOrder
     * @return
     */
    @MnsEvent
    public Result refundGroupTicket(RefundGroupTicketInfo info, Date procTime, OrderItem orderItem, RefundOrder refundOrder) {
        RefundSerial refundSerial = refundSerialMapper.selectByLocalSn(Long.valueOf(info.getRefId()));
        if (refundSerial == null) {
            return new Result(false, "退票流水错误");
        }

        if (refundSerial.getRefund_time() != null) {
            return new Result(false, "退票流水:" + info.getRefId() + "重复操作");
        }
        // 退票失败
        if (!info.isSuccess()) {
            return refundFail(refundOrder);
        }

        refundOrder.setRefund_ticket_time(procTime);

        //refundSerial.setRemoteSerialNo(refundSn);
        refundSerial.setRefund_time(procTime);

        orderItem.setRefund_quantity(orderItem.getRefund_quantity() + refundSerial.getQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
        refundSerialMapper.updateByPrimaryKeySelective(refundSerial);

        // 退款
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), refundOrder);

        MnsEventManager.addEvent(OrderConstant.EventType.REFUND_TICKET, new RefundTicketEventParam(refundOrder.getId(), info.isSuccess()));
        return new Result(true);
    }

    private void addRefundMoneyJob(Long ordCode, String serialNum) {
        log.info("退款失败 加入任务处理...");
        // 退款失败回调 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("ordCode", String.valueOf(ordCode));
        jobParams.put("serialNum", serialNum);
        bookingOrderManager.addJob(OrderConstant.Actions.REFUND_MONEY, jobParams, true);
    }

    private Result refundFail(RefundOrder refundOrder) {
        try {
            refundOrderManager.refundFail(refundOrder);
        } catch (Exception e) {
            throw new ApiException("退票失败还原状态异常", e);
        }
        return new Result(true);
    }
}
