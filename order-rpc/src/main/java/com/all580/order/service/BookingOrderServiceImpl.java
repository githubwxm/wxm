package com.all580.order.service;

import com.all580.order.adapter.CreateOrderInterface;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.dao.*;
import com.all580.order.dto.*;
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
import org.springframework.util.CollectionUtils;

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
    @Autowired
    private MnsEventAspect eventManager;
    @Autowired
    private JobAspect jobManager;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @MnsEvent
    public Result<?> create(Map params, String type) throws Exception {
        CreateOrderInterface orderInterface = applicationContext.getBean(OrderConstant.CREATE_ADAPTER + type, CreateOrderInterface.class);
        Assert.notNull(orderInterface, type + " 类型创建订单适配器没找到");
        // 锁定库存集合(统一锁定)
        Map<Integer, LockStockDto> lockStockDtoMap = new HashMap<>();
        List<ProductSearchParams> lockParams = new ArrayList<>();
        //创建终端订单
        CreateOrderResultDto createOrderResult = this.createOrder(orderInterface, params, lockStockDtoMap, lockParams);
        //终端订单
        Order mainOrder = createOrderResult.getOrder();
        List<OrderItem> mainItems = createOrderResult.getOrderItems();

        Map<Order, List<OrderItem>> orderListMap = new HashMap<>();
        orderListMap.put(createOrderResult.getOrder(), createOrderResult.getOrderItems());
        //如果是套票订单，循环创建层级订单
        //递归调用
        while(createOrderResult.isHasPackageOrderItem()){
            List<OrderItem> packageOrderItem = createOrderResult.getPackageOrderItems();
            createOrderResult = this.createPackageOrderItem(orderInterface, params, packageOrderItem, lockStockDtoMap, lockParams);
            if (!CollectionUtils.isEmpty(createOrderResult.getPackageCreateOrders())){
                for (CreateOrderResultDto resultDto : createOrderResult.getPackageCreateOrders()) {
                    //收集所有订单及对应的子订单，以备处理订单审核等订单流程
                    orderListMap.put(resultDto.getOrder(), resultDto.getOrderItems());
                }
            }
        }
        // 锁定库存
        Result<Map<Integer, List<Boolean>>> lockResult = productSalesPlanRPCService.lockProductStocks(lockParams);
        if (!lockResult.isSuccess()) {
            throw new ApiException(lockResult.getError());
        }
        Map<Integer, List<Boolean>> listMap = lockResult.get();
        if (listMap.size() != lockStockDtoMap.size()) {
            throw new ApiException("锁定库存异常");
        }
        List<OrderItem> allItems = new ArrayList<>();
        // 检查是否待审核
        checkCreateAudit(lockStockDtoMap, listMap, allItems);
        //处理所有订单是否待审核
        for (Order order : orderListMap.keySet()){
            List<OrderItem> orderItems = orderListMap.get(order);
            // 更新订单状态
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getStatus() == OrderConstant.OrderItemStatus.AUDIT_WAIT
                        || orderItem.getPro_type() == ProductConstants.ProductType.PACKAGE) {
                    order.setStatus(OrderConstant.OrderStatus.AUDIT_WAIT);
                    break;
                }
            }
            // 更新审核时间
            if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT && order.getAudit_time() == null) {
                order.setAudit_time(new Date());
            }
            orderMapper.updateByPrimaryKeySelective(order);

            // 触发事件
            eventManager.addEvent(OrderConstant.EventType.ORDER_CREATE, order.getId());

            log.info(OrderConstant.LogOperateCode.NAME, bookingOrderManager.orderLog(order.getId(), null,
                    order.getBuy_ep_id(), order.getBuy_ep_name(), OrderConstant.LogOperateCode.CREATE_SUCCESS,
                    null, String.format("订单创建成功:%s", JsonUtils.toJson(params)), null));
        }
        //逐级检查关联订单
        List<CreateOrderResultDto> resultDtoList = createOrderResult.getPackageCreateOrders();
        if (resultDtoList != null){
            List<Order> orderList = new ArrayList<>();//最底层的所有元素订单
            for (CreateOrderResultDto resultDto : resultDtoList) {
                orderList.add(resultDto.getOrder());
            }
            //逐级处理套票关联订单的审核状态
            bookingOrderManager.checkAuditOrderChainForPackage(orderList);
        }

        Result<Object> result = new Result<>(Boolean.TRUE);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", mainOrder);
        resultMap.put("items", mainItems);
        result.put(resultMap);
        // 执行后事
        boolean ok = orderInterface.after(params, mainOrder);
        if (ok) {
            Map<String, Collection<?>> data = new HashMap<>();
            data.put("t_order", CommonUtil.oneToList(mainOrder));
            data.put("t_order_item", mainItems);
            result.putExt(Result.SYNC_DATA, data);
        }
        return result;
    }

    /**
     * 创建套票元素订单
     * @param orderInterface
     * @param params
     * @param packageOrderItem
     * @param lockStockDtoMap
     * @param lockParams
     * @return
     */
    private CreateOrderResultDto createPackageOrderItem(CreateOrderInterface orderInterface, Map params, List<OrderItem> packageOrderItem,
                                                       Map<Integer, LockStockDto> lockStockDtoMap, List<ProductSearchParams> lockParams){
        //处理套票元素订单信息
        Map<String, List<Map>> itemMap = new HashMap<>();//套票子产品与元素产品信息键值对
        List<Map> mapList = orderInterface.getOrderItemParams(params);
        for (Map item : mapList) {
            String productSubCode = CommonUtil.objectParseString(item.get("product_sub_code"));
            itemMap.put(productSubCode, orderInterface.getOrderItemParams(item));
        }

        CreateOrderResultDto createOrderResultDto = new CreateOrderResultDto();
        List<CreateOrderResultDto> resultDtoList = new ArrayList<>();
        for (OrderItem oi : packageOrderItem){
            //组装元素订单创建参数
            params = bookingOrderManager.gerneratePackageItemParams(params, oi, itemMap);

            CreateOrderResultDto resultDto = this.createOrder(orderInterface, params, lockStockDtoMap, lockParams);
            //保存套票与元素订单之间的关联
            bookingOrderManager.insertPackageOrderChain(oi.getOrder_id(), resultDto.getOrder().getId());
            //保存套票与元素子订单之间的关联
            bookingOrderManager.insertPackageOrderItemChain(oi, resultDto.getOrderItems());

            if (createOrderResultDto.getPackageOrderItems() == null){
                createOrderResultDto.setPackageOrderItems(resultDto.getPackageOrderItems());
            }else {
                createOrderResultDto.getPackageOrderItems().addAll(resultDto.getPackageOrderItems());
            }
            //收集元素订单，以备处理元素套票订单的创建
            resultDtoList.add(resultDto);
        }
        createOrderResultDto.setPackageCreateOrders(resultDtoList);
        createOrderResultDto.setHasPackageOrderItem(!CollectionUtils.isEmpty(createOrderResultDto.getPackageOrderItems()));

        return createOrderResultDto;
    }

    /**
     * 创建主订单
     * @param orderInterface
     * @param params
     * @param lockStockDtoMap
     * @param lockParams
     * @return
     */
    private CreateOrderResultDto createOrder(CreateOrderInterface orderInterface, Map<String, Object> params,
                                             Map<Integer, LockStockDto> lockStockDtoMap, List<ProductSearchParams> lockParams){
        CreateOrder createOrder = orderInterface.parseParams(params);

        Result validateResult = orderInterface.validate(createOrder, params);
        if (!validateResult.isSuccess()) {
            throw  new ApiException(validateResult.getError());
        }

        // 创建订单
        Order order = orderInterface.insertOrder(createOrder, params);
        // 获取子订单
        List<Map> items = orderInterface.getOrderItemParams(params);
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItem> packageOrderItems = new ArrayList<>();
        ValidateProductSub first = null;
        for (Map item : items) {
            ValidateProductSub sub = orderInterface.parseItemParams(createOrder, item);
            if (first == null) first = sub;
            OrderItem orderItem = this.createOrderItem(orderInterface, createOrder, order, sub, item, lockStockDtoMap, lockParams);
            orderItems.add(orderItem);
            //套票需要创建元素订单的子订单
            if (orderItem.getPro_type() == ProductConstants.ProductType.PACKAGE){
                packageOrderItems.add(orderItem);
            }
        }
        if (first == null) {
            throw new ApiException("请选择购买的产品");
        }

        // 创建订单联系人
        orderInterface.insertShipping(params, order);

        CreateOrderResultDto result = new CreateOrderResultDto();
        result.setOrder(order);
        result.setOrderItems(orderItems);
        if (CollectionUtils.isEmpty(packageOrderItems)){
            result.setHasPackageOrderItem(Boolean.FALSE);
        }else {
            result.setHasPackageOrderItem(Boolean.TRUE);
            result.setPackageOrderItems(packageOrderItems);
        }
        return result;
    }

    /**
     * 创建子订单
     * @param orderInterface
     * @param createOrder
     * @param order
     * @param sub
     * @param item
     * @param lockStockDtoMap
     * @param lockParams
     * @return
     */
    private OrderItem createOrderItem(CreateOrderInterface orderInterface, CreateOrder createOrder, Order order,ValidateProductSub sub,
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

        // 设置最小生效日期和最大失效日期
        orderItem.setMin_effective_date(details.get(0).getEffective_date());
        orderItem.setMax_expiry_date(details.get(details.size() - 1).getExpiry_date());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        // 析构销售链
        orderInterface.insertSalesChain(orderItem, sub, allDaysSales);

        orderInterface.insertVisitor(visitors, orderItem, salesInfo, sub, item);

        lockStockDtoMap.put(orderItem.getId(), new LockStockDto(orderItem, details, salesInfo.getDay_info_list()));
        lockParams.add(bookingOrderManager.parseParams(orderItem));

        // 预分账记录
        bookingOrderManager.prePaySplitAccount(allDaysSales, orderItem, createOrder.getEpId());

        return orderItem;
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

        Order order = orderMapper.selectBySN(Long.parseLong(orderSn));
        //套票元素订单不能单独支付
        Order pOrder = orderMapper.selectPackageOrderById(order.getId());
        if (pOrder != null){
            throw new ApiException("非法请求:当前订单不能单独支付");
        }
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
        if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND && response != null) {
            ReSendTicketParams reSendTicketParams = new ReSendTicketParams();
            reSendTicketParams.setOrderSn(orderItem.getNumber());
            reSendTicketParams.setVisitorId(visitorId);
            reSendTicketParams.setMobile(phone);
            reSendTicketParams.setMaOrderId(response.getMa_order_id());
            reSendTicketParams.setMaProductId(response.getMa_product_id());
            reSendTicketParams.setVoucher(response.getVoucher_value());
            return voucherRPCService.resendTicket(orderItem.getEp_ma_id(), reSendTicketParams);
        }
        // 出票
        // 记录任务
        Map<String, String> jobParam = new HashMap<>();
        jobParam.put("orderItemId", orderItem.getId().toString());
        jobManager.addJob(OrderConstant.Actions.SEND_TICKET, Collections.singleton(jobParam));
        return new Result(true);
    }
}
