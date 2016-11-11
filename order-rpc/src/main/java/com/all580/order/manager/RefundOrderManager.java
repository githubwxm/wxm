package com.all580.order.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.all580.notice.api.conf.SmsType;
import com.all580.notice.api.service.SmsService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.entity.*;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.consts.ProductRules;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.all580.voucher.api.model.RefundTicketParams;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
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
        syncOrderCancelData(order.getId());
        return new Result(true);
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
            if (detail.getQuantity() - detail.getUsedQuantity() - detail.getRefundQuantity() < quantity) {
                throw new ApiException(
                        String.format("日期:%s余票不足,已用:%s,已退:%s",
                                new Object[]{day, detail.getUsedQuantity(), detail.getRefundQuantity()}));
            }
            total += quantity;
            // 修改退票数
            detail.setRefundQuantity(detail.getRefundQuantity() + quantity);
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
                    upRefundVisitor.getPreQuantity() + upRefundVisitor.getReturnQuantity() + vQuantity > visitor.getQuantity() ) {
                throw new ApiException(String.format("游客:%d 余票不足.总票数:%d 已退票:%d 已预退票:%d 本次预退票:%d",
                        new Object[]{visitor.getName(),
                                visitor.getQuantity(), upRefundVisitor.getReturnQuantity(), upRefundVisitor.getPreQuantity(), vQuantity}));
            }
            RefundVisitor refundVisitor = new RefundVisitor();
            refundVisitor.setVisitorId(id);
            refundVisitor.setPreQuantity(vQuantity);
            refundVisitor.setRefundOrderId(refundId);
            refundVisitor.setOrderItemId(itemId);
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
            if (visitor.getId().intValue() == id) {
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
            if (visitor.getId().intValue() == id) {
                return visitor;
            }
        }
        return null;
    }

    /**
     * 计算退支付金额
     * @param daysList 每日退票详情
     * @param detailList 每日订单详情
     * @param itemId 子订单ID
     * @param epId 支付企业ID
     * @param coreEpId 支付企业余额托管平台商ID
     * @return
     */
    public int calcRefundMoney(List daysList, List<OrderItemDetail> detailList, int itemId, int epId, int coreEpId, Date refundDate) throws Exception {
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
            Map<String, Integer> rate = ProductRules.calcRefund(detail.getCustRefundRule(), detail.getDay(), refundDate);
            if (rate.get("type") == ProductConstants.AddPriceType.FIX) {
                money += (outPrice * quantity) - rate.get("fixed") * quantity;
            } else {
                money += outPrice - outPrice * quantity * (float)rate.get("percent") / 100;
            }
        }
        return money;
    }

    /**
     * 生成退订订单
     * @param itemId 子订单ID
     * @param daysList 每天退票数
     * @param quantity 总退票数
     * @param money 退支付金额
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public RefundOrder generateRefundOrder(int itemId, List daysList, int quantity, int money, String cause) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setOrderItemId(itemId);
        refundOrder.setNumber(UUIDGenerator.generateUUID());
        refundOrder.setQuantity(quantity);
        refundOrder.setData(JsonUtils.toJson(daysList));
        refundOrder.setStatus(OrderConstant.RefundOrderStatus.AUDIT_WAIT);
        refundOrder.setCreateTime(new Date());
        refundOrder.setMoney(money);
        refundOrder.setCause(cause);
        refundOrderMapper.insertSelective(refundOrder);
        return refundOrder;
    }

    /**
     * 退货预分账
     * @param itemId 子订单ID
     * @param refundOrderId 退订订单ID
     * @param detailList 每天详情
     * @param refundDate 退订时间
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void preRefundAccount(List daysList, int itemId, int refundOrderId, List<OrderItemDetail> detailList, Date refundDate, int payAmount) throws Exception {
        Map<Integer, Integer> coreEpIdMap = new HashMap<>();
        List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderItem(itemId);
        for (OrderItemAccount account : accounts) {
            String data = account.getData();
            if (StringUtils.isEmpty(data)) {
                continue;
            }
            JSONArray daysData = JSONArray.parseArray(data);
            int coreEpId = getCoreEp(coreEpIdMap, account.getEpId());
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
                Map<String, Integer> rate = ProductRules.calcRefund(detail.getCustRefundRule(), detail.getDay(), refundDate);
                // 平台内部分账->利润
                if (coreEpId == account.getCoreEpId()) {
                    int profit = dayData.getIntValue("profit");
                    float percent = 1.0f;
                    if (rate.get("type") == ProductConstants.AddPriceType.FIX) {
                        percent = rate.get("fixed") / payAmount;
                    } else {
                        percent = rate.get("percent") / 100.0f;
                    }
                    money += profit * quantity * (1 - percent);
                    cash += profit * quantity * percent;
                } else {
                    // 平台之间分账->进货价
                    int inPrice = dayData.getIntValue("inPrice");
                    if (rate.get("type") == ProductConstants.AddPriceType.FIX) {
                        money += inPrice * quantity - rate.get("fixed") * quantity;
                    } else {
                        money += inPrice * quantity - inPrice * (float)rate.get("percent") / 100;
                    }
                }
            }
            RefundAccount refundAccount = new RefundAccount();
            refundAccount.setEpId(account.getEpId());
            refundAccount.setCoreEpId(account.getCoreEpId());
            refundAccount.setMoney(money == 0 ? 0 : -money);
            refundAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
            refundAccount.setRefundOrderId(refundOrderId);
            refundAccountMapper.insertSelective(refundAccount);
            RefundAccount refundAccountCash = new RefundAccount();
            refundAccountCash.setEpId(account.getEpId());
            refundAccountCash.setCoreEpId(account.getCoreEpId());
            refundAccountCash.setMoney(cash == 0 ? 0 : cash);
            refundAccountCash.setStatus(OrderConstant.AccountSplitStatus.NOT);
            refundAccountCash.setRefundOrderId(refundOrderId);
            refundAccountMapper.insertSelective(refundAccountCash);
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
            detail.setRefundQuantity(detail.getRefundQuantity() - quantity);
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
        List<Visitor> visitorList = visitorMapper.selectByOrderItem(refundOrder.getOrderItemId());
        for (RefundVisitor refundVisitor : refundVisitorList) {
            total += refundVisitor.getPreQuantity();
            refundVisitor.setReturnQuantity(refundVisitor.getPreQuantity());
            refundVisitor.setPreQuantity(0);
            refundVisitorMapper.updateByPrimaryKeySelective(refundVisitor);
            Visitor visitor = getVisitorById(visitorList, refundVisitor.getVisitorId());
            visitor.setReturnQuantity(visitor.getReturnQuantity() + refundVisitor.getReturnQuantity());
            visitorMapper.updateByPrimaryKeySelective(visitor);
        }
        return total;
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
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(refundOrder.getOrderItemId());
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
        refundSerial.setLocalSerialNo(UUIDGenerator.generateUUID());
        refundSerial.setQuantity(refundOrder.getQuantity());
        refundSerial.setData(refundOrder.getData());
        refundSerial.setRefundOrderId(refundOrder.getId());
        refundSerial.setCreateTime(new Date());
        refundSerialMapper.insertSelective(refundSerial);

        refundOrder.setStatus(OrderConstant.RefundOrderStatus.REFUNDING);
        refundOrder.setLocalRefundSerialNo(refundSerial.getLocalSerialNo());

        int i = 0;
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrderItemId());
        List<RefundVisitor> refundVisitorList = refundVisitorMapper.selectByRefundId(refundOrder.getId());
        for (RefundVisitor refundVisitor : refundVisitorList) {
            RefundTicketParams ticketParams = new RefundTicketParams();
            ticketParams.setApplyTime(refundOrder.getAuditTime());
            ticketParams.setOrderSn(refundVisitor.getOrderItemId());
            ticketParams.setQuantity(refundVisitor.getPreQuantity());
            ticketParams.setVisitorId(refundVisitor.getVisitorId());
            ticketParams.setRefundSn(String.valueOf(refundSerial.getLocalSerialNo()));
            ticketParams.setReason(refundOrder.getCause());
            try {
                // TODO: 2016/11/3 讲道理这里要一起提交或者只能一个人退票
                Result result = voucherRPCService.refundTicket(orderItem.getEpMaId(), ticketParams);
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

    /**
     * 退款
     * @param order 订单
     */
    public void refundMoney(Order order, int money, String sn) {
        log.debug("订单:{} 发起退款:{}", order.getNumber(), money);
        if (money == 0 && sn != null) {
            log.debug("订单:{} 退款为0元,直接调用退款成功.", order.getNumber());
            refundMoneyAfter(Long.valueOf(sn), true);
            return;
        }
        Integer coreEpId = getCoreEpId(getCoreEpId(order.getBuyEpId()));
        // 退款
        // 余额退款
        if (order.getPaymentType() == PaymentConstant.PaymentType.BALANCE.intValue()) {
            BalanceChangeInfo payInfo = new BalanceChangeInfo();
            payInfo.setEpId(coreEpId);
            payInfo.setCoreEpId(coreEpId);
            payInfo.setBalance(-money);

            BalanceChangeInfo saveInfo = new BalanceChangeInfo();
            saveInfo.setEpId(order.getBuyEpId());
            saveInfo.setCoreEpId(payInfo.getCoreEpId());
            saveInfo.setBalance(money);
            saveInfo.setCanCash(money);
            // 退款
            Result result = changeBalances(
                    PaymentConstant.BalanceChangeType.BALANCE_REFUND,
                    sn == null ? order.getNumber().toString() : sn, payInfo, saveInfo);
            if (!result.isSuccess()) {
                log.warn("余额退款失败:{}", result.get());
                throw new ApiException(result.getError());
            }
            return;
        }
        // 第三方退款
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("totalFee", order.getPayAmount());
        payParams.put("refundFee", money);
        payParams.put("serialNum", sn == null ? order.getLocalPaymentSerialNo() : sn);
        payParams.put("outTransId", order.getThirdSerialNo());
        Result result = thirdPayService.reqRefund(order.getNumber(), coreEpId, order.getPaymentType(), payParams);
        if (!result.isSuccess()) {
            log.warn("第三方退款异常:{}", result);
            throw new ApiException(result.getError());
        }
    }

    /**
     * 还可售库存
     * @param orderItemList
     */
    public void refundStock(List<OrderItem> orderItemList) {
        if (orderItemList != null) {
            List<ProductSearchParams> lockParams = new ArrayList<>();
            for (OrderItem orderItem : orderItemList) {
                List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
                for (OrderItemDetail detail : detailList) {
                    if (!detail.getOversell()) {
                        ProductSearchParams p = new ProductSearchParams();
                        p.setSubProductId(orderItem.getProSubId());
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
    public void refundStock(OrderItem orderItem) {
        refundStock(CommonUtil.oneToList(orderItem));
    }

    /**
     * 退款后操作
     * @param sn 流水
     * @param success 退款成功与否
     */
    public void refundMoneyAfter(long sn, boolean success) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(sn);

        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        refundOrder.setRefundMoneyTime(new Date());
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
            jobParams.put("orderItemId", String.valueOf(refundOrder.getOrderItemId()));
            jobParams.put("check", "true");
            addJob(OrderConstant.Actions.REFUND_STOCK, jobParams);
        }
        // 同步数据
        syncRefundOrderMoney(refundOrder.getId());
    }

    /**
     * 同步订单取消数据
     * @param orderId 订单ID
     */
    public void syncOrderCancelData(int orderId) {
        generateSyncByOrder(orderId)
                .put("t_order", CommonUtil.oneToList(orderMapper.selectByPrimaryKey(orderId)))
                .put("t_order_item", orderItemMapper.selectByOrderId(orderId))
                .sync();
    }

    /**
     * 同步退订订单申请数据
     * @param refundId 退订订单ID
     */
    public void syncRefundOrderApplyData(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        generateSyncByItem(refundOrder.getOrderItemId())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .put("t_order_item_detail", orderItemDetailMapper.selectByItemId(refundOrder.getOrderItemId()))
                .put("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundId))
                .put("t_refund_account", refundAccountMapper.selectByRefundId(refundId))
                .sync();
    }

    /**
     * 同步退订审核通过数据
     * @param refundId 退订订单ID
     */
    public void syncRefundOrderAuditAcceptData(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        generateSyncByItem(refundOrder.getOrderItemId())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .put("t_refund_serial", CommonUtil.oneToList(refundSerialMapper.selectByRefundOrder(refundOrder.getId())))
                .put("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundId))
                .put("t_visitor", visitorMapper.selectByOrderItem(refundOrder.getOrderItemId()))
                .put("t_order_item", CommonUtil.oneToList(orderItemMapper.selectByPrimaryKey(refundOrder.getOrderItemId())))
                .sync();
    }

    /**
     * 同步退订订单审核拒绝数据
     * @param refundId 退订订单ID
     */
    public void syncRefundOrderAuditRefuse(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        generateSyncByItem(refundOrder.getOrderItemId())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .put("t_order_item_detail", orderItemDetailMapper.selectByItemId(refundOrder.getOrderItemId()))
                .sync();
    }

    /**
     * 同步退订订单退款数据
     * @param refundId 退订订单ID
     */
    public void syncRefundOrderMoney(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        generateSyncByItem(refundOrder.getOrderItemId())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .sync();
    }

    /**
     * 同步退票数据
     * @param refundId 退订订单ID
     */
    public void syncRefundTicketData(int refundId) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        generateSyncByItem(refundOrder.getOrderItemId())
                .put("t_refund_order", CommonUtil.oneToList(refundOrder))
                .put("t_order_item", CommonUtil.oneToList(orderItemMapper.selectByPrimaryKey(refundOrder.getOrderItemId())))
                .put("t_refund_serial", CommonUtil.oneToList(refundSerialMapper.selectByRefundOrder(refundOrder.getId())))
                .put("t_visitor", visitorMapper.selectByOrderItem(refundOrder.getOrderItemId()))
                .put("t_refund_visitor", refundVisitorMapper.selectByRefundId(refundId))
                .sync();
    }
}
