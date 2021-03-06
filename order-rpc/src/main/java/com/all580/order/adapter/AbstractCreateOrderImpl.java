package com.all580.order.adapter;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.PriceDto;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.*;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.text.ParseException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/7 10:47
 */
public abstract class AbstractCreateOrderImpl implements CreateOrderInterface {
    protected BookingOrderManager bookingOrderManager;

    protected EpService epService;
    protected ProductSalesPlanRPCService productSalesPlanRPCService;

    public abstract void setBookingOrderManager(BookingOrderManager bookingOrderManager);
    public abstract void setEpService(EpService epService);
    public abstract void setProductSalesPlanRPCService(ProductSalesPlanRPCService productSalesPlanRPCService);

    /**
     * 转换参数(这里不做参数验证,参数验证在网管层 已经处理了)
     * @param params
     * @return
     */
    public CreateOrder parseParams(Map params) {
        CreateOrder createOrder = new CreateOrder();
        createOrder.setEpId(CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)));
        createOrder.setCoreEpId(CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        createOrder.setFrom(CommonUtil.objectParseInteger(params.get("from")));
        createOrder.setRemark(CommonUtil.objectParseString(params.get("remark")));
        createOrder.setOperatorId(CommonUtil.objectParseInteger(params.get("operator_id")));
        createOrder.setOperatorName(CommonUtil.objectParseString(params.get("operator_name")));
        createOrder.setOuter(CommonUtil.objectParseString(params.get("outer_id")));
        createOrder.setSource(CommonUtil.objectParseInteger(params.get("source")));
        // 获取下单企业名称
        String buyEpName = null;
        Result<Map<String, Object>> epResult = epService.selectId(createOrder.getEpId());
        if (epResult != null && epResult.isSuccess() && epResult.get() != null) {
            buyEpName = String.valueOf(epResult.get().get("name"));
        }
        createOrder.setEpName(buyEpName);
        return createOrder;
    }

    @Override
    public List<Map> getOrderItemParams(Map params) {
        return (List<Map>) params.get("items");
    }

    public ValidateProductSub parseItemParams(CreateOrder createOrder, Map item) {
        ValidateProductSub sub = new ValidateProductSub();
        sub.setCode(Long.parseLong(item.get("product_sub_code").toString()));
        sub.setDays(CommonUtil.objectParseInteger(item.get("days")));
        sub.setQuantity(CommonUtil.objectParseInteger(item.get("quantity")));
        sub.setGroupId(CommonUtil.objectParseInteger(item.get("group_id")));
        String sendMsg = CommonUtil.objectParseString(item.get("send_msg"));
        if (StringUtils.isEmpty(sendMsg) || BooleanUtils.toBooleanObject(sendMsg) == null) {
            Result<Boolean> result = epService.isSendVoucher(createOrder.getEpId(), createOrder.getCoreEpId());
            sub.setSend(result.get());
        } else {
            sub.setSend(BooleanUtils.toBooleanObject(sendMsg));
        }
        sub.setMemo(CommonUtil.objectParseString(item.get("memo")));
        try {
            sub.setBooking(DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, CommonUtil.objectParseString(item.get("start"))));
        } catch (ParseException e) {
            throw new ApiException("创建订单解析子订单数据异常:日期转换错误", e);
        }
        return sub;
    }

    public Result validate(CreateOrder createOrder, Map params) {
        // 判断销售商状态是否为已冻结
        if (!bookingOrderManager.isEpStatus(epService.getEpStatus(createOrder.getEpId()), EpConstant.EpStatus.ACTIVE)) {
            throw new ApiException("销售商企业已冻结");
        }

        // 只有销售商可以下单
        Result<Integer> epType = epService.selectEpType(createOrder.getEpId());
        //对于套票自动创建元素订单的处理的处理
        Integer productType = CommonUtil.objectParseInteger(params.get("product_type"), 0);
        boolean isPackageAutoOrder = (productType == ProductConstants.ProductType.PACKAGE &&
                (bookingOrderManager.isEpType(epType, EpConstant.EpType.SELLER) ||
                        bookingOrderManager.isEpType(epType, EpConstant.EpType.PLATFORM)));
        if (!bookingOrderManager.isEpType(epType, EpConstant.EpType.SELLER) &&
                !bookingOrderManager.isEpType(epType, EpConstant.EpType.OTA) && !isPackageAutoOrder) {
            throw new ApiException("该企业不能购买产品");
        }

        if (StringUtils.isNotEmpty(createOrder.getOuter())) {
            Order order = bookingOrderManager.selectByOuter(createOrder.getEpId(), createOrder.getOuter());
            if (order != null) {
                Result result = new Result(false);
                result.setCode(Result.UNIQUE_KEY_ERROR);
                Map<String, Object> map = new HashMap<>();
                map.put("t_order", JsonUtils.obj2map(order));
                map.put("items", JsonUtils.json2List(JsonUtils.toJson(bookingOrderManager.selectByOrder(order.getId()))));
                result.put(map);
                return result;
            }
        }
        return new Result(true);
    }

    public Order insertOrder(CreateOrder createOrder, Map params) {
        return bookingOrderManager.generateOrder(createOrder.getCoreEpId(), createOrder.getEpId(), createOrder.getEpName(),
                createOrder.getOperatorId(), createOrder.getOperatorName(), createOrder.getFrom(), createOrder.getRemark(),
                createOrder.getOuter(),createOrder.getSource());
    }

    public ProductSalesInfo validateProductAndGetSales(ValidateProductSub sub, CreateOrder createOrder, Map item) {
        // 验证是否可售
        ProductSearchParams searchParams = new ProductSearchParams();
        searchParams.setSubProductCode(sub.getCode());
        searchParams.setStartDate(sub.getBooking());
        searchParams.setDays(sub.getDays());
        searchParams.setQuantity(sub.getQuantity());
        searchParams.setBuyEpId(createOrder.getEpId());
        Result<ProductSalesInfo> salesInfoResult = productSalesPlanRPCService.validateProductSalesInfo(searchParams);
        if (!salesInfoResult.isSuccess()) {
            throw new ApiException(salesInfoResult.getError());
        }
        ProductSalesInfo salesInfo = salesInfoResult.get();
        // 判断供应商状态是否为已冻结
        if (!bookingOrderManager.isEpStatus(epService.getEpStatus(salesInfo.getEp_id()), EpConstant.EpStatus.ACTIVE)) {
            throw new ApiException(salesInfo.getProduct_sub_name() + "供应商企业已冻结");
        }
        if (salesInfo.getDay_info_list().size() != sub.getDays()) {
            throw new ApiException(salesInfo.getProduct_sub_name() + "预定天数与获取产品天数不匹配");
        }
        // 验证最低购票
        if (salesInfo.getMin_buy_quantity() != null && salesInfo.getMin_buy_quantity() > sub.getQuantity()) {
            throw new ApiException(salesInfo.getProduct_sub_name() + "低于最低购买票数");
        }
        // 判断最高票数
        if (salesInfo.getMax_buy_quantity() != null && salesInfo.getMax_buy_quantity() > 0 && sub.getQuantity() > salesInfo.getMax_buy_quantity()) {
            throw new ApiException(String.format(salesInfo.getProduct_sub_name() + "超过订单最高购买限制: 当前购买:%d, 最大购买:%d", sub.getQuantity(), salesInfo.getMax_buy_quantity())).dataMap()
                    .putData("current", sub.getQuantity()).putData("max", salesInfo.getMax_buy_quantity());
        }
        return salesInfo;
    }

    public void validateBookingDate(ValidateProductSub sub, ProductSalesInfo salesInfo) {
        bookingOrderManager.validateBookingDate(sub.getBooking(), salesInfo);
    }

    public OrderItem insertItem(Order order, ValidateProductSub sub, ProductSalesInfo salesInfo, PriceDto price, Map item) {
        Date endTime = salesInfo.getDay_info_list().get(salesInfo.getDay_info_list().size() - 1).getEnd_time();
        return bookingOrderManager.generateItem(salesInfo, endTime, price.getSale(), sub.getBooking(), sub.getDays(),
                order.getId(), sub.getQuantity(), sub.getGroupId(), sub.getMemo(), sub.isSend());
    }

    @Override
    public List<OrderItemDetail> insertDetail(Order order, CreateOrder createOrder, OrderItem item, ValidateProductSub sub, ProductSalesInfo salesInfo, List<List<EpSalesInfo>> allDaysSales) {
        List<OrderItemDetail> details = new ArrayList<>();
        int i = 0;
        for (ProductSalesDayInfo dayInfo : salesInfo.getDay_info_list()) {
            List<EpSalesInfo> daySales = allDaysSales.get(i);
            Assert.notEmpty(daySales, salesInfo.getProduct_sub_name() + "产品销售计划不全");
            EpSalesInfo saleInfo = bookingOrderManager.getSalePrice(daySales, item.getSupplier_ep_id());
            EpSalesInfo buyInfo = bookingOrderManager.getBuyingPrice(daySales, createOrder.getEpId());
            Assert.notNull(saleInfo, salesInfo.getProduct_sub_name() + "产品未正确配置");
            Assert.notNull(buyInfo, salesInfo.getProduct_sub_name() + "产品未正确配置");
            OrderItemDetail orderItemDetail = bookingOrderManager.generateDetail(dayInfo, item,
                    DateUtils.addDays(sub.getBooking(), i), sub.getQuantity(), salesInfo.getLow_use_quantity(),
                    saleInfo.getPrice(), order.getFrom_type() == OrderConstant.FromType.TRUST ? buyInfo.getShop_price() : buyInfo.getPrice());
            details.add(orderItemDetail);
            i++;
        }
        return details;
    }

    @Override
    public List<OrderItemSalesChain> insertSalesChain(OrderItem item, ValidateProductSub sub, List<List<EpSalesInfo>> allDaysSales) {
        List<OrderItemSalesChain> chains = new ArrayList<>();
        for (int i = 0; i < allDaysSales.size(); i++) {
            List<EpSalesInfo> daySales = allDaysSales.get(i);
            Date day = DateUtils.addDays(sub.getBooking(), i);
            Set<Integer> eps = new HashSet<>();
            for (EpSalesInfo daySale : daySales) {
                if (!eps.contains(daySale.getEp_id())) {
                    chains.add(bookingOrderManager.generateChain(item, daySale.getEp_id(), day, daySales));
                    eps.add(daySale.getEp_id());
                }
                if (!eps.contains(daySale.getSale_ep_id())) {
                    chains.add(bookingOrderManager.generateChain(item, daySale.getSale_ep_id(), day, daySales));
                    eps.add(daySale.getSale_ep_id());
                }
            }
        }
        return chains;
    }

    @Override
    public List<Visitor> insertVisitor(List<?> visitorList, OrderItem orderItem, ProductSalesInfo salesInfo, ValidateProductSub sub, Map item) {
        List<Visitor> visitors = new ArrayList<>();
        for (Object o : visitorList) {
            visitors.add(bookingOrderManager.generateVisitor((Map) o, orderItem.getId()));
        }
        return visitors;
    }

    @Override
    public Shipping insertShipping(Map params, Order order) {
        Map shippingMap = (Map) params.get("shipping");
        return bookingOrderManager.generateShipping(shippingMap, order.getId());
    }

    @Override
    public boolean after(Map params, Order order) {
        if (isCheck(params)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            bookingOrderManager.getEventManager().rollback();
            return false;
        }
        return true;
    }

    public boolean isCheck(Map params) {
        Object object = params.get("check");
        if (object != null) {
            return StringUtils.isNumeric(object.toString()) ? BooleanUtils.toBoolean(Integer.parseInt(object.toString())) : BooleanUtils.toBoolean(object.toString());
        }
        return false;
    }
}
