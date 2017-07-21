package com.all580.order.adapter;

import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.PriceDto;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzw on 2017/7/13.
 */
@Component(OrderConstant.CREATE_ADAPTER + "PACKAGE")
public class PackageCreateOrderItemImpl extends AbstractCreateOrderImpl{

    @Resource(name=OrderConstant.CREATE_ADAPTER + "TICKET",type = CreateOrderInterface.class)
    private CreateOrderInterface ticketCreateOrder;
    @Resource(name=OrderConstant.CREATE_ADAPTER + "HOTEL",type = CreateOrderInterface.class)
    private CreateOrderInterface hotelCreateOrder;
    @Resource(name=OrderConstant.CREATE_ADAPTER + "LINE",type = CreateOrderInterface.class)
    private CreateOrderInterface lineCreateOrder;
    @Resource
    private OrderMapper orderMapper;

    private CreateOrderInterface getCreateOrderInterface(Integer productType){
        if(ProductConstants.ProductType.SCENERY == productType){
            return this.ticketCreateOrder;
        }
        if(ProductConstants.ProductType.HOTEL == productType){
            return this.hotelCreateOrder;
        }
        if(ProductConstants.ProductType.ITINERARY == productType){
            return this.lineCreateOrder;
        }
        throw new ApiException("未找到对应产品类型的订单接口");
    }

    @Override
    public void validateVisitor(ProductSalesInfo salesInfo, ValidateProductSub sub, List<?> visitorList, Map item) {
        Integer productType = salesInfo.getProduct_type();
        this.getCreateOrderInterface(productType).validateVisitor(salesInfo,sub,visitorList,item);
    }

    @Override
    @Autowired
    public void setBookingOrderManager(BookingOrderManager bookingOrderManager) {
        super.bookingOrderManager = bookingOrderManager;
    }

    @Override
    @Autowired
    public void setEpService(EpService epService) {
        super.epService = epService;
    }

    @Override
    @Autowired
    public void setProductSalesPlanRPCService(ProductSalesPlanRPCService productSalesPlanRPCService) {
        super.productSalesPlanRPCService = productSalesPlanRPCService;
    }

    @Override
    public Order insertOrder(CreateOrder createOrder, Map params) {
        Object orderNumber = params.get("order_number");
        if (orderNumber == null){
            return super.insertOrder(createOrder, params);
        }
        return orderMapper.selectBySN(Long.parseLong(orderNumber.toString()));
    }

    @Override
    public OrderItem insertItem(Order order, ValidateProductSub sub, ProductSalesInfo salesInfo, PriceDto price, Map item) {
        Integer productType = salesInfo.getProduct_type();
        return this.getCreateOrderInterface(productType).insertItem(order, sub, salesInfo, price, item);
    }

    @Override
    public List<Visitor> insertVisitor(List<?> visitorList, OrderItem orderItem, ProductSalesInfo salesInfo, ValidateProductSub sub, Map item) {
        return this.getCreateOrderInterface(orderItem.getPro_type()).insertVisitor(visitorList, orderItem, salesInfo, sub, item);
    }
}
