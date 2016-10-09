package com.all580.order.service;

import com.all580.ep.api.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.dao.*;
import com.all580.order.dto.GenerateAccountDto;
import com.all580.order.dto.LockStockDto;
import com.all580.order.entity.*;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private EpService epService;

    @Override
    public Result<?> create(Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, bookingOrderManager.generateCreateOrderValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("创建订单参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }

        try {
            Integer buyEpId = (Integer) params.get("ep_id");
            Integer from = (Integer) params.get("from");
            int totalPrice = 0;
            int totalShopPrice = 0;

            // 判断销售商状态是否为已冻结
            if (!bookingOrderManager.isEpStatus(epService.getEpStatus(buyEpId), EpConstant.EpStatus.ACTIVE)) {
                return new Result<>(false, "销售商企业已冻结");
            }
            // 锁定库存集合(统一锁定)
            List<LockStockDto> lockStockDtoList = new ArrayList<>();
            // 更新子订单
            Map<Integer, OrderItem> updateOrderItemMap = new HashMap<>();

            // 创建订单
            Order order = bookingOrderManager.generateOrder(buyEpId, (Integer) params.get("user_id"), (String) params.get("user_name"), from);

            // 获取子订单
            List<Map> items = (List<Map>) params.get("items");
            for (Map item : items) {
                Integer productSubId = (Integer) item.get("product_sub_id");
                Integer quantity = (Integer) item.get("quantity");
                Integer days = (Integer) item.get("days");
                //预定日期
                Date bookingDate = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, (String) item.get("start"));

                // 验证是否可售
                Result<ProductSalesInfo> salesInfoResult = productSalesPlanRPCService.validateProductSalesInfo(productSubId, bookingDate, days, quantity);
                if (salesInfoResult.hasError()) {
                    return salesInfoResult;
                }
                ProductSalesInfo salesInfo = salesInfoResult.get();

                // 判断供应商状态是否为已冻结
                if (!bookingOrderManager.isEpStatus(epService.getEpStatus(salesInfo.getEpId()), EpConstant.EpStatus.ACTIVE)) {
                    return new Result<>(false, "供应商企业已冻结");
                }

                // 判断游客信息
                List<Map> visitors = (List<Map>) item.get("visitor");
                if (salesInfo.isRequireSid()) {
                    Result visitorResult = bookingOrderManager.validateVisitor(
                            visitors, productSubId, bookingDate, salesInfo.getSidDayCount(), salesInfo.getSidDayQuantity());
                    if (visitorResult.hasError()) {
                        return visitorResult;
                    }
                }

                // 获取销售商价格
                EpSalesInfo info = bookingOrderManager.getBuyingPrice(salesInfo.getSales(), buyEpId);
                totalPrice += info.getPrice() * quantity * days;
                totalShopPrice += info.getShopPrice() * quantity * days;

                // 创建子订单
                OrderItem orderItem = bookingOrderManager.generateItem(salesInfo, info, days, order.getId(), quantity);
                orderItemMapper.insert(orderItem);
                lockStockDtoList.add(new LockStockDto(orderItem.getId(), productSubId, bookingDate, days, quantity));
                updateOrderItemMap.put(orderItem.getId(), orderItem);

                // 创建子订单详情
                for (Integer i = 0; i < days; i++) {
                    OrderItemDetail orderItemDetail = bookingOrderManager.generateDetail(salesInfo, orderItem.getId(), DateUtils.addDays(bookingDate, i), quantity);
                    orderItemDetailMapper.insert(orderItemDetail);
                }

                // 预分账记录
                List<OrderItemAccount> accounts = new ArrayList<>();
                // 平台商之间的分账
                Map<Integer, List<EpSalesInfo>> coreEpMap = new HashMap<>();
                for (EpSalesInfo epSalesInfo : salesInfo.getSales()) {
                    Integer buyCoreEpId = bookingOrderManager.getCoreEpId(epService.selectPlatformId(epSalesInfo.getBuyEpId()));
                    Integer saleCoreEpId = bookingOrderManager.getCoreEpId(epService.selectPlatformId(epSalesInfo.getSaleEpId()));
                    if (buyCoreEpId == null || saleCoreEpId == null) {
                        return new Result<>(false, "企业平台商不存在");
                    }
                    if (epSalesInfo.getBuyEpId() == buyCoreEpId && saleCoreEpId == epSalesInfo.getSaleEpId()) {
                        GenerateAccountDto accountDto = new GenerateAccountDto(
                                buyCoreEpId, saleCoreEpId, orderItem.getId(),
                                saleCoreEpId, saleCoreEpId, epSalesInfo.getPrice() * quantity,
                                bookingOrderManager.getProfit(salesInfo.getSales(), buyCoreEpId),
                                bookingOrderManager.getProfit(salesInfo.getSales(), saleCoreEpId));
                        accounts.addAll(bookingOrderManager.generateAccount(accountDto));
                    }

                    if (buyCoreEpId == saleCoreEpId && epSalesInfo.getSaleEpId() != saleCoreEpId) {
                        List<EpSalesInfo> infoList = coreEpMap.get(saleCoreEpId);
                        if (infoList == null) {
                            infoList = new ArrayList<>();
                        }
                        infoList.add(epSalesInfo);
                        coreEpMap.put(saleCoreEpId, infoList);
                    }
                }

                // 平台商内部分账
                for (Integer coreEpId : coreEpMap.keySet()) {
                    List<EpSalesInfo> infoList = coreEpMap.get(coreEpId);
                    if (infoList != null && !infoList.isEmpty()) {
                        for (EpSalesInfo epSalesInfo : infoList) {
                            int profit = bookingOrderManager.getProfit(salesInfo.getSales(), epSalesInfo.getSaleEpId()) * quantity;
                            GenerateAccountDto accountDto = new GenerateAccountDto(
                                    coreEpId, coreEpId, orderItem.getId(),
                                    epSalesInfo.getSaleEpId(), coreEpId,
                                    profit, 0, profit);
                            accounts.addAll(bookingOrderManager.generateAccount(accountDto));
                        }
                    }
                }

                // 插入分账记录
                for (OrderItemAccount account : accounts) {
                    orderItemAccountMapper.insert(account);
                }

                // 创建游客信息
                for (Map v : visitors) {
                    Visitor visitor = bookingOrderManager.generateVisitor(v, orderItem.getId());
                    visitorMapper.insert(visitor);
                }
            }

            // 创建订单联系人
            Map shippingMap = (Map) params.get("shipping");
            Shipping shipping = bookingOrderManager.generateShipping(shippingMap, order.getId());
            shippingMapper.insert(shipping);

            for (LockStockDto lockStockDto : lockStockDtoList) {
                // 锁定库存
                Result<Boolean> lockResult = productSalesPlanRPCService.lockProductStock(
                        lockStockDto.getProductSubId(), lockStockDto.getBookingDate(), lockStockDto.getDays(), lockStockDto.getQuantity());
                if (lockResult.hasError()) {
                    return lockResult;
                }
                // 判断是否需要审核
                if (!lockResult.get()) {
                    OrderItem item = updateOrderItemMap.get(lockStockDto.getOrderItemId());
                    item.setStatus(OrderConstant.OrderItemStatus.AUDIT_SUCCESS);
                    orderItemMapper.updateByPrimaryKey(item);
                }
            }

            // 更新订单状态
            for (OrderItem orderItem : updateOrderItemMap.values()) {
                if (orderItem.getStatus() == OrderConstant.OrderItemStatus.AUDIT_WAIT) {
                    order.setStatus(OrderConstant.OrderStatus.AUDIT_WAIT);
                    break;
                }
            }

            // 更新订单金额
            order.setPayAmount(from == OrderConstant.FromType.TRUST ? totalShopPrice : totalPrice);
            order.setSaleAmount(totalPrice);
            orderMapper.updateByPrimaryKey(order);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("order", order);
            resultMap.put("items",updateOrderItemMap.values());
            Result<Object> result = new Result<>(true);
            result.put(resultMap);
            return result;
        } catch (Exception e) {
            log.error("创建订单异常", e);
            return new Result<>(false);
        }
    }

    @Override
    public Result<?> audit(Map params) {
        return null;
    }

    @Override
    public Result<?> payment(Map params) {
        return null;
    }

    @Override
    public Result<?> resendTicket(Map params) {
        return null;
    }
}
