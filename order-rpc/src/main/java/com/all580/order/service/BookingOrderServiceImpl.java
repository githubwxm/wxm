package com.all580.order.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dto.LockStockDto;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.RefundOrderManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.all580.voucher.api.model.ReSendTicketParams;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务实现
 * @date 2016/9/28 9:23
 */
@Service
@Slf4j
public class BookingOrderServiceImpl implements BookingOrderService {
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private RefundOrderManager refundOrderManager;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private EpService epService;
    @Autowired
    private ThirdPayService thirdPayService;
    @Autowired
    private VoucherRPCService voucherRPCService;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> create(Map params) throws Exception {
        Integer buyEpId = CommonUtil.objectParseInteger(params.get("ep_id"));
        Integer from = CommonUtil.objectParseInteger(params.get("from"));
        String remark = CommonUtil.objectParseString(params.get("remark"));
        int totalPrice = 0;
        int totalPayPrice = 0;
        int totalPayShopPrice = 0;

        // 判断销售商状态是否为已冻结
        if (!bookingOrderManager.isEpStatus(epService.getEpStatus(buyEpId), EpConstant.EpStatus.ACTIVE)) {
            throw new ApiException("销售商企业已冻结");
        }
        // 只有销售商可以下单
        Result<Integer> epType = epService.selectEpType(buyEpId);
        if (!bookingOrderManager.isEpType(epType, EpConstant.EpType.SELLER) &&
                !bookingOrderManager.isEpType(epType, EpConstant.EpType.OTA)) {
            throw new ApiException("该企业不能购买产品");
        }
        // 获取平台商ID
        Integer coreEpId = bookingOrderManager.getCoreEpId(bookingOrderManager.getCoreEpId(buyEpId));
        // 锁定库存集合(统一锁定)
        Map<Integer, LockStockDto> lockStockDtoMap = new HashMap<>();
        List<ProductSearchParams> lockParams = new ArrayList<>();

        // 获取下单企业名称
        String buyEpName = null;
        Result<Map<String, Object>> epResult = epService.selectId(buyEpId);
        if (epResult != null && epResult.isSuccess() && epResult.get() != null) {
            buyEpName = String.valueOf(epResult.get().get("name"));
        }

        // 创建订单
        Order order = bookingOrderManager.generateOrder(coreEpId, buyEpId, buyEpName,
                CommonUtil.objectParseInteger(params.get("operator_id")),
                CommonUtil.objectParseString(params.get("operator_name")), from, remark);

        // 存储游客信息
        Map<Integer, List<Visitor>> visitorMap = new HashMap<>();
        // 获取子订单
        List<Map> items = (List<Map>) params.get("items");
        for (Map item : items) {
            // TODO: 2016/11/2 这里应该是productSubCode
            Integer productSubId = CommonUtil.objectParseInteger(item.get("product_sub_id"));
            Integer quantity = CommonUtil.objectParseInteger(item.get("quantity"));
            Integer days = CommonUtil.objectParseInteger(item.get("days"));
            int visitorQuantity = 0; // 游客总票数
            //预定日期
            Date bookingDate = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, CommonUtil.objectParseString(item.get("start")));

            // 验证是否可售
            ProductSearchParams searchParams = new ProductSearchParams();
            searchParams.setSubProductId(productSubId);
            searchParams.setStartDate(bookingDate);
            searchParams.setDays(days);
            searchParams.setQuantity(quantity);
            searchParams.setBuyEpId(buyEpId);
            Result<ProductSalesInfo> salesInfoResult = productSalesPlanRPCService.validateProductSalesInfo(searchParams);
            if (!salesInfoResult.isSuccess()) {
                throw new ApiException(salesInfoResult.getError());
            }
            ProductSalesInfo salesInfo = salesInfoResult.get();
            // 判断供应商状态是否为已冻结
            if (!bookingOrderManager.isEpStatus(epService.getEpStatus(salesInfo.getEpId()), EpConstant.EpStatus.ACTIVE)) {
                throw new ApiException("供应商企业已冻结");
            }

            List<ProductSalesDayInfo> dayInfoList = salesInfo.getDayInfoList();

            if (dayInfoList.size() != days) {
                throw new ApiException("预定天数与获取产品天数不匹配");
            }
            // 验证预定时间限制
            Date when = new Date();
            for (ProductSalesDayInfo dayInfo : dayInfoList) {
                if (dayInfo.isBookingLimit()) {
                    int dayLimit = dayInfo.getBookingDayLimit();
                    Date limit = DateUtils.addDays(bookingDate, -dayLimit);
                    String time = dayInfo.getBookingTimeLimit();
                    if (time != null) {
                        String[] timeArray = time.split(":");
                        limit = DateUtils.setHours(limit, Integer.parseInt(timeArray[0]));
                        limit = DateUtils.setMinutes(limit, Integer.parseInt(timeArray[1]));
                    }
                    if (when.after(limit)) {
                        throw new ApiException("预定时间限制");
                    }
                }
            }

            // 判断游客信息
            List<Map> visitors = (List<Map>) item.get("visitor");
            if (salesInfo.isRequireSid()) {
                Result visitorResult = bookingOrderManager.validateVisitor(
                        visitors, salesInfo.getProductSubCode(), bookingDate, salesInfo.getSidDayCount(), salesInfo.getSidDayQuantity());
                if (!visitorResult.isSuccess()) {
                    throw new ApiException(visitorResult.getError());
                }
            }

            // 每天的价格
            List<List<EpSalesInfo>> allDaysSales = salesInfo.getSales();

            // 子订单总进货价
            int saleAmount = 0;
            for (List<EpSalesInfo> daySales : allDaysSales) {
                // 获取销售商价格
                EpSalesInfo info = bookingOrderManager.getBuyingPrice(daySales, buyEpId);
                if (info == null) {
                    throw new ApiException("非法购买:该销售商未销售本产品");
                }
                saleAmount += info.getPrice() * quantity;
                // 只计算预付的,到付不计算在内
                if (salesInfo.getPayType() == ProductConstants.PayType.PREPAY) {
                    totalPayPrice += info.getPrice() * quantity; // 计算进货价
                    totalPayShopPrice += (info.getShopPrice() == null ? 0 : info.getShopPrice()) * quantity; // 计算门市价
                }

                EpSalesInfo self = new EpSalesInfo();
                self.setSaleEpId(buyEpId);
                self.setBuyEpId(-1);
                // 代收:叶子销售商以门市价卖出
                self.setPrice(from == OrderConstant.FromType.TRUST ? info.getShopPrice() : info.getPrice());
                if (self.getPrice() == null) {
                    self.setPrice(0);
                }
                daySales.add(self);
            }
            totalPrice += saleAmount;

            // 创建子订单
            OrderItem orderItem = bookingOrderManager.generateItem(salesInfo, dayInfoList.get(dayInfoList.size() - 1).getEndTime(), saleAmount, bookingDate, days, order.getId(), quantity, productSubId);

            List<OrderItemDetail> detailList = new ArrayList<>();
            List<Visitor> visitorList = new ArrayList<>();
            // 创建子订单详情
            int i = 0;
            for (ProductSalesDayInfo dayInfo : dayInfoList) {
                OrderItemDetail orderItemDetail = bookingOrderManager.generateDetail(dayInfo, orderItem.getId(), DateUtils.addDays(bookingDate, i), quantity);
                detailList.add(orderItemDetail);
                // 创建游客信息
                for (Map v : visitors) {
                    Visitor visitor = bookingOrderManager.generateVisitor(v, orderItemDetail.getId());
                    visitorQuantity += visitor.getQuantity();
                    visitorList.add(visitor);
                }
                i++;
            }

            // 判断总张数是否匹配
            if (visitorQuantity != quantity) {
                throw new ApiException("游客票数与总票数不符");
            }
            lockStockDtoMap.put(orderItem.getId(), new LockStockDto(orderItem, detailList));
            lockParams.add(bookingOrderManager.parseParams(orderItem));
            visitorMap.put(orderItem.getId(), visitorList);

            // 预分账记录
            bookingOrderManager.preSplitAccount(allDaysSales, orderItem.getId(), quantity, salesInfo.getPayType(), bookingDate);
        }

