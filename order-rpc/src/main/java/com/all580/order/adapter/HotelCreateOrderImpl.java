package com.all580.order.adapter;

import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 酒店创建订单适配器
 * @date 2017/2/7 11:24
 */
@Component(OrderConstant.CREATE_ADAPTER + "HOTEL")
public class HotelCreateOrderImpl extends AbstractCreateOrderImpl {

    @Override
    public void validateVisitor(ProductSalesInfo salesInfo, ValidateProductSub sub, List<?> visitorList, Map item) {

    }

    @Override
    public List<Visitor> insertVisitor(List<?> visitorList, List<OrderItemDetail> details, ProductSalesInfo salesInfo, ValidateProductSub sub, Map item) {
        return null;
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
}
