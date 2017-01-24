package com.all580.order.manager;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.dto.RefundDay;
import com.all580.order.entity.*;
import com.all580.order.util.AccountUtil;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.all580.voucher.api.model.RefundTicketParams;
import com.all580.voucher.api.model.group.RefundGroupTicketParams;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import com.github.ltsopensource.core.domain.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订
 * @date 2016/10/9 10:32
 */
@Component
@Slf4j
public class RefundOrderManager extends BaseOrderManager {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private RefundAccountMapper refundAccountMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private RefundSerialMapper refundSerialMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private VoucherRPCService voucherRPCService;
    @Autowired
    private ThirdPayService thirdPayService;
    @Autowired
    private SmsManager smsManager;

    /**
     * 取消订单
     * @param sn 订单编号
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result cancel(long sn) {
        return cancel(orderMapper.selectBySN(sn));
    }

    /**
     * 取消订单
     * @param order 订单
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result cancel(Order order) {
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        // 检查订单状态
        switch (order.getStatus()) {
            case OrderConstant.OrderStatus.CANCEL:
                throw new ApiException("重复操作,订单已取消");
            case OrderConstant.OrderStatus.PAID:
                throw new ApiException("订单已支付,请走退订流程");
            case OrderConstant.OrderStatus.PAYING:
                throw new ApiException("支付中,不能取消");
            default:
                order.setStatus(OrderConstant.OrderStatus.CANCEL);
        }
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND ||
                    orderItem.getStatus() == OrderConstant.OrderItemStatus.TICKETING) {
                throw new ApiException("已出票或正在出票中,不能取消");
            }

            orderItem.setStatus(OrderConstant.OrderItemStatus.CANCEL);
            orderItemMapper.updateByPrimaryKeySelective(orderItem);
        }
        // 更新主订单为已取消
        orderMapper.updateByPrimaryKeySelective(order);
        // 还库存
        refundStock(orderItems);
        // 同步数据
        return new Result(true);
    }

    /**
     * 退订审核通过
     * @param orderItem 子订单
     * @param refundOrder 退订订单
     * @param order 订单
     * @return
     * @throws Exception
     */
    public Result auditSuccess(OrderItem orderItem, RefundOrder refundOrder, Order order) throws Exception {
        if (refundOrder.getAudit_time() == null) {
            refundOrder.setAudit_time(new Date());
        }
        if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND) {
            // 调用退票
            refundTicket(refundOrder);
            refundOrderMapper.updateByPrimaryKeySelective(refundOrder);
        } else {
            // 没有出票直接退款
            if (orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                    orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM) {
                nonSendTicketRefundForGroup(refundOrder, orderItem.getGroup_id());
                orderItem.setRefund_quantity(refundOrder.getQuantity());
            } else {
                int quantity = nonSendTicketRefund(refundOrder);
                orderItem.setRefund_quantity(orderItem.getRefund_quantity() + quantity);
            }
            orderItemMapper.updateByPrimaryKeySelective(orderItem);
            // 退款
            refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), refundOrder);
        }

        // 同步数据
        Map syncData = syncRefundOrderAuditAcceptData(refundOrder.getId());
        return new Result<>(true).putExt(Result.SYNC_DATA, syncData);
    }

    /**
     * 判断是否可退 并设置订单详情已退数量 && 创建退订游客信息
     * @param refundDays 退票信息
     * @param details 订单详情
     * @param itemId 子订单ID
     * @param refundId 退订订单ID
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int canRefundForDays(Collection<RefundDay> refundDays, Collection<OrderItemDetail> details, int itemId, int refundId) {
        int totalRefundQuantity = 0;
        List<Visitor> visitorList = visitorMapper.selectByOrderItem(itemId);
        List<RefundVisitor> refundVisitorList = refundVisitorMapper.selectByItemId(itemId);
        List<Visitor> totalVisitorList = new ArrayList<>();
        for (RefundDay refundDay : refundDays) {
            OrderItemDetail detail = AccountUtil.getDetailByDay(details, refundDay.getDay());
            if (detail == null) {
                throw new ApiException(String.format("日期:%s没有订单数据", refundDay.getDay()));
            }
            if (detail.getQuantity() - detail.getUsed_quantity() - detail.getRefund_quantity() < refundDay.getQuantity()) {
                throw new ApiException(
                        String.format("日期:%s余票不足,已用:%s,已退(含预退):%s",
                                new Object[]{JsonUtils.toJson(refundDay.getDay()), detail.getUsed_quantity(), detail.getRefund_quantity()}));
            }
            // 修改退票数
            detail.setRefund_quantity(detail.getRefund_quantity() + refundDay.getQuantity());
            orderItemDetailMapper.updateByPrimaryKeySelective(detail);

            // 把该天的游客张数融合到总张数里面
            AccountUtil.parseTotalVisitorQuantityForEveryDay(totalVisitorList, refundDay.getVisitors());
        }

        // 遍历游客
        for (Visitor visitor : totalVisitorList) {
            Visitor v = getVisitorById(visitorList, visitor.getId());
            if (v == null) {
                throw new ApiException(String.format("游客:%d 不存在", visitor.getId()));
            }
            if (v.getQuantity() < visitor.getQuantity()) {
                throw new ApiException(String.format("游客:%s 票不足", new Object[]{visitor.getName()}));
            }
            if (visitor.getQuantity() <= 0) {
                throw new ApiException(String.format("游客:%s 退票数量必须大于0", visitor.getName()));
            }
            // 判断余票
            RefundVisitor upRefundVisitor = getRefundVisitorById(refundVisitorList, visitor.getId());
            // 有退票信息 && 预退票 + 已退票 + 本次退票 > 游客总票数 则错误
            if (upRefundVisitor != null &&
                    upRefundVisitor.getPre_quantity() + upRefundVisitor.getReturn_quantity() + visitor.getQuantity() > v.getQuantity() ) {
                throw new ApiException(String.format("游客:%s 余票不足.总票数:%d 已退票:%d 已预退票:%d 本次预退票:%d",
                        new Object[]{visitor.getName(),
                                v.getQuantity(), upRefundVisitor.getReturn_quantity(), upRefundVisitor.getPre_quantity(), visitor.getQuantity()}));
            }
            RefundVisitor refundVisitor = new RefundVisitor();
            refundVisitor.setVisitor_id(visitor.getId());
            refundVisitor.setPre_quantity(visitor.getQuantity());
            refundVisitor.setRefund_order_id(refundId);
            refundVisitor.setOrder_item_id(itemId);
            refundVisitorMapper.insertSelective(refundVisitor);
            totalRefundQuantity += visitor.getQuantity();
        }
        return totalRefundQuantity;
    }

    /**
     * 获取游客信息
     * @param visitorList 游客数据
     * @param id 游客ID
     * @return
     */
    private Visitor getVisitorById(List<Visitor> visitorList, Integer id) {
        if (visitorList == null || id == null) {
            return null;
        }
        for (Visitor visitor : visitorList) {
            if (visitor != null && visitor.getId() != null && visitor.getId().intValue() == id) {
                return visitor;
            }
        }
        return null;
    }

    private RefundVisitor getRefundVisitorById(List<RefundVisitor> visitorList, Integer id) {
        if (visitorList == null || id == null) {
            return null;
        }
        for (RefundVisitor visitor : visitorList) {
            if (visitor != null && visitor.getVisitor_id() != null && visitor.getVisitor_id().intValue() == id) {
                return visitor;
            }
        }
        return null;
    }

    /**
     * 计算退支付金额
     * @param orderItem 子订单
     * @param order 订单
     * @param applyFrom 申请来源
     * @param refundDays 退票信息
     * @param details 订单详情
     * @param refundDate 退订时间
     * @return
     */
    public int[] calcRefundMoneyAndFee(OrderItem orderItem, Order order, int applyFrom, Collection<RefundDay> refundDays, Collection<OrderItemDetail> details, Date refundDate) {
        int [] calcResult = new int[]{0, 0};
        // 到付不计算
        if (orderItem.getPayment_flag() != ProductConstants.PayType.PAYS) {
            OrderItemAccount account = orderItemAccountMapper.selectByOrderItemAndEp(orderItem.getId(), order.getBuy_ep_id(), order.getPayee_ep_id());
            if (account == null) {
                throw new ApiException("数据异常,分账记录不存在");
            }
            calcResult = AccountUtil.calcRefundMoneyAndFee(account, applyFrom, refundDays, details, refundDate);
        }
        if (calcResult[0] < 0) {
            throw new ApiException("销售价小于退货手续费");
        }
        return calcResult;
    }

    /**
     * 生成退订订单
     * @param item 子订单
     * @param refundDays 每天退票数
     * @param quantity 总退票数
     * @param money 退支付金额
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public RefundOrder generateRefundOrder(OrderItem item, Collection<RefundDay> refundDays, int quantity, int money, int fee, String cause,
                                           Integer auditTicket, Integer auditMoney, int saleCoreEpId) {
        return generateRefundOrder(item, refundDays, quantity, money, fee, cause, 0, auditTicket, auditMoney, saleCoreEpId);
    }

    /**
     * 生成退订订单
     * @param item 子订单
     * @param refundDays 每天退票数
     * @param quantity 总退票数
     * @param money 退支付金额
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public RefundOrder generateRefundOrder(OrderItem item, Collection<RefundDay> refundDays, int quantity, int money, int fee, String cause,
                                           Integer groupId, Integer auditTicket, Integer auditMoney, int saleCoreEpId) {
        RefundOrder refundOrder = new RefundOrder();
        // 获取平台商通道费率
        refundOrder.setChannel_fee(getChannelRate(item.getSupplier_core_ep_id(), saleCoreEpId));
        refundOrder.setOrder_item_id(item.getId());
        refundOrder.setNumber(UUIDGenerator.generateUUID());
        refundOrder.setQuantity(quantity);
        refundOrder.setData(JsonUtils.toJson(refundDays));
        refundOrder.setStatus(OrderConstant.RefundOrderStatus.AUDIT_WAIT);
        refundOrder.setCreate_time(new Date());
        refundOrder.setMoney(money);
        refundOrder.setFee(fee);
        refundOrder.setCause(cause);
        refundOrder.setGroup_id(groupId == null ? 0 : groupId);
        refundOrder.setAudit_ticket(auditTicket);
        refundOrder.setAudit_money(auditMoney);
        refundOrderMapper.insertSelective(refundOrder);
        return refundOrder;
    }

    /**
     * 退订预分账
     * @param from 申请来源
     * @param refundOrderId 退订订单ID
     * @param order 订单
     * @param refundDate 退订时间
     * @param itemId 子订单ID
     * @param details 订单详情
     * @param refundDays 退订信息
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public List<RefundAccount> preRefundSplitAccount(int from, int refundOrderId, Order order, Date refundDate, int itemId, Collection<OrderItemDetail> details, Collection<RefundDay> refundDays) {
        List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderItem(itemId);
        List<RefundAccount> refundAccounts = AccountUtil.refundSplitAccount(order, accounts, details, refundDays, from, refundOrderId, refundDate);
        for (RefundAccount refundAccount : refundAccounts) {
            refundAccountMapper.insertSelective(refundAccount);
        }
        return refundAccounts;
    }

    /**
     * 退款分账
     * @param refundId 退订订单
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void refundSplitAccount(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        List<RefundAccount> accountList = refundAccountMapper.selectByRefundId(refundOrder.getId());
        if (accountList != null) {
            List<BalanceChangeInfo> infoList = AccountUtil.makerRefundBalanceChangeInfo(accountList);
            for (RefundAccount refundAccount : accountList) {
                refundAccountMapper.updateByPrimaryKeySelective(refundAccount);
            }
            // 调用分账
            Result<BalanceChangeRsp> result = changeBalances(PaymentConstant.BalanceChangeType.REFUND_PAY, String.valueOf(refundOrder.getNumber()), infoList);
            if (!result.isSuccess()) {
                log.warn("退款分账失败:{}", result.get());
                throw new ApiException(result.getError());
            }
        }
    }

    /**
     * 退订失败把之前的预退票信息退回
     * @param refundDays
     * @param detailList
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void returnRefundForDays(Collection<RefundDay> refundDays, List<OrderItemDetail> detailList) throws Exception {
        for (RefundDay refundDay : refundDays) {
            OrderItemDetail detail = AccountUtil.getDetailByDay(detailList, refundDay.getDay());
            if (detail == null) {
                throw new ApiException(String.format("日期:%s没有订单数据,数据异常", refundDay.getDay()));
            }
            // 修改退票数
            detail.setRefund_quantity(detail.getRefund_quantity() - refundDay.getQuantity());
            orderItemDetailMapper.updateByPrimaryKeySelective(detail);
        }
    }

    /**
     * 已支付未出票的订单退订 更新游客退票数据
     * @param refundOrder
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int nonSendTicketRefund(RefundOrder refundOrder) throws Exception {
        int total = 0;
        List<RefundVisitor> refundVisitorList = refundVisitorMapper.selectByRefundId(refundOrder.getId());
        List<Visitor> visitorList = visitorMapper.selectByOrderItem(refundOrder.getOrder_item_id());
        for (RefundVisitor refundVisitor : refundVisitorList) {
            total += refundVisitor.getPre_quantity();
            refundVisitor.setReturn_quantity(refundVisitor.getPre_quantity());
            refundVisitor.setPre_quantity(0);
            refundVisitorMapper.updateByPrimaryKeySelective(refundVisitor);
            Visitor visitor = getVisitorById(visitorList, refundVisitor.getVisitor_id());
            visitor.setReturn_quantity(visitor.getReturn_quantity() + refundVisitor.getReturn_quantity());
            visitorMapper.updateByPrimaryKeySelective(visitor);
        }
        return total;
    }

    /**
     * 已支付未出票的订单退订 更新游客退票数据
     * @param refundOrder
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void nonSendTicketRefundForGroup(RefundOrder refundOrder, int groupId) throws Exception {
        List<Visitor> visitorList = visitorMapper.selectByOrderItem(refundOrder.getOrder_item_id());
        for (Visitor visitor : visitorList) {
            if (visitor.getGroup_id() == groupId) {
                visitor.setReturn_quantity(visitor.getQuantity() - visitor.getUse_quantity());
                visitorMapper.updateByPrimaryKeySelective(visitor);
            } else {
                log.warn("团队退订订单号:{} 游客信息的团队号:{}不属于本团队:{}", new Object[]{refundOrder.getNumber(), visitor.getGroup_id(), groupId});
            }
        }
    }

    /**
     * 退票失败
     * @param refundOrder
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void refundFail(RefundOrder refundOrder) throws Exception {
        refundOrder.setStatus(OrderConstant.RefundOrderStatus.FAIL);
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(refundOrder.getOrder_item_id());
        Collection<RefundDay> refundDays = AccountUtil.decompileRefundDay(refundOrder.getData());
        returnRefundForDays(refundDays, detailList);
        refundOrderMapper.updateByPrimaryKeySelective(refundOrder);
    }

    /**
     * 退票
     * @param refundOrder
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void refundTicket(RefundOrder refundOrder) {
        // 生成退票流水
        RefundSerial refundSerial = new RefundSerial();
        refundSerial.setLocal_serial_no(UUIDGenerator.generateUUID());
        refundSerial.setQuantity(refundOrder.getQuantity());
        refundSerial.setData(refundOrder.getData());
        refundSerial.setRefund_order_id(refundOrder.getId());
        refundSerial.setCreate_time(new Date());
        refundSerialMapper.insertSelective(refundSerial);

        refundOrder.setStatus(OrderConstant.RefundOrderStatus.REFUNDING);
        refundOrder.setLocal_refund_serial_no(refundSerial.getLocal_serial_no());

        int i = 0;
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
        if (orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM) {
            RefundGroupTicketParams ticketParams = new RefundGroupTicketParams();
            ticketParams.setOrderSn(orderItem.getNumber());
            ticketParams.setRefundSn(String.valueOf(refundSerial.getLocal_serial_no()));
            ticketParams.setQuantity(refundOrder.getQuantity());
            ticketParams.setReason(refundOrder.getCause());
            ticketParams.setApplyTime(refundOrder.getAudit_time());
            voucherRPCService.refundGroupTicket(orderItem.getEp_ma_id(), ticketParams);
        } else {
            List<RefundVisitor> refundVisitorList = refundVisitorMapper.selectByRefundId(refundOrder.getId());
            for (RefundVisitor refundVisitor : refundVisitorList) {
                RefundTicketParams ticketParams = new RefundTicketParams();
                ticketParams.setApplyTime(refundOrder.getAudit_time());
                ticketParams.setOrderSn(orderItem.getNumber());
                ticketParams.setQuantity(refundVisitor.getPre_quantity());
                ticketParams.setVisitorId(refundVisitor.getVisitor_id());
                ticketParams.setRefundSn(String.valueOf(refundSerial.getLocal_serial_no()));
                ticketParams.setReason(refundOrder.getCause());
                try {
                    // TODO: 2016/11/3 讲道理这里要一起提交或者只能一个人退票
                    Result result = voucherRPCService.refundTicket(orderItem.getEp_ma_id(), ticketParams);
                    if (!result.isSuccess()) {
                        throw new ApiException(result.getError());
                    }
                    i++;
                } catch (Exception e) {
                    log.warn("退票请求部分失败", e);
                }
            }
            if (i != refundVisitorList.size()) {
                log.warn("*****退票发起部分成功*****");
            }
        }
    }

    /**
     * 退款 判断是否审核
     * @param order
     * @param money
     * @param sn
     * @param refundOrder
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result refundMoney(Order order, int money, String sn, RefundOrder refundOrder) {
        // 需要审核
        if (refundOrder.getAudit_money() == ProductConstants.RefundMoneyAudit.YES &&
                refundOrder.getStatus() != OrderConstant.RefundOrderStatus.REFUND_MONEY_AUDITING) {
            refundOrder.setStatus(OrderConstant.RefundOrderStatus.REFUND_MONEY_AUDITING);
            refundOrderMapper.updateByPrimaryKeySelective(refundOrder);
            return new Result(true);
        }
        // 不需要审核
        refundOrder.setStatus(OrderConstant.RefundOrderStatus.REFUND_MONEY);
        refundOrderMapper.updateByPrimaryKeySelective(refundOrder);
        // 非支付宝
        if (order.getPayment_type() == null || order.getPayment_type() != PaymentConstant.PaymentType.ALI_PAY.intValue()) {
            // 退款
            return refundMoney(order, money, sn, refundOrder.getId());
        }
        return new Result(true);
    }

    /**
     * 退款
     * @param order 订单
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result refundMoney(Order order, int money, String sn, int refundOrderId) {
        log.debug("订单:{} 发起退款:{}", order.getNumber(), money);
        if (money == 0 && sn != null) {
            log.debug("订单:{} 退款为0元,直接调用退款成功.", order.getNumber());
            refundMoneyAfter(Long.valueOf(sn), true);
            return new Result(true);
        }
        Integer coreEpId = getCoreEpId(getCoreEpId(order.getBuy_ep_id()));
        // 退款
        // 余额退款
        if (order.getPayment_type() == PaymentConstant.PaymentType.BALANCE.intValue()) {
            List<RefundAccount> accountList = refundAccountMapper.selectByRefundId(refundOrderId);
            // 获取余额变动信息
            List<BalanceChangeInfo> balanceChangeInfoList = AccountUtil.makerRefundBalanceChangeInfo(accountList);
            // 退款
            Result result = changeBalances(
                    PaymentConstant.BalanceChangeType.BALANCE_REFUND,
                    sn == null ? order.getNumber().toString() : sn, balanceChangeInfoList);
            if (!result.isSuccess()) {
                log.warn("余额退款失败:{}", result.get());
                throw new ApiException(result.getError());
            }
            return new Result(true);
        }
        // 第三方退款
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("totalFee", order.getPay_amount());
        payParams.put("refundFee", money);
        payParams.put("serialNum", sn == null ? order.getNumber().toString() : sn);
        payParams.put("outTransId", order.getThird_serial_no());
        Result result = thirdPayService.reqRefund(order.getNumber(), coreEpId, order.getPayment_type(), payParams);
        if (!result.isSuccess()) {
            log.warn("第三方退款异常:{}", result);
            throw new ApiException(result.getError());
        }
        return result;
    }

    /**
     * 还可售库存
     * @param orderItemList
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void refundStock(List<OrderItem> orderItemList) {
        if (orderItemList != null) {
            List<ProductSearchParams> lockParams = new ArrayList<>();
            for (OrderItem orderItem : orderItemList) {
                List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
                for (OrderItemDetail detail : detailList) {
                    if (!detail.getOversell()) {
                        ProductSearchParams p = new ProductSearchParams();
                        p.setSubProductCode(orderItem.getPro_sub_number());
                        p.setStartDate(detail.getDay());
                        p.setDays(1);
                        p.setQuantity(detail.getQuantity());
                        p.setSubOrderId(orderItem.getId());
                        lockParams.add(p);
                    }
                }
            }
            // 还库存
            if (!lockParams.isEmpty()) {
                com.framework.common.Result result = productSalesPlanRPCService.addProductStocks(lockParams);
                if (!result.isSuccess()) {
                    throw new ApiException(result.getError());
                }
            }
        }
    }

    /**
     * 还可售库存
     * @param orderItem
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void refundStock(OrderItem orderItem) {
        refundStock(CommonUtil.oneToList(orderItem));
    }

    /**
     * 退款后操作
     * @param sn 流水
     * @param success 退款成功与否
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void refundMoneyAfter(long sn, boolean success) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(sn);

        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        if (refundOrder == null) {
            throw new ApiException("订单不存在");
        }
        refundOrder.setRefund_money_time(new Date());
        refundOrder.setStatus(success ? OrderConstant.RefundOrderStatus.REFUND_SUCCESS : OrderConstant.RefundOrderStatus.REFUND_MONEY_FAIL);
        refundOrderMapper.updateByPrimaryKeySelective(refundOrder);

        if (success) {
            // 发送短信 退款
            if (refundOrder.getMoney() > 0) {
                smsManager.sendRefundMoneySuccessSms(refundOrder);
            }
            // 发送短信 退订
            smsManager.sendRefundSuccessSms(refundOrder);
            // 还库存 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderItemId", String.valueOf(refundOrder.getOrder_item_id()));
            jobParams.put("check", "true");
            Job stockJob = createJob(OrderConstant.Actions.REFUND_STOCK, jobParams, false);

            // 退款分账 记录任务 余额不做后续分账(和支付的时候一起)
            if (order.getPayment_type() != null && order.getPayment_type() != PaymentConstant.PaymentType.BALANCE.intValue()) {
                Map<String, String> moneyJobParams = new HashMap<>();
                moneyJobParams.put("refundId", String.valueOf(refundOrder.getId()));
                Job moneyJob = createJob(OrderConstant.Actions.REFUND_MONEY_SPLIT_ACCOUNT, moneyJobParams, false);
                addJobs(stockJob, moneyJob);
            } else {
                addJobs(stockJob);
            }
        }
        // 同步数据
        syncRefundOrderMoney(refundOrder.getId());
    }

    /**
     * 检查申请退订权限
     * @param orderItem 子订单
     * @param order 订单
     * @param applyFrom 供应侧/销售侧
     * @param epId 企业ID
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void checkApplyRefund(OrderItem orderItem, Order order, Integer applyFrom, int epId) {
        if (applyFrom == ProductConstants.RefundEqType.SELLER) {
            if (epId != order.getBuy_ep_id() && epId != order.getPayee_ep_id()) {
                throw new ApiException("非法请求:当前企业不能退订该订单");
            }
        } else {
            if (epId != orderItem.getSupplier_ep_id() && epId != order.getPayee_ep_id()) {
                throw new ApiException("非法请求:当前企业不能退订该订单");
            }
        }
    }

    /**
     * 同步订单取消数据
     * @param orderId 订单ID
     */
    public Map syncOrderCancelData(int orderId) {
        return generateSyncByOrder(orderId)
                .put("t_order", CommonUtil.oneToList(orderMapper.selectByPrimaryKey(orderId)))
                .put("t_order_item", orderItemMapper.selectByOrderId(orderId))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步退订订单申请数据
     * @param refundId 退订订单ID
     */
    public Map syncRefundOrderApplyData(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        return generateSyncByItem(refundOrder.getOrder_item_id())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .put("t_order_item_detail", orderItemDetailMapper.selectByItemId(refundOrder.getOrder_item_id()))
                .put("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundId))
                .put("t_refund_account", refundAccountMapper.selectByRefundId(refundId))
                .sync().getDataMapForJsonMap();
    }
    /**
     * 同步退款分账数据
     * @param refundId 退订订单ID
     */
    public Map syncRefundAccountData(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        return generateSyncByItem(refundOrder.getOrder_item_id())
                .put("t_refund_account", refundAccountMapper.selectByRefundId(refundId))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步退订审核通过数据
     * @param refundId 退订订单ID
     */
    public Map syncRefundOrderAuditAcceptData(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        return generateSyncByItem(refundOrder.getOrder_item_id())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .put("t_refund_serial", CommonUtil.oneToList(refundSerialMapper.selectByRefundOrder(refundOrder.getId())))
                .put("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundId))
                .put("t_visitor", visitorMapper.selectByOrderItem(refundOrder.getOrder_item_id()))
                .put("t_order_item", CommonUtil.oneToList(orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id())))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步退订订单审核拒绝数据
     * @param refundId 退订订单ID
     */
    public Map syncRefundOrderAuditRefuse(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        return generateSyncByItem(refundOrder.getOrder_item_id())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .put("t_order_item_detail", orderItemDetailMapper.selectByItemId(refundOrder.getOrder_item_id()))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步退订订单退款数据
     * @param refundId 退订订单ID
     */
    public Map syncRefundOrderMoney(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        return generateSyncByItem(refundOrder.getOrder_item_id())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步退票数据
     * @param refundId 退订订单ID
     */
    public Map syncRefundTicketData(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        return generateSyncByItem(refundOrder.getOrder_item_id())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .put("t_order_item", CommonUtil.oneToList(orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id())))
                .put("t_refund_serial", CommonUtil.oneToList(refundSerialMapper.selectByRefundOrder(refundOrder.getId())))
                .put("t_visitor", visitorMapper.selectByOrderItem(refundOrder.getOrder_item_id()))
                .put("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundId))
                .sync().getDataMapForJsonMap();
    }
}
