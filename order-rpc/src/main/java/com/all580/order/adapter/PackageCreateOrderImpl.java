package com.all580.order.adapter;

import com.all580.ep.api.conf.EpConstant;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.PackageOrderItemMapper;
import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemSalesChain;
import com.all580.order.entity.PackageOrderItem;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzw on 2017/7/20.
 */
@Component
public class PackageCreateOrderImpl  implements CreatePackageOrderService {

    @Autowired
    private PackageOrderItemMapper packageOrderItemMapper;

    @Resource(name= OrderConstant.CREATE_ADAPTER + "PACKAGE",type = CreateOrderInterface.class)
    private PackageCreateOrderItemImpl packageCreateOrderItemService;

    @Override
    public CreateOrder parseParams(Map params) {
        return packageCreateOrderItemService.parseParams(params);
    }

    @Override
    public Result validate(CreateOrder createOrder, Map params) {
        return packageCreateOrderItemService.validate(createOrder, params);
    }

    @Override
    public Order insertOrder(CreateOrder createOrder, Map params) {
        return packageCreateOrderItemService.insertOrder(createOrder, params);
    }

    @Override
    public ValidateProductSub parseItemParams(CreateOrder createOrder, Map item) {
        return packageCreateOrderItemService.parseItemParams(createOrder, item);
    }

    @Override
    public ProductSalesInfo validateProductAndGetSales(ValidateProductSub sub, CreateOrder createOrder, Map item) {
        return packageCreateOrderItemService.validateProductAndGetSales(sub, createOrder, item);
    }

    @Override
    public void validateBookingDate(ValidateProductSub sub, List<ProductSalesDayInfo> dayInfoList) {
        packageCreateOrderItemService.validateBookingDate(sub, dayInfoList);
    }

    @Override
    public PackageOrderItem insertPackageOrderInfo(ProductSalesInfo salesInfo, Order order, Map params) {
        PackageOrderItem packageOrderItem = new PackageOrderItem();
        packageOrderItem.setEp_id(CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)));
        packageOrderItem.setCore_ep_id(CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        packageOrderItem.setOrder_number(order.getNumber());
        packageOrderItem.setProduct_sub_name(salesInfo.getProduct_sub_name());
        packageOrderItem.setProduct_sub_id(salesInfo.getProduct_sub_id());
        packageOrderItem.setProduct_sub_code(salesInfo.getProduct_sub_code());
        packageOrderItem.setProduct_name(salesInfo.getProduct_name());
        packageOrderItem.setQuantity(CommonUtil.objectParseInteger(params.get("quantity")));

        packageOrderItemMapper.insertSelective(packageOrderItem);
        return packageOrderItem;
    }

    @Override
    public List<OrderItemSalesChain> insertSalesChain(PackageOrderItem item, ValidateProductSub sub, List<List<EpSalesInfo>> allDaysSales) {
//        OrderItem orderItem = new OrderItem();
//        orderItem.setId(item.getId());
//        return packageCreateOrderItemService.insertSalesChain(orderItem, sub, allDaysSales);
        return null;
    }

    @Override
    public void prePaySplitAccount(List<List<EpSalesInfo>> allDaysSales, PackageOrderItem item, Integer epId) {
        OrderItem orderItem = new OrderItem();

    }
}
