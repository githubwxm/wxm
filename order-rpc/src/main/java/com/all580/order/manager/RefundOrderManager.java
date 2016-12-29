package com.all580.order.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.all580.ep.api.service.CoreEpChannelService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.entity.*;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.consts.ProductRules;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.all580.voucher.api.model.RefundTicketParams;
import com.all580.voucher.api.model.group.RefundGroupTicketParams;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.Result;
import com.framework.common.lang.Arith;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import com.github.ltsopensource.core.domain.Job;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
        Map syncData = syncOrderCancelData(order.getId());
        return new Result(true).putExt(Result.SYNC_DATA, syncData);
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
     * 判断是否可退 并更新退票数
     * @param daysList 每天数据
     * @param detailList 订单详情
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int canRefundForDays(List daysList, List<OrderItemDetail> detailList, int itemId, int refundId) throws Exception {
        int total = 0;
        List<Visitor> visitorList = visitorMapper.selectByOrderItem(itemId);
        List<RefundVisitor> refundVisitorList = refundVisitorMapper.selectByItemId(itemId);
        List<Map> visitors = new ArrayList<>();
        for (Object item : daysList) {
            Map dayMap = (Map) item;
            String day = CommonUtil.objectParseString(dayMap.get("day"));
            Date date = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, day);
            OrderItemDetail detail = getDetailByDay(detailList, date);
            if (detail == null) {
                throw new ApiException(String.format("日期:%s没有订单数据", day));
            }
            Integer quantity = CommonUtil.objectParseInteger(dayMap.get("quantity"));
            if (detail.getQuantity() - detail.getUsed_quantity() - detail.getRefund_quantity() < quantity) {
                throw new ApiException(
                        String.format("日期:%s余票不足,已用:%s,已退:%s",
                                new Object[]{day, detail.getUsed_quantity(), detail.getRefund_quantity()}));
            }
            total += quantity;
            // 修改退票数
            detail.setRefund_quantity(detail.getRefund_quantity() + quantity);
            orderItemDetailMapper.updateByPrimaryKeySelective(detail);

            int tmpVisitorQuantity = 0;
            for (Object o : ((List) dayMap.get("visitors"))) {
                Map vMap = (Map) o;
                Integer id = CommonUtil.objectParseInteger(vMap.get("id"));
                Integer vQuantity = CommonUtil.objectParseInteger(vMap.get("quantity"));
                boolean have = false;
                for (Map visitor : visitors) {
                    if (visitor.get("id").equals(id)) {
                        Integer tmp = CommonUtil.objectParseInteger(visitor.get("quantity"));
                        visitor.put("quantity", tmp + vQuantity);
                        have = true;
                        break;
                    }
                }
                if (!have) {
                    visitors.add(vMap);
                }
                tmpVisitorQuantity += vQuantity;
            }
            if (tmpVisitorQuantity != quantity) {
                throw new ApiException(String.format("日期:%s游客退票数不符",
                        new Object[]{day}));
            }
        }
        // 判断游客余票
        int v = canVisitorRefund(visitors, visitorList, refundVisitorList, itemId, refundId);
        if (v != total) {
            throw new ApiException("总退票数与游客每日退票数不符");
        }
        return total;
    }

    /**
     * 判断游客余票退票 并插入游客退票信息
     * @param visitors 退票申请游客信息
     * @param visitorList 订单游客信息
     * @param refundVisitorList 订单之前游客退票信息
     * @param itemId 订单ID
     * @param refundId 退票订单ID
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int canVisitorRefund(List<Map> visitors, List<Visitor> visitorList, List<RefundVisitor> refundVisitorList, int itemId, int refundId) {
        int total = 0;
        for (Map vMap : visitors) {
            Integer id = CommonUtil.objectParseInteger(vMap.get("id"));
            Integer vQuantity = CommonUtil.objectParseInteger(vMap.get("quantity"));
            Visitor visitor = getVisitorById(visitorList, id);
            if (visitor == null) {
                throw new ApiException(String.format("游客:%d 不存在", id));
            }
            if (visitor.getQuantity() < vQuantity) {
                throw new ApiException(String.format("游客:%s 票不足", new Object[]{visitor.getName()}));
            }
            // 判断余票
            RefundVisitor upRefundVisitor = getRefundVisitorById(refundVisitorList, id);
            if (upRefundVisitor != null &&
                    upRefundVisitor.getPre_quantity() + upRefundVisitor.getReturn_quantity() + vQuantity > visitor.getQuantity() ) {
                throw new ApiException(String.format("游客:%d 余票不足.总票数:%d 已退票:%d 已预退票:%d 本次预退票:%d",
                        new Object[]{visitor.getName(),
                                visitor.getQuantity(), upRefundVisitor.getReturn_quantity(), upRefundVisitor.getPre_quantity(), vQuantity}));
            }
            RefundVisitor refundVisitor = new RefundVisitor();
            refundVisitor.setVisitor_id(id);
            refundVisitor.setPre_quantity(vQuantity);
            refundVisitor.setRefund_order_id(refundId);
            refundVisitor.setOrder_item_id(itemId);
            refundVisitorMapper.insertSelective(refundVisitor);
            total += vQuantity;
        }
        return total;
    }

    /**
     * 获取游客信息
     * @param visitorList 游客数据
     * @param id 游客ID
     * @return
     */
    public Visitor getVisitorById(List<Visitor> visitorList, Integer id) {
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

    public RefundVisitor getRefundVisitorById(List<RefundVisitor> visitorList, Integer id) {
        if (visitorList == null || id == null) {
            return null;
        }
        for (RefundVisitor visitor : visitorList) {
            if (visitor != null && visitor.getId() != null && visitor.getId().intValue() == id) {
                return visitor;
            }
        }
        return null;
    }

    /**
     * 计算退支付金额
     * @param from 供应侧/销售侧
     * @param daysList 每日退票详情
     * @param detailList 每日订单详情
     * @param itemId 子订单ID
     * @param epId 支付企业ID
     * @param coreEpId 支付企业余额托管平台商ID
     * @return 0:退支付金额 1:手续费
     */
    public int[] calcRefundMoney(int from, List daysList, List<OrderItemDetail> detailList, int itemId, int epId, int coreEpId, Date refundDate) throws Exception {
        OrderItemAccount account = orderItemAccountMapper.selectByOrderItemAndEp(itemId, epId, coreEpId);
        if (account == null) {
            throw new ApiException("数据异常,分账记录不存在");
        }
        String data = account.getData();
        if (StringUtils.isEmpty(data)) {
            throw new ApiException("数据异常,分账记录的每日单价不存在");
        }
        JSONArray daysData = JSONArray.parseArray(data);
        if (daysData.size() != detailList.size()) {
            throw new ApiException("数据异常,分账记录的每日单价与订单详情不匹配");
        }
        int money = 0;
        int fee = 0;
        for (Object item : daysList) {
            Map dayMap = (Map) item;
            String day = CommonUtil.objectParseString(dayMap.get("day"));
            Date date = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, day);
            OrderItemDetail detail = getDetailByDay(detailList, date);
            if (detail == null) {
                throw new ApiException(String.format("日期:%s没有订单数据", day));
            }
            JSONObject dayData = getAccountDataByDay(daysData, day);
            if (dayData == null) {
                throw new ApiException(String.format("日期:%s没有利润数据", day));
            }
            int outPrice = dayData.getIntValue("outPrice");
            Integer quantity = CommonUtil.objectParseInteger(dayMap.get("quantity"));
            Map<String, Integer> rate = ProductRules.calcRefund(from == ProductConstants.RefundEqType.SELLER ? detail.getCust_refund_rule() : detail.getSaler_refund_rule(), detail.getDay(), refundDate);
            float feeTmp = 0f;
            if (rate.get("type") == ProductConstants.AddPriceType.FIX) {
                feeTmp = rate.get("fixed") * quantity;
            } else {
                feeTmp = outPrice * quantity * (float)rate.get("percent") / 100;
            }
            fee += feeTmp;
            money += outPrice * quantity - feeTmp;
            dayMap.put("fee", (int)feeTmp);
        }
        return new int[]{money, fee};
    }

    /**
     * 计算退支付金额
     * @param from 供应侧/销售侧
     * @param itemId 子订单ID
     * @param quantity 退款钱的票
     * @param epId 支付企业ID
     * @param coreEpId 支付企业余额托管平台商ID
     * @param refundDate 退订时间
     * @param detailList 每日退票详情
     * @return
     */
    public int[] calcRefundMoneyForGroup(int from, int itemId, int quantity, int epId, int coreEpId, Date refundDate, List<OrderItemDetail> detailList) {
        OrderItemAccount account = orderItemAccountMapper.selectByOrderItemAndEp(itemId, epId, coreEpId);
        if (account == null) {
            throw new ApiException("数据异常,分账记录不存在");
        }
        String data = account.getData();
        if (StringUtils.isEmpty(data)) {
            throw new ApiException("数据异常,分账记录的每日单价不存在");
        }
        JSONArray daysData = JSONArray.parseArray(data);
        if (daysData.size() != detailList.size()) {
            throw new ApiException("数据异常,分账记录的每日单价与订单详情不匹配");
        }
        int money = 0;
        int fee = 0;
        for (OrderItemDetail detail : detailList) {
            String day = DateFormatUtils.parseDateToDatetimeString(detail.getDay());
            JSONObject dayData = getAccountDataByDay(daysData, day);
            if (dayData == null) {
                throw new ApiException(String.format("日期:%s没有利润数据", day));
            }
            int outPrice = dayData.getIntValue("outPrice");
            Map<String, Integer> rate = ProductRules.calcRefund(from == ProductConstants.RefundEqType.SELLER ? detail.getCust_refund_rule() : detail.getSaler_refund_rule(), detail.getDay(), refundDate);
            float feeTmp = 0f;
            if (rate.get("type") == ProductConstants.AddPriceType.FIX) {
                feeTmp = rate.get("fixed") * quantity;
            } else {
                feeTmp = outPrice * quantity * (float)rate.get("percent") / 100;
            }
            fee += feeTmp;
            money += outPrice * quantity - feeTmp;
        }
        return new int[]{money, fee};
    }

    /**
     * 生成退订订单
     * @param item 子订单
     * @param daysList 每天退票数
     * @param quantity 总退票数
     * @param money 退支付金额
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public RefundOrder generateRefundOrder(OrderItem item, List daysList, int quantity, int money, int fee, String cause,
                                           Integer auditTicket, Integer auditMoney, int saleCoreEpId) {
        return generateRefundOrder(item, daysList, quantity, money, fee, cause, 0, auditTicket, auditMoney, saleCoreEpId);
    }

    /**
     * 生成退订订单
     * @param item 子订单
     * @param daysList 每天退票数
     * @param quantity 总退票数
     * @param money 退支付金额
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public RefundOrder generateRefundOrder(OrderItem item, List daysList, int quantity, int money, int fee, String cause,
                                           Integer groupId, Integer auditTicket, Integer auditMoney, int saleCoreEpId) {
        RefundOrder refundOrder = new RefundOrder();
        // 获取平台商通道费率
        refundOrder.setChannel_fee(getChannelRate(item.getSupplier_core_ep_id(), saleCoreEpId));
        refundOrder.setOrder_item_id(item.getId());
        refundOrder.setNumber(UUIDGenerator.generateUUID());
        refundOrder.setQuantity(quantity);
        refundOrder.setData(JsonUtils.toJson(daysList));
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
     * 退货预分账
     * @param orderItem 子订单
     * @param refundOrderId 退订订单ID
     * @param detailList 每天详情
     * @param refundDate 退订时间
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void preRefundAccount(List daysList, int from, OrderItem orderItem, int refundOrderId, List<OrderItemDetail> detailList, Date refundDate, Order order) throws Exception {
        List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderItem(orderItem.getId());
        JSONArray priceDaysData = checkGetPriceDayData(order, accounts);
        if (priceDaysData == null) {
            throw new ApiException("分账单价数据异常");
        }
        Set<String> uk = new HashSet<>();
        for (OrderItemAccount account : accounts) {
            String data = account.getData();
            if (StringUtils.isEmpty(data)) {
                continue;
            }
            String key = account.getEp_id() + "#" + account.getCore_ep_id();
            if (uk.contains(key)) {
                continue;
            }
            uk.add(key);
            JSONArray daysData = JSONArray.parseArray(data);
            int money = 0;
            int cash = 0;
            for (Object item : daysList) {
                Map dayMap = (Map) item;
                String day = CommonUtil.objectParseString(dayMap.get("day"));
                Date date = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, day);
                int quantity = CommonUtil.objectParseInteger(dayMap.get("quantity"));
                OrderItemDetail detail = getDetailByDay(detailList, date);
                if (detail == null) {
                    throw new ApiException(String.format("日期:%s没有订单数据,数据异常", day));
                }
                JSONObject dayData = getAccountDataByDay(daysData, day);
                if (dayData == null) {
                    throw new ApiException(String.format("日期:%s没有利润数据,数据异常", day));
                }
                Map<String, Integer> rate = ProductRules.calcRefund(from == ProductConstants.RefundEqType.SELLER ? detail.getCust_refund_rule() : detail.getSaler_refund_rule(), detail.getDay(), refundDate);
                // 利润
                int profit = dayData.getIntValue("profit");
                double percent = getPercent(priceDaysData, day, rate);
                // 退余额(出货价*1-手续费)
                money += Arith.round(Arith.mul(profit * quantity, 1 - percent), 0);
                // 进可提现(出货价*手续费)
                cash += Arith.round(Arith.mul(profit * quantity, percent), 0);
            }
            // 自供平台商并且是收款商则分账不退钱,因为退票的时候已经退了钱
            if (account.getEp_id().intValue() == account.getCore_ep_id() &&
                    account.getCore_ep_id().intValue() == order.getPayee_ep_id() &&
                    orderItem.getSupplier_core_ep_id().intValue() == order.getPayee_ep_id()) {
                money = 0;
            }
            RefundAccount refundAccount = new RefundAccount();
            refundAccount.setEp_id(account.getEp_id());
            refundAccount.setCore_ep_id(account.getCore_ep_id());
            refundAccount.setMoney(money == 0 ? 0 : -money);
            refundAccount.setProfit(cash == 0 ? 0 : cash);
            refundAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
            refundAccount.setRefund_order_id(refundOrderId);
            refundAccount.setData(data);
            refundAccountMapper.insertSelective(refundAccount);
        }
    }

    /**
     * 退货预分账
     * @param from
     * @param item 子订单
     * @param refundOrderId 退订订单ID
     * @param detailList 每天详情
     * @param refundDate 退订时间
     * @param order
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void preRefundAccountForGroup(int from, OrderItem item, int refundOrderId, List<OrderItemDetail> detailList, Date refundDate, Order order) throws Exception {
        List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderItem(item.getId());
        JSONArray priceDaysData = checkGetPriceDayData(order, accounts);
        if (priceDaysData == null) {
            throw new ApiException("分账单价数据异常");
        }
        Set<String> uk = new HashSet<>();
        for (OrderItemAccount account : accounts) {
            String data = account.getData();
            if (StringUtils.isEmpty(data)) {
                continue;
            }
            String key = account.getEp_id() + "#" + account.getCore_ep_id();
            if (uk.contains(key)) {
                continue;
            }
            uk.add(key);
            JSONArray daysData = JSONArray.parseArray(data);
            int money = 0;
            int cash = 0;
            for (OrderItemDetail detail : detailList) {
                String day = DateFormatUtils.parseDateToDatetimeString(detail.getDay());
                JSONObject dayData = getAccountDataByDay(daysData, day);
                if (dayData == null) {
                    throw new ApiException(String.format("日期:%s没有利润数据,数据异常", day));
                }
                int quantity = detail.getQuantity() - detail.getUsed_quantity();
                Map<String, Integer> rate = ProductRules.calcRefund(from == ProductConstants.RefundEqType.SELLER ? detail.getCust_refund_rule() : detail.getSaler_refund_rule(), detail.getDay(), refundDate);
                // 利润
                int profit = dayData.getIntValue("profit");
                double percent = getPercent(priceDaysData, day, rate);
                // 退余额(出货价*1-手续费)
                money += Arith.round(Arith.mul(profit * quantity, 1 - percent), 0);
                // 进可提现(出货价*手续费)
                cash += Arith.round(Arith.mul(profit * quantity, percent), 0);
            }
            // 自供平台商并且是收款商则分账不退钱,因为退票的时候已经退了钱
            if (account.getEp_id().intValue() == account.getCore_ep_id() &&
                    account.getCore_ep_id().intValue() == order.getPayee_ep_id() &&
                    item.getSupplier_core_ep_id().intValue() == order.getPayee_ep_id()) {
                money = 0;
            }
            RefundAccount refundAccount = new RefundAccount();
            refundAccount.setEp_id(account.getEp_id());
            refundAccount.setCore_ep_id(account.getCore_ep_id());
            refundAccount.setMoney(money == 0 ? 0 : -money);
            refundAccount.setProfit(cash == 0 ? 0 : cash);
            refundAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
            refundAccount.setRefund_order_id(refundOrderId);
            refundAccount.setData(data);
            refundAccountMapper.insertSelective(refundAccount);
        }
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
            List<BalanceChangeInfo> infoList = new ArrayList<>();
            for (RefundAccount refundAccount : accountList) {
                BalanceChangeInfo info = new BalanceChangeInfo();
                info.setEp_id(refundAccount.getEp_id());
                info.setCore_ep_id(refundAccount.getCore_ep_id());
                info.setBalance(refundAccount.getMoney());
                info.setCan_cash(refundAccount.getProfit());
                infoList.add(info);
                refundAccount.setStatus(OrderConstant.AccountSplitStatus.HAS);
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
     * @param daysList
     * @param detailList
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void returnRefundForDays(List daysList, List<OrderItemDetail> detailList) throws Exception {
        for (Object item : daysList) {
            Map dayMap = (Map) item;
            String day = CommonUtil.objectParseString(dayMap.get("day"));
            Date date = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, day);
            OrderItemDetail detail = getDetailByDay(detailList, date);
            if (detail == null) {
                throw new ApiException(String.format("日期:%s没有订单数据,数据异常", day));
            }
            Integer quantity = CommonUtil.objectParseInteger(dayMap.get("quantity"));
            // 修改退票数
            detail.setRefund_quantity(detail.getRefund_quantity() - quantity);
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
        List daysList = JsonUtils.json2List(refundOrder.getData());
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(refundOrder.getOrder_item_id());
        returnRefundForDays(daysList, detailList);
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
            return refundMoney(order, money, sn);
        }
        return new Result(true);
    }

    /**
     * 退款
     * @param order 订单
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result refundMoney(Order order, int money, String sn) {
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
            BalanceChangeInfo payInfo = new BalanceChangeInfo();
            payInfo.setEp_id(coreEpId);
            payInfo.setCore_ep_id(coreEpId);
            payInfo.setBalance(-money);

            BalanceChangeInfo saveInfo = new BalanceChangeInfo();
            saveInfo.setEp_id(order.getBuy_ep_id());
            saveInfo.setCore_ep_id(payInfo.getCore_ep_id());
            saveInfo.setBalance(money);
            saveInfo.setCan_cash(money);
            // 退款
            Result result = changeBalances(
                    PaymentConstant.BalanceChangeType.BALANCE_REFUND,
                    sn == null ? order.getNumber().toString() : sn, payInfo, saveInfo);
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
                        p.setSubProductId(orderItem.getPro_sub_id());
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

            // 退款分账 记录任务
            Map<String, String> moneyJobParams = new HashMap<>();
            moneyJobParams.put("refundId", String.valueOf(refundOrder.getId()));
            Job moneyJob = createJob(OrderConstant.Actions.REFUND_MONEY_SPLIT_ACCOUNT, moneyJobParams, false);

            addJobs(stockJob, moneyJob);
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
     * 检查分账数据并返回单价分账数据
     * @param order 订单
     * @param accounts 分账数据
     * @return
     */
    private JSONArray checkGetPriceDayData(Order order, List<OrderItemAccount> accounts) {
        for (OrderItemAccount account : accounts) {
            if (account.getEp_id().intValue() == order.getBuy_ep_id() && account.getCore_ep_id().intValue() == order.getPayee_ep_id()) {
                String data = account.getData();
                if (StringUtils.isEmpty(data)) {
                    throw new ApiException("分账数据异常");
                }
                return JSONArray.parseArray(data);
            }
        }
        return null;
    }

    /**
     * 获取利润百分比
     * @param priceDaysData
     * @param day
     * @param rate
     * @return
     */
    private double getPercent(JSONArray priceDaysData, String day, Map<String, Integer> rate) {
        double percent = 1.0;
        if (rate.get("type") == ProductConstants.AddPriceType.FIX) {
            JSONObject priceDayData = getAccountDataByDay(priceDaysData, day);
            if (priceDayData == null) {
                throw new ApiException("日期:"+day+"分账单价数据异常");
            }
            percent = Arith.div(rate.get("fixed"), priceDayData.getIntValue("outPrice"), 4);
        } else {
            percent = Arith.div(rate.get("percent"), 100, 4);
        }
        return percent;
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
