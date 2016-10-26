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
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
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

    @Override
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
        // 获取平台商ID
        Integer coreEpId = bookingOrderManager.getCoreEpId(bookingOrderManager.getCoreEpId(buyEpId));
        // 锁定库存集合(统一锁定)
        Map<Integer, LockStockDto> lockStockDtoMap = new HashMap<>();
        List<ProductSearchParams> lockParams = new ArrayList<>();

        // 创建订单
        Order order = bookingOrderManager.generateOrder(coreEpId, buyEpId,
                CommonUtil.objectParseInteger(params.get("operator_id")),
                CommonUtil.objectParseString(params.get("operator_name")), from, remark);

        // 获取子订单
        List<Map> items = (List<Map>) params.get("items");
        for (Map item : items) {
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
            Result<ProductSalesInfo> salesInfoResult = productSalesPlanRPCService.validateProductSalesInfo(searchParams);
            if (salesInfoResult.hasError()) {
                throw new ApiException(salesInfoResult.getError());
            }
            ProductSalesInfo salesInfo = salesInfoResult.get();

            // 验证预定时间限制
            if (salesInfo.isBookingLimit()) {
                Date when = new Date();
                for (Integer i = 0; i < days; i++) {
                    int dayLimit = salesInfo.getBookingDayLimit();
                    Date limit = DateUtils.addDays(bookingDate, dayLimit);
                    String time = salesInfo.getBookingTimeLimit();
                    if (time != null) {
                        String[] timeArray = time.split(":");
                        limit = DateUtils.setHours(limit, Integer.parseInt(timeArray[0]));
                        limit = DateUtils.setMinutes(limit, Integer.parseInt(timeArray[1]));
                    }
                    if (limit.after(when)) {
                        throw new ApiException("预定时间限制");
                    }
                }
            }

            // 判断供应商状态是否为已冻结
            if (!bookingOrderManager.isEpStatus(epService.getEpStatus(salesInfo.getEpId()), EpConstant.EpStatus.ACTIVE)) {
                throw new ApiException("供应商企业已冻结");
            }

            // 判断游客信息
            List<Map> visitors = (List<Map>) item.get("visitor");
            if (salesInfo.isRequireSid()) {
                Result visitorResult = bookingOrderManager.validateVisitor(
                        visitors, productSubId, bookingDate, salesInfo.getSidDayCount(), salesInfo.getSidDayQuantity());
                if (visitorResult.hasError()) {
                    throw new ApiException(visitorResult.getError());
                }
            }

            // 景点特殊处理,自己封装每天的价格
            List<List<EpSalesInfo>> allDaysSales = new ArrayList<>();
            for (Integer i = 0; i < days; i++) {
                allDaysSales.add(new ArrayList<>(salesInfo.getSales()));
            }

            // 子订单总进货价
            int saleAmount = 0;
            for (List<EpSalesInfo> daySales : allDaysSales) {
                // 获取销售商价格
                EpSalesInfo info = bookingOrderManager.getBuyingPrice(daySales, buyEpId);
                saleAmount += info.getPrice() * quantity;
                // 只计算预付的,到付不计算在内
                if (salesInfo.getPayType() == ProductConstants.PayType.PREPAY) {
                    totalPayPrice += info.getPrice() * quantity; // 计算进货价
                    totalPayShopPrice += info.getShopPrice() * quantity; // 计算门市价
                }

                EpSalesInfo self = new EpSalesInfo();
                self.setSaleEpId(buyEpId);
                self.setBuyEpId(-1);
                // 代收:叶子销售商以门市价卖出
                self.setPrice(from == OrderConstant.FromType.TRUST ? info.getShopPrice() : info.getPrice());
                daySales.add(self);
            }
            totalPrice += saleAmount;

            // 创建子订单
            OrderItem orderItem = bookingOrderManager.generateItem(salesInfo, saleAmount, bookingDate, days, order.getId(), quantity);

            List<OrderItemDetail> detailList = new ArrayList<>();
            // 创建子订单详情
            for (Integer i = 0; i < days; i++) {
                OrderItemDetail orderItemDetail = bookingOrderManager.generateDetail(salesInfo, orderItem.getId(), DateUtils.addDays(bookingDate, i), quantity);
                detailList.add(orderItemDetail);
                // 创建游客信息
                for (Map v : visitors) {
                    Visitor visitor = bookingOrderManager.generateVisitor(v, orderItemDetail.getId());
                    visitorQuantity += visitor.getQuantity();
                }
            }

            // 判断总张数是否匹配
            if (visitorQuantity != quantity) {
                throw new ApiException("游客票数与总票数不符");
            }
            lockStockDtoMap.put(orderItem.getId(), new LockStockDto(orderItem, detailList));
            lockParams.add(bookingOrderManager.parseParams(orderItem));

            // 预分账记录
            bookingOrderManager.preSplitAccount(allDaysSales, orderItem.getId(), quantity, salesInfo.getPayType(), bookingDate);
        }

        // 创建订单联系人
        Map shippingMap = (Map) params.get("shipping");
        bookingOrderManager.generateShipping(shippingMap, order.getId());

        // 锁定库存
        Result<Map<Integer, List<Boolean>>> lockResult = productSalesPlanRPCService.lockProductStocks(lockParams);
        if (lockResult.hasError()) {
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
        orderMapper.updateByPrimaryKeySelective(order);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", JsonUtils.obj2map(order));
        resultMap.put("items", JsonUtils.toJson(orderItems));
        Result<Object> result = new Result<>(true);
        result.put(resultMap);

        // 同步数据
        bookingOrderManager.syncCreateOrderData(order.getId());
        return result;
    }

    @Override
    public Result<?> audit(Map params) {
        long orderItemSn = Long.valueOf(params.get("order_item_id").toString());
        OrderItem orderItem = orderItemMapper.selectBySN(orderItemSn);
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
            return new Result<>(true);
        }
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        // 不通过
        // 取消订单
        return refundOrderManager.cancel(order.getNumber());
    }

    @Override
    public Result<?> payment(Map params) {
        Long sn = Long.valueOf(params.get("order_sn").toString());
        Integer payType = CommonUtil.objectParseInteger(params.get("pay_type"));

        Order order = orderMapper.selectBySN(sn);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (order.getStatus() != OrderConstant.OrderStatus.PAY_WAIT &&
                order.getStatus() != OrderConstant.OrderStatus.PAY_FAIL) {
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
            if (result.hasError()) {
                log.warn("余额支付失败:{}", result.get());
                throw new ApiException(result.getError());
            }
            return new Result<>(true);
        }
        // 第三方支付
        // 获取商品名称
        List<String> names = orderItemMapper.getProductNamesByOrderId(order.getId());
        List<Integer> ids = orderItemMapper.getProductIdsByOrderId(order.getId());
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("prodName", StringUtils.join(names, ","));
        payParams.put("totalFee", order.getPayAmount());
        payParams.put("serialNum", order.getNumber().toString());
        payParams.put("prodId", StringUtils.join(ids, ","));

        Result result = thirdPayService.reqPay(order.getNumber(),
                bookingOrderManager.getCoreEpId(epService.selectPlatformId(order.getBuyEpId())),
                payType, payParams);
        if (result.hasError()) {
            log.warn("第三方支付异常:{}", result);
            throw new ApiException(result.getError());
        }
        return result;
    }

    @Override
    public Result<?> resendTicket(Map params) {
        return null;
    }
}
