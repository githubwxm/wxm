package com.all580.order.service;

import com.all580.order.adapter.CreateOrderInterface;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.dao.*;
import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.LockStockDto;
import com.all580.order.dto.PriceDto;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.*;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.LockTransactionManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.all580.voucher.api.model.ReSendTicketParams;
import com.all580.voucher.api.model.group.ModifyGroupTicketParams;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventManager;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import javax.lang.exception.ParamsMapValidationException;
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
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private MaSendResponseMapper maSendResponseMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private VoucherRPCService voucherRPCService;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Autowired
    private LockTransactionManager lockTransactionManager;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @MnsEvent
    public Result<?> create(Map params, String type) throws Exception {
        CreateOrderInterface orderInterface = applicationContext.getBean(OrderConstant.CREATE_ADAPTER + type, CreateOrderInterface.class);
        Assert.notNull(orderInterface, type + " 类型创建订单适配器没找到");

        int totalSalePrice = 0;
        int totalPayPrice = 0;
        CreateOrder createOrder = orderInterface.parseParams(params);

        Result validateResult = orderInterface.validate(createOrder, params);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }

        // 创建订单
        Order order = orderInterface.insertOrder(createOrder, params);
        // 获取子订单
        List<Map> items = (List<Map>) params.get("items");
        // 锁定库存集合(统一锁定)
        Map<Integer, LockStockDto> lockStockDtoMap = new HashMap<>();
        List<ProductSearchParams> lockParams = new ArrayList<>();
        for (Map item : items) {
            ValidateProductSub sub = orderInterface.parseItemParams(item);
            ProductSalesInfo salesInfo = orderInterface.validateProductAndGetSales(sub, createOrder, item);

            orderInterface.validateBookingDate(sub, salesInfo.getDay_info_list());

            // 判断游客信息
            List<?> visitors = (List<?>) item.get("visitor");
            orderInterface.validateVisitor(salesInfo, sub, visitors, item);

            // 每天的价格
            List<List<EpSalesInfo>> allDaysSales = salesInfo.getSales();
            Assert.notEmpty(allDaysSales, "该产品未被分销");

            // 子订单总进货价
            PriceDto price = bookingOrderManager.calcSalesPrice(allDaysSales, salesInfo, createOrder.getEpId(), sub.getQuantity(), createOrder.getFrom());
            totalSalePrice += price.getSale();
            // 只计算预付的,到付不计算在内
            if (salesInfo.getPay_type() == ProductConstants.PayType.PREPAY) {
                totalPayPrice += price.getSale();
            }

            // 创建子订单
            OrderItem orderItem = orderInterface.insertItem(order, sub, salesInfo, price, item);


            // 创建子订单详情
            List<OrderItemDetail> details = orderInterface.insertDetail(orderItem, sub, salesInfo);

            orderInterface.insertVisitor(visitors, details, salesInfo, sub, item);

            lockStockDtoMap.put(orderItem.getId(), new LockStockDto(orderItem, details, salesInfo.getDay_info_list()));
            lockParams.add(bookingOrderManager.parseParams(orderItem));

            // 预分账记录
            bookingOrderManager.prePaySplitAccount(allDaysSales, orderItem, createOrder.getEpId());
        }
        // 更新订单金额
        order.setPay_amount(totalPayPrice);
        order.setSale_amount(totalSalePrice);

        // 创建订单联系人
        orderInterface.insertShipping(params, order);

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
        // 检查是否待审核
        checkCreateAudit(lockStockDtoMap, listMap, orderItems);

        // 更新订单状态
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getStatus() == OrderConstant.OrderItemStatus.AUDIT_WAIT) {
                order.setStatus(OrderConstant.OrderStatus.AUDIT_WAIT);
                break;
            }
        }

        // 更新审核时间
        if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT && order.getAudit_time() == null) {
            order.setAudit_time(new Date());
        }

        orderMapper.updateByPrimaryKeySelective(order);

        // 执行后事
        orderInterface.after(params, order);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", JsonUtils.obj2map(order));
        resultMap.put("items", JsonUtils.json2List(JsonUtils.toJson(orderItems)));
        Result<Object> result = new Result<>(true);
        result.put(resultMap);

        // 触发事件
        MnsEventManager.addEvent(OrderConstant.EventType.ORDER_CREATE, order.getId());
        Map<String, Collection<?>> data = new HashMap<>();
        data.put("t_order", CommonUtil.oneToList(order));
        data.put("t_order_item", orderItems);
        return result.putExt(Result.SYNC_DATA, JsonUtils.obj2map(data));
    }

    @Override
    public Result<?> audit(Map params) {
        String orderItemId = params.get("order_item_id").toString();
        long orderItemSn = Long.valueOf(orderItemId);
        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemId, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.auditBooking(params, orderItemSn);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<?> payment(Map params) {
        String orderSn = params.get("order_sn").toString();
        Long sn = Long.valueOf(orderSn);
        Integer payType = CommonUtil.objectParseInteger(params.get("pay_type"));

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderSn, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.payment(params, sn, payType);
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
        if (ArrayUtils.indexOf(new int[]{
                OrderConstant.OrderItemStatus.TICKET_FAIL,
                OrderConstant.OrderItemStatus.SEND,
                OrderConstant.OrderItemStatus.NON_SEND
        }, orderItem.getStatus()) < 0) {
            throw new ApiException("子订单不在可重新发票状态");
        }

        String phone = CommonUtil.objectParseString(params.get("phone"));

        // 是否全部重新发票
        Boolean all = Boolean.parseBoolean(CommonUtil.objectParseString(params.get("all")));
        if (all) {
            List<Visitor> visitors = visitorMapper.selectByOrderItem(orderItem.getId());
            for (Visitor visitor : visitors) {
                reSendTicket(orderItem, visitor.getId(), StringUtils.isEmpty(phone) ? visitor.getPhone() : phone);
            }
        } else {
            Integer visitorId = CommonUtil.objectParseInteger(params.get("visitor_id"));
            if (visitorId == null) {
                throw new ParamsMapValidationException("visitor_id", "游客ID不能为空");
            }
            Result result = reSendTicket(orderItem, visitorId, phone);
            if (!result.isSuccess()) {
                throw new ApiException(result.getError());
            }
        }
        return new Result<>(true);
    }

    @Override
    public Result<?> resendTicketForGroup(Map params) {
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(params.get("order_item_sn").toString()));
        if (orderItem == null) {
            throw new ApiException("子订单不存在");
        }
        if (ArrayUtils.indexOf(new int[]{
                OrderConstant.OrderItemStatus.TICKET_FAIL,
                OrderConstant.OrderItemStatus.SEND,
                OrderConstant.OrderItemStatus.NON_SEND
        }, orderItem.getStatus()) < 0) {
            throw new ApiException("子订单不在可重新发票状态");
        }

        if (!(orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM)) {
            return new Result(false, "该订单不是团队订单");
        }

        List<RefundOrder> refundOrderList = refundOrderMapper.selectByItemId(orderItem.getId());
        if (refundOrderList != null && refundOrderList.size() > 0) {
            throw new ApiException("该子订单已发起退票");
        }

        if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND) {
            return voucherRPCService.resendGroupTicket(orderItem.getEp_ma_id(), orderItem.getNumber());
        }
        // 出票
        // 记录任务
        Map<String, String> jobMap = new HashMap<>();
        jobMap.put("orderItemId", orderItem.getId().toString());
        bookingOrderManager.addJob(OrderConstant.Actions.SEND_GROUP_TICKET, jobMap);
        return new Result<>(true);
    }

    @Override
    public Result<?> modifyTicketForGroup(Map params) {
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(params.get("order_item_sn").toString()));
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }
        if (!(orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM)) {
            return new Result(false, "该订单不是团队订单");
        }
        if ((orderItem.getStatus() != OrderConstant.OrderItemStatus.SEND &&
                orderItem.getStatus() != OrderConstant.OrderItemStatus.MODIFY &&
                orderItem.getStatus() != OrderConstant.OrderItemStatus.MODIFY_FAIL) ||
                (orderItem.getUsed_quantity() != null && orderItem.getUsed_quantity() > 0)) {
            return new Result(false, "该订单不在可修改状态");
        }

        ModifyGroupTicketParams ticketParams = new ModifyGroupTicketParams();
        ticketParams.setOrderSn(orderItem.getNumber());
        String guideName = CommonUtil.objectParseString(params.get("guide_name"));
        if (StringUtils.isNotEmpty(guideName)) {
            ticketParams.setGuideName(guideName);
        }
        String guidePhone = CommonUtil.objectParseString(params.get("guide_phone"));
        if (StringUtils.isNotEmpty(guidePhone)) {
            ticketParams.setGuidePhone(guidePhone);
        }
        String guideSid = CommonUtil.objectParseString(params.get("guide_sid"));
        if (StringUtils.isNotEmpty(guideSid)) {
            ticketParams.setGuideSid(guideSid);
        }
        String guideCard = CommonUtil.objectParseString(params.get("guide_card"));
        if (StringUtils.isNotEmpty(guideCard)) {
            ticketParams.setGuideCard(guideCard);
        }
        List visitors = (List) params.get("visitors");
        if (visitors != null && visitors.size() > 0) {
            List<com.all580.voucher.api.model.Visitor> vs = new ArrayList<>();
            for (Object o : visitors) {
                Map visitor = (Map) o;
                String name = CommonUtil.objectParseString(visitor.get("name"));
                if (StringUtils.isNotEmpty(name)) {
                    com.all580.voucher.api.model.Visitor v = new com.all580.voucher.api.model.Visitor();
                    v.setName(name);
                    v.setPhone(visitor.get("phone").toString());
                    v.setSid(visitor.get("sid").toString());
                    vs.add(v);
                }
            }
            ticketParams.setVisitors(vs);
        }
        return voucherRPCService.modifyGroupTicket(orderItem.getEp_ma_id(), ticketParams);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @MnsEvent
    public void checkCreateAudit(Map<Integer, LockStockDto> lockStockDtoMap, Map<Integer, List<Boolean>> listMap, List<OrderItem> orderItems) {
        for (Integer itemId : listMap.keySet()) {
            int i = 0;
            // 判断是否需要审核
            LockStockDto lockStockDto = lockStockDtoMap.get(itemId);
            OrderItem item = lockStockDto.getOrderItem();
            List<Boolean> booleanList = listMap.get(itemId);
            List<OrderItemDetail> orderItemDetail = lockStockDto.getOrderItemDetail();
            List<ProductSalesDayInfo> dayInfoList = lockStockDto.getDayInfoList();
            if (booleanList.size() != orderItemDetail.size() || booleanList.size() != dayInfoList.size()) {
                throw new ApiException(String.format("锁库存天数:%d与购买天数:%d不匹配", booleanList.size(), orderItemDetail.size()));
            }
            for (Boolean oversell : booleanList) {
                OrderItemDetail detail = orderItemDetail.get(i);
                ProductSalesDayInfo dayInfo = dayInfoList.get(i);
                detail.setOversell(oversell);
                // 如果是超卖则把子订单状态修改为待审
                if ((oversell &&
                        (dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.OVERSELL_NEED_AUDIT ||
                                dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.ANYWAY_NEED_AUDIT) ||
                        dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.ANYWAY_NEED_AUDIT_OVERSELL ||
                        dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.ANYWAY_NEED_AUDIT_NOT_OVERSELL) &&
                        item.getStatus() == OrderConstant.OrderItemStatus.AUDIT_SUCCESS) {
                    item.setStatus(OrderConstant.OrderItemStatus.AUDIT_WAIT);
                    orderItemMapper.updateByPrimaryKeySelective(item);
                    MnsEventManager.addEvent(OrderConstant.EventType.ORDER_WAIT_AUDIT, item.getId());
                }
                // 更新子订单详情
                orderItemDetailMapper.updateByPrimaryKeySelective(detail);
                i++;
            }
            orderItems.add(item);
        }
    }

    private Result reSendTicket(OrderItem orderItem, Integer visitorId, String phone) {
        RefundOrder refundOrder = refundOrderMapper.selectByItemIdAndVisitor(orderItem.getId(), visitorId);
        if (refundOrder != null) {
            return new Result(false, "该子订单已发起退票");
        }
        MaSendResponse response = maSendResponseMapper.selectByVisitorId(orderItem.getId(), visitorId, orderItem.getEp_ma_id());
        if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND && response != null) {
            ReSendTicketParams reSendTicketParams = new ReSendTicketParams();
            reSendTicketParams.setOrderSn(orderItem.getNumber());
            reSendTicketParams.setVisitorId(visitorId);
            reSendTicketParams.setMobile(phone);
            return voucherRPCService.resendTicket(orderItem.getEp_ma_id(), reSendTicketParams);
        }
        // 出票
        // 记录任务
        Map<String, String> jobParam = new HashMap<>();
        jobParam.put("orderItemId", orderItem.getId().toString());
        bookingOrderManager.addJob(OrderConstant.Actions.SEND_TICKET, jobParam);
        return new Result(true);
    }
}
