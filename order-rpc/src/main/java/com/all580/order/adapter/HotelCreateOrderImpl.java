package com.all580.order.adapter;

import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dto.PriceDto;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.util.CommonUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 酒店创建订单适配器
 * @date 2017/2/7 11:24
 */
@Component(OrderConstant.CREATE_ADAPTER + OrderConstant.OrderAdapter.HOTEL)
public class HotelCreateOrderImpl extends AbstractCreateOrderImpl {

    @Override
    public void validateVisitor(ProductSalesInfo salesInfo, ValidateProductSub sub, List<?> visitorList, Map item) {

    }

    @Override
    public List<Visitor> insertVisitor(List<?> visitorList, OrderItem orderItem, ProductSalesInfo salesInfo, ValidateProductSub sub, Map item) {
        return null;
    }

    @Override
    public OrderItem insertItem(Order order, ValidateProductSub sub, ProductSalesInfo salesInfo, PriceDto price, Map item) {
        Date endTime = DateUtils.addDays(sub.getBooking(), sub.getDays());
        return bookingOrderManager.generateItem(salesInfo, endTime, price.getSale(), sub.getBooking(), sub.getDays(), order.getId(), sub.getQuantity(), sub.getGroupId(), CommonUtil.objectParseString(item.get("memo")), sub.isSend());
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
