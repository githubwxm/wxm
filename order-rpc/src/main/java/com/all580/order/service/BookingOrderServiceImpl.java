package com.all580.order.service;

import com.all580.order.adapter.CreateOrderInterface;
import com.all580.order.adapter.CreatePackageOrderService;
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
import com.all580.order.manager.SmsManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventAspect;
import com.framework.common.lang.JsonUtils;
import com.framework.common.outside.JobAspect;
import com.framework.common.outside.JobTask;
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
    private PackageOrderItemMapper packageOrderItemMapper;

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
    @Autowired
    private MnsEventAspect eventManager;
    @Autowired
    private JobAspect jobManager;
    @Autowired
    private SmsManager smsManager;
    @Autowired
    private CreatePackageOrderService createPackageOrderService;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @MnsEvent
    public Result<?> create(Map params, String type) throws Exception {
        CreateOrderInterface orderInterface = applicationContext.getBean(OrderConstant.CREATE_ADAPTER + type, CreateOrderInterface.class);
        Assert.notNull(orderInterface, type + " 类型创建订单适配器没找到");

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
        ValidateProductSub first = null;
        for (Map item : items) {
            ValidateProductSub sub = orderInterface.parseItemParams(createOrder, item);
            if (first == null) first = sub;
            this.createOrderItem(orderInterface, createOrder, order, sub, item, lockStockDtoMap, lockParams);
        }
        if (first == null) {
            throw new ApiException("请选择购买的产品");
        }

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
        boolean ok = orderInterface.after(params, order);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", order);
        resultMap.put("items", orderItems);
        Result<Map> result = new Result<>(true);
        result.put(resultMap);

        // 触发事件
        eventManager.addEvent(OrderConstant.EventType.ORDER_CREATE, order.getId());
        if (ok) {
            Map<String, Collection<?>> data = new HashMap<>();
            data.put("t_order", CommonUtil.oneToList(order));
            data.put("t_order_item", orderItems);
            result.putExt(Result.SYNC_DATA, JsonUtils.obj2map(data));
        }

        log.info(OrderConstant.LogOperateCode.NAME, bookingOrderManager.orderLog(order.getId(), null,
                order.getBuy_ep_id(), order.getBuy_ep_name(), OrderConstant.LogOperateCode.CREATE_SUCCESS,
                null, String.format("订单创建成功:%s", JsonUtils.toJson(params)), null));
        return result;
    }

    private void createOrderItem(CreateOrderInterface orderInterface, CreateOrder createOrder, Order order,ValidateProductSub sub,
                                     Map item, Map<Integer, LockStockDto> lockStockDtoMap, List<ProductSearchParams> lockParams){
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

        // 创建子订单
        OrderItem orderItem = orderInterface.insertItem(order, sub, salesInfo, price, item);

        // 只计算预付的,到付不计算在内
        if (salesInfo.getPay_type() == ProductConstants.PayType.PREPAY) {
            order.setPay_amount(price.getSale() + (order.getPay_amount() == null ? 0 : order.getPay_amount()));
        }
        order.setSale_amount(price.getSale() + (order.getSale_amount() == null ? 0 : order.getSale_amount()));

        // 创建子订单详情
        List<OrderItemDetail> details = orderInterface.insertDetail(order, createOrder, orderItem, sub, salesInfo, allDaysSales);

        // 析构销售链
        orderInterface.insertSalesChain(orderItem, sub, allDaysSales);

        orderInterface.insertVisitor(visitors, orderItem, salesInfo, sub, item);

        lockStockDtoMap.put(orderItem.getId(), new LockStockDto(orderItem, details, salesInfo.getDay_info_list()));
        lockParams.add(bookingOrderManager.parseParams(orderItem));

        // 预分账记录
        bookingOrderManager.prePaySplitAccount(allDaysSales, orderItem, createOrder.getEpId());
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> createPackageOrder(Map params) throws Exception {
        //解析参数
        CreateOrder createOrder = createPackageOrderService.parseParams(params);
        //校验是否可购买
        Result validateResult = createPackageOrderService.validate(createOrder, params);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }

        // 创建订单
        Order order = createPackageOrderService.insertOrder(createOrder, params);

        ValidateProductSub packageSub = createPackageOrderService.parseItemParams(createOrder, params);

        ProductSalesInfo salesInfo = createPackageOrderService.validateProductAndGetSales(packageSub, createOrder, params);

        createPackageOrderService.validateBookingDate(packageSub, salesInfo.getDay_info_list());

        // 每天的价格
        List<List<EpSalesInfo>> allDaysSales = salesInfo.getSales();
        Assert.notEmpty(allDaysSales, "该产品未被分销");

        //套票订单总进货价
        PriceDto price = bookingOrderManager.calcSalesPrice(allDaysSales, salesInfo, createOrder.getEpId(), packageSub.getQuantity(), createOrder.getFrom());

        PackageOrderItem packageOrderItem = createPackageOrderService.insertPackageOrderInfo(salesInfo, order, params);
        packageOrderItem.setStart(packageSub.getBooking());

        if (salesInfo.getDay_info_list().size() != 1) {
            throw new ApiException("套票产品目前只能购买一天");
        }
        ProductSalesDayInfo productSalesDayInfo = salesInfo.getDay_info_list().get(0);
        packageOrderItem.setCust_refund_rule(productSalesDayInfo.getCust_refund_rule());
        packageOrderItem.setSaler_refund_rule(productSalesDayInfo.getSaler_refund_rule());
        packageOrderItem.setSettle_price(productSalesDayInfo.getSettle_price());
        EpSalesInfo saleInfo = bookingOrderManager.getSalePrice(allDaysSales.get(0), salesInfo.getEp_id());
        EpSalesInfo buyInfo = bookingOrderManager.getBuyingPrice(allDaysSales.get(0), order.getBuy_ep_id());
        Assert.notNull(saleInfo, "该产品未正确配置");
        Assert.notNull(buyInfo, "该产品未正确配置");
        packageOrderItem.setSale_price(order.getFrom_type() == OrderConstant.FromType.TRUST ? buyInfo.getShop_price() : buyInfo.getPrice());
        packageOrderItem.setSupply_price(saleInfo.getPrice());
        packageOrderItem.setUpdate_time(new Date());

        // 锁定库存集合(统一锁定)
        Map<Integer, LockStockDto> lockStockDtoMap = new HashMap<>();
        List<ProductSearchParams> lockParams = new ArrayList<>();

        // 组装参数
        OrderItem orderItem = new OrderItem();
        orderItem.setId(packageOrderItem.getId());
        orderItem.setPro_sub_number(packageOrderItem.getProduct_sub_code());
        orderItem.setStart(packageOrderItem.getStart());
        orderItem.setDays(packageSub.getDays());
        orderItem.setQuantity(packageOrderItem.getQuantity());

        lockStockDtoMap.put(packageOrderItem.getId(), new LockStockDto(orderItem, null, salesInfo.getDay_info_list()));
        lockParams.add(bookingOrderManager.parseParams(orderItem));

        // 创建元素订单
        params.put("order_number", order.getNumber());
        CreateOrderInterface orderInterface = applicationContext.getBean(OrderConstant.CREATE_ADAPTER + "PACKAGE", CreateOrderInterface.class);

        List<Map> items = (List<Map>) params.get("items");

        int buyEpId = createOrder.getEpId();
        for (Map item : items){
            ValidateProductSub sub = orderInterface.parseItemParams(createOrder, item);
            // 元素产品购买者为打包商
            createOrder.setEpId(salesInfo.getEp_id());
            this.createOrderItem(orderInterface, createOrder, order, sub, item, lockStockDtoMap, lockParams);
        }
        createOrder.setEpId(buyEpId);

        // 创建订单联系人
        orderInterface.insertShipping(params, order);

        // 锁定库存
        Result<Map<Integer, List<Boolean>>> lockResult = productSalesPlanRPCService.lockProductStocks(lockParams);
        if (!lockResult.isSuccess()) {
            throw new ApiException(lockResult.getError());
        }

        Map<Integer, List<Boolean>> listMap = lockResult.get();
        if (listMap.size() != lockStockDtoMap.size()) {
            throw new ApiException("锁定套票库存异常");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        // 检查是否待审核
        checkCreateAudit(lockStockDtoMap, listMap, orderItems);

        // 更新订单状态
        for (OrderItem item : orderItems) {
            if (item.getStatus() == OrderConstant.OrderItemStatus.AUDIT_WAIT) {
                order.setStatus(OrderConstant.OrderStatus.AUDIT_WAIT);
                break;
            }
        }

        // 更新审核时间
        if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT && order.getAudit_time() == null) {
            order.setAudit_time(new Date());
        }

        // 执行后事
        boolean ok = orderInterface.after(params, order);

        // 触发事件
        eventManager.addEvent(OrderConstant.EventType.ORDER_CREATE, order.getId());

        // 存入元素产品支付金额与销售金额
        packageOrderItem.setPay_amount(order.getPay_amount());
        packageOrderItem.setSale_amount(order.getSale_amount());
        // 设置订单金额
        order.setPay_amount(salesInfo.getPay_type() == ProductConstants.PayType.PREPAY  ? price.getSale() : 0);
        order.setSale_amount(price.getSale());

        orderMapper.updateByPrimaryKeySelective(order);

        packageOrderItemMapper.updateByPrimaryKeySelective(packageOrderItem);

        createPackageOrderService.insertSalesChain(packageOrderItem, packageSub, allDaysSales);
        // 预分账记录
        createPackageOrderService.prePaySplitAccount(allDaysSales, packageOrderItem, createOrder.getEpId());

        Result<Object> result = new Result<>(Boolean.TRUE);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", order);
        resultMap.put("items", orderItems);
        result.put(resultMap);

        if (ok) {
            resultMap.clear();
            resultMap.put("t_order", CommonUtil.oneToList(order));
            resultMap.put("t_order_item", orderItems);
            result.putExt(Result.SYNC_DATA, resultMap);
        }

        log.info(OrderConstant.LogOperateCode.NAME, bookingOrderManager.orderLog(order.getId(), null,
                order.getBuy_ep_id(), order.getBuy_ep_name(), OrderConstant.LogOperateCode.CREATE_SUCCESS,
                null, String.format("订单创建成功:%s", JsonUtils.toJson(params)), null));
        return result;
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
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @JobTask
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
            throw new ApiException("子订单不在可重新发票状态,当前状态为:" + OrderConstant.OrderItemStatus.getName(orderItem.getStatus()));
        }

        String phone = CommonUtil.objectParseString(params.get("phone"));

        int ok = orderItemMapper.resendTicket(orderItem.getId());
        if (ok <= 0) {
            log.warn("订单:{} 重新发票失败,已超过最大次数或太快", orderItem.getNumber());
            throw new ApiException("已超过最大次数或太快");
        }
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
    @JobTask
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
            throw new ApiException("子订单不在可重新发票状态,当前状态为:" + OrderConstant.OrderItemStatus.getName(orderItem.getStatus()));
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
        jobManager.addJob(OrderConstant.Actions.SEND_GROUP_TICKET, Collections.singleton(jobMap));
        return new Result<>(true);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> modifyTicketForGroup(Map params) {
        String orderItemSn = params.get("order_item_sn").toString();
        Long sn = Long.valueOf(orderItemSn);

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemSn, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.modifyTicketForGroup(params, sn);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<?> consumeHotelBySupplier(Map params) {
        String orderItemSn = params.get("order_item_sn").toString();
        Long sn = Long.valueOf(orderItemSn);

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemSn, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.consumeHotelTicket(params, sn);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<?> consumeLineBySupplier(Map params) {
        String orderItemSn = params.get("order_item_sn").toString();
        Long sn = Long.valueOf(orderItemSn);

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemSn, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.consumeLineTicket(params, sn);
        } finally {
            lock.unlock();
        }
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
            if (orderItemDetail == null)
                continue;
            List<ProductSalesDayInfo> dayInfoList = lockStockDto.getDayInfoList();
            if (booleanList.size() != orderItemDetail.size() || booleanList.size() != dayInfoList.size()) {
                throw new ApiException(String.format("锁库存天数:%d与购买天数:%d不匹配", booleanList.size(), orderItemDetail.size()))
                        .dataMap().putData("lockDays", booleanList.size()).putData("buyDays", orderItemDetail.size());
            }
            for (Boolean oversell : booleanList) {
                OrderItemDetail detail = orderItemDetail.get(i);
                ProductSalesDayInfo dayInfo = dayInfoList.get(i);
                detail.setOversell(oversell);
                // 如果是超卖则把子订单状态修改为待审
                if (((oversell && dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.OVERSELL_NEED_AUDIT) ||
                        dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.ANYWAY_NEED_AUDIT ||
                        dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.ANYWAY_NEED_AUDIT_OVERSELL ||
                        dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.ANYWAY_NEED_AUDIT_NOT_OVERSELL) &&
                        item.getStatus() == OrderConstant.OrderItemStatus.AUDIT_SUCCESS) {
                    item.setStatus(OrderConstant.OrderItemStatus.AUDIT_WAIT);
                    orderItemMapper.updateByPrimaryKeySelective(item);
                    eventManager.addEvent(OrderConstant.EventType.ORDER_WAIT_AUDIT, item.getId());
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
        Result result = new Result(true);
        if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND && response != null) {
            // 改为自己发送短信
            try {
                switch (orderItem.getPro_type()) {
                    case ProductConstants.ProductType.SCENERY:
                        response.setPhone(phone);
                        smsManager.sendVoucher(orderItem, response);
                        break;
                    case ProductConstants.ProductType.HOTEL:
                        smsManager.sendHotelSendTicket(orderItem, phone);
                        break;
                    case ProductConstants.ProductType.ITINERARY:
                        smsManager.sendLineSendTicket(orderItem, phone);
                        break;
                    default:
                        result.setError("改产品不支持重新发票");
                }
            } catch (Exception e) {
                result.setError(e.getMessage());
                Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
                Map params = smsManager.parseParams(orderItem.getVoucher_msg(), order, orderItem, response, orderItem.getQuantity());
                result.put(params);
            }
            return result;
        }
        // 出票
        // 记录任务
        Map<String, String> jobParam = new HashMap<>();
        jobParam.put("orderItemId", orderItem.getId().toString());
        jobManager.addJob(OrderConstant.Actions.SEND_TICKET, Collections.singleton(jobParam));
        return result;
    }
}
