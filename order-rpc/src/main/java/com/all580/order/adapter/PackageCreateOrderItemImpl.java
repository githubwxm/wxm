package com.all580.order.adapter;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.PriceDto;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.ArrayList;
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
        if (productType == ProductConstants.ProductType.PACKAGE) return;
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
    public ProductSalesInfo validateProductAndGetSales(ValidateProductSub sub, CreateOrder createOrder, Map item) {
        Integer productType = CommonUtil.objectParseInteger(item.get("product_type"));
        if (productType != ProductConstants.ProductType.PACKAGE){
            //如果不是套票走各自的验证查询
            return this.getCreateOrderInterface(productType).validateProductAndGetSales(sub, createOrder,item);
        }
        ProductSearchParams packageParam = new ProductSearchParams();
        packageParam.setSubProductCode(sub.getCode());
        packageParam.setStartDate(sub.getBooking());
        packageParam.setDays(sub.getDays());
        packageParam.setQuantity(sub.getQuantity());
        packageParam.setBuyEpId(createOrder.getEpId());
        List<ProductSearchParams> searchParams = new ArrayList<>();
        List<Map> maps = (List<Map>) item.get("items");
        for (Map map : maps){
            ValidateProductSub productSub = this.parseItemParams(createOrder, map);
            ProductSearchParams productSearchParams = new ProductSearchParams();
            productSearchParams.setSubProductCode(productSub.getCode());
            productSearchParams.setStartDate(productSub.getBooking());
            searchParams.add(productSearchParams);
        }

        Result<ProductSalesInfo> salesInfoResult = productSalesPlanRPCService.validateProductSalesInfo(packageParam, searchParams);
        if (!salesInfoResult.isSuccess()) {
            throw new ApiException(salesInfoResult.getError());
        }
        ProductSalesInfo salesInfo = salesInfoResult.get();
        // 判断供应商状态是否为已冻结
        if (!bookingOrderManager.isEpStatus(epService.getEpStatus(salesInfo.getEp_id()), EpConstant.EpStatus.ACTIVE)) {
            throw new ApiException("供应商企业已冻结");
        }
        if (salesInfo.getDay_info_list().size() != sub.getDays()) {
            throw new ApiException("预定天数与获取产品天数不匹配");
        }
        // 验证最低购票
        if (salesInfo.getMin_buy_quantity() != null && salesInfo.getMin_buy_quantity() > sub.getQuantity()) {
            throw new ApiException("低于最低购买票数");
        }
        // 判断最高票数
        if (salesInfo.getMax_buy_quantity() != null && salesInfo.getMax_buy_quantity() > 0 && sub.getQuantity() > salesInfo.getMax_buy_quantity()) {
            throw new ApiException(String.format("超过订单最高购买限制: 当前购买:%d, 最大购买:%d", sub.getQuantity(), salesInfo.getMax_buy_quantity())).dataMap()
                    .putData("current", sub.getQuantity()).putData("max", salesInfo.getMax_buy_quantity());
        }
        return salesInfo;
    }

    @Override
    public OrderItem insertItem(Order order, ValidateProductSub sub, ProductSalesInfo salesInfo, PriceDto price, Map item) {
        Integer productType = salesInfo.getProduct_type();
        if (productType == ProductConstants.ProductType.PACKAGE){
            //如果是套票，走景点创建子订单方法
            return this.getCreateOrderInterface(ProductConstants.ProductType.SCENERY).
                    insertItem(order, sub, salesInfo, price, item);
        }
        return this.getCreateOrderInterface(productType).insertItem(order, sub, salesInfo, price, item);
    }

    @Override
    public List<Visitor> insertVisitor(List<?> visitorList, OrderItem orderItem, ProductSalesInfo salesInfo, ValidateProductSub sub, Map item) {
        if (orderItem.getPro_type() == ProductConstants.ProductType.PACKAGE){
            //如果是套票,直接放回null
            return null;
        }
        return this.getCreateOrderInterface(orderItem.getPro_type()).insertVisitor(visitorList, orderItem, salesInfo, sub, item);
    }
}
