package com.all580.order.adapter;

import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.Order;
import com.all580.order.entity.PackageOrderItem;
import com.all580.order.entity.PackageOrderItemSalesChain;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by xiangzw on 2017/7/20.
 */
public interface CreatePackageOrderService {

    CreateOrder parseParams(Map params);

    Result validate(CreateOrder createOrder, Map params);

    Order insertOrder(CreateOrder createOrder, Map params);

    ValidateProductSub parseItemParams(CreateOrder createOrder, Map params);

    ProductSalesInfo validateProductAndGetSales(ValidateProductSub sub, CreateOrder createOrder, Map params);

    void validateBookingDate(ValidateProductSub sub, List<ProductSalesDayInfo> dayInfoList);

    PackageOrderItem insertPackageOrderInfo(ProductSalesInfo salesInfo, Order order, Map params);

    List<PackageOrderItemSalesChain> insertSalesChain(PackageOrderItem item, ValidateProductSub sub, List<List<EpSalesInfo>> allDaysSales);

    void prePaySplitAccount(List<List<EpSalesInfo>> allDaysSales, PackageOrderItem item, Integer epId);

}
