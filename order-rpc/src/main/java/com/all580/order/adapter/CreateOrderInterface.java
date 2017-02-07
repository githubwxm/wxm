package com.all580.order.adapter;

import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.PriceDto;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.*;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;

import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/7 11:46
 */
public interface CreateOrderInterface {

    CreateOrder parseParams(Map params);

    ValidateProductSub parseItemParams(Map item);

    void validate(CreateOrder createOrder, Map params);

    Order insertOrder(CreateOrder createOrder, Map params);

    ProductSalesInfo validateProductAndGetSales(ValidateProductSub sub, CreateOrder createOrder, Map item);

    void validateBookingDate(ValidateProductSub sub, List<ProductSalesDayInfo> dayInfoList);

    void validateVisitor(ProductSalesInfo salesInfo, ValidateProductSub sub, List<?> visitorList, Map item);

    OrderItem insertItem(Order order, ValidateProductSub sub, ProductSalesInfo salesInfo, PriceDto price, Map item);

    List<OrderItemDetail> insertDetail(OrderItem item, ValidateProductSub sub, ProductSalesInfo salesInfo);

    List<Visitor> insertVisitor(List<?> visitorList, List<OrderItemDetail> details, ProductSalesInfo salesInfo, ValidateProductSub sub, Map item);

    Shipping insertShipping(Map params, Order order);
}