        // 创建订单联系人
        Map shippingMap = (Map) params.get("shipping");
        bookingOrderManager.generateShipping(shippingMap, order.getId());

        // 锁定库存
        Result<Map<Integer, List<Boolean>>> lockResult = productSalesPlanRPCService.lockProductStocks(lockParams);
        if (!lockResult.isSuccess()) {
            throw new ApiException(lockResult.getError());
        }

        Map<Integer, List<Boolean>> listMap = lockResult.get();
        if (listMap.size() != lockStockDtoMap.size()) {
            throw new ApiException("锁定库存异常");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (Integer itemId : listMap.keySet()) {
            int i = 0;
            // 判断是否需要审核
            LockStockDto lockStockDto = lockStockDtoMap.get(itemId);
            OrderItem item = lockStockDto.getOrderItem();
            for (Boolean oversell : listMap.get(itemId)) {
                OrderItemDetail detail = lockStockDto.getOrderItemDetail().get(i);
                detail.setOversell(oversell);
                // 如果是超卖则把子订单状态修改为待审
                if (oversell && item.getStatus() == OrderConstant.OrderItemStatus.AUDIT_SUCCESS) {
                    item.setStatus(OrderConstant.OrderItemStatus.AUDIT_WAIT);
                    orderItemMapper.updateByPrimaryKeySelective(item);
                }
                // 更新子订单详情
                orderItemDetailMapper.updateByPrimaryKeySelective(detail);
                i++;
            }
            orderItems.add(item);
        }

        // 更新订单状态
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getStatus() == OrderConstant.OrderItemStatus.AUDIT_WAIT) {
                order.setStatus(OrderConstant.OrderStatus.AUDIT_WAIT);
                break;
            }
        }

        // 更新订单金额
        order.setPayAmount(from == OrderConstant.FromType.TRUST ? totalPayShopPrice : totalPayPrice);
        order.setSaleAmount(totalPrice);

        // 到付
        if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT && order.getPayAmount() <= 0) {
            order.setStatus(OrderConstant.OrderStatus.PAID_HANDLING); // 已支付,处理中
            // 支付成功回调 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", order.getId().toString());
            bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, jobParams);
        }
        orderMapper.updateByPrimaryKeySelective(order);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", JsonUtils.obj2map(order));
        resultMap.put("items", JsonUtils.json2List(JsonUtils.toJson(orderItems)));
        resultMap.put("visitors", JsonUtils.obj2map(visitorMap));
        Result<Object> result = new Result<>(true);
        result.put(resultMap);

        // 同步数据
        bookingOrderManager.syncCreateOrderData(order.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> audit(final Map params) {
        String orderItemId = params.get("order_item_id").toString();
        long orderItemSn = Long.valueOf(orderItemId);
        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemId, lockTimeOut);

        // 锁成功
        try {
            final OrderItem orderItem = orderItemMapper.selectBySN(orderItemSn);
            if (orderItem == null) {
                throw new ApiException("订单不存在");
            }
            if (orderItem.getStatus() != OrderConstant.OrderItemStatus.AUDIT_WAIT) {
                throw new ApiException("订单不在待审状态");
            }

            Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
            if (order == null) {
                throw new ApiException("订单不存在");
            }
            if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT) {
                throw new ApiException("订单不在待审状态");
            }

            orderItem.setAuditUserId(CommonUtil.objectParseInteger(params.get("operator_id")));
            orderItem.setAuditUserName(CommonUtil.objectParseString(params.get("operator_name")));
            boolean status = Boolean.parseBoolean(params.get("status").toString());
            // 通过
            if (status) {
                orderItem.setStatus(OrderConstant.OrderItemStatus.AUDIT_SUCCESS);
                orderItemMapper.updateByPrimaryKeySelective(orderItem);
                boolean allAudit = bookingOrderManager.isOrderAllAudit(orderItem.getOrderId(), orderItem.getId());
                if (allAudit) {
                    order.setStatus(OrderConstant.OrderStatus.PAY_WAIT);
                    // 判断是否需要支付
                    if (order.getPayAmount() <= 0) { // 不需要支付
                        order.setStatus(OrderConstant.OrderStatus.PAID_HANDLING); // 已支付,处理中
                        // 支付成功回调 记录任务
                        Map<String, String> jobParams = new HashMap<>();
                        jobParams.put("orderId", order.getId().toString());
                        bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, jobParams);
                    }
                    orderMapper.updateByPrimaryKeySelective(order);
                }
                // 同步数据
                bookingOrderManager.syncOrderAuditAcceptData(order.getId(), orderItem.getId());
                return new Result<>(true);
            }
            orderItemMapper.updateByPrimaryKeySelective(orderItem);

            // 不通过
            // 取消订单 会自动同步数据
            return refundOrderManager.cancel(order.getNumber());
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> payment(Map params) {
        String orderSn = params.get("order_sn").toString();
        Long sn = Long.valueOf(orderSn);
        Integer payType = CommonUtil.objectParseInteger(params.get("pay_type"));

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderSn, lockTimeOut);

        // 锁成功
        try {
            Order order = orderMapper.selectBySN(sn);
            if (order == null) {
                throw new ApiException("订单不存在");
            }
            if (order.getStatus() != OrderConstant.OrderStatus.PAY_WAIT &&
                    order.getStatus() != OrderConstant.OrderStatus.PAY_FAIL &&
                    order.getStatus() != OrderConstant.OrderStatus.PAYING) {
                throw new ApiException("订单不在待支付状态");
            }
            if (order.getPayAmount() <= 0) {
                throw new ApiException("该订单不需要支付");
            }


            order.setStatus(OrderConstant.OrderStatus.PAYING);
            order.setLocalPaymentSerialNo(String.valueOf(UUIDGenerator.generateUUID()));
            order.setPaymentType(payType);
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

            // 调用支付RPC
            // 余额支付
            if (payType == PaymentConstant.PaymentType.BALANCE.intValue()) {
                BalanceChangeInfo payInfo = new BalanceChangeInfo();
                payInfo.setEpId(order.getBuyEpId());
                payInfo.setCoreEpId(bookingOrderManager.getCoreEpId(bookingOrderManager.getCoreEpId(order.getBuyEpId())));
                payInfo.setBalance(-order.getPayAmount());
                payInfo.setCanCash(-order.getPayAmount());

                BalanceChangeInfo saveInfo = new BalanceChangeInfo();
                saveInfo.setEpId(payInfo.getCoreEpId());
                saveInfo.setCoreEpId(payInfo.getCoreEpId());
                saveInfo.setBalance(order.getPayAmount());
                // 支付
                Result result = bookingOrderManager.changeBalances(
                        PaymentConstant.BalanceChangeType.BALANCE_PAY,
                        order.getNumber().toString(), payInfo, saveInfo);
                if (!result.isSuccess()) {
                    log.warn("余额支付失败:{}", result.get());
                    throw new ApiException(result.getError());
                }
                // 同步数据
                bookingOrderManager.syncOrderPaymentData(order.getId());
                return new Result<>(true);
            }
            // 第三方支付
            // 获取商品名称
            List<String> names = orderItemMapper.getProductNamesByOrderId(order.getId());
            List<Long> ids = orderItemMapper.getProductIdsByOrderId(order.getId());
            Map<String, Object> payParams = new HashMap<>();
            payParams.put("prodName", StringUtils.join(names, ","));
            payParams.put("totalFee", order.getPayAmount());
            payParams.put("serialNum", order.getNumber().toString());
            payParams.put("prodId", StringUtils.join(ids, ","));

            Result result = thirdPayService.reqPay(order.getNumber(),
                    bookingOrderManager.getCoreEpId(epService.selectPlatformId(order.getBuyEpId())),
                    payType, payParams);
            if (!result.isSuccess()) {
                log.warn("第三方支付异常:{}", result);
                throw new ApiException(result.getError());
            }
            // 同步数据
            bookingOrderManager.syncOrderPaymentData(order.getId());
            return result;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<?> resendTicket(Map params) {
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(params.get("order_item_sn").toString()));
        if (orderItem == null) {
            throw new ApiException("子订单不存在");
        }
        ReSendTicketParams reSendTicketParams = new ReSendTicketParams();
        reSendTicketParams.setOrderSn(orderItem.getNumber());
        reSendTicketParams.setVisitorId(CommonUtil.objectParseInteger(params.get("visitor_id")));
        reSendTicketParams.setMobile(params.get("phone").toString());
        return voucherRPCService.resendTicket(orderItem.getEpMaId(), reSendTicketParams);
    }
}
