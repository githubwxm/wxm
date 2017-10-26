package com.all580.order.adapter;

import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 门票创建订单适配器
 * @date 2017/2/7 11:24
 */
@Component(OrderConstant.CREATE_ADAPTER + OrderConstant.OrderAdapter.TICKET)
public class TicketCreateOrderImpl extends AbstractCreateOrderImpl {

    @Override
    public void validateVisitor(ProductSalesInfo salesInfo, ValidateProductSub sub, List<?> visitorList, Map item) {
        Assert.notEmpty(visitorList);
        if (salesInfo.isRequire_sid()) {
            int total = bookingOrderManager.validateVisitor(
                    (List<Map>) visitorList, salesInfo.getProduct_sub_code(), sub.getBooking(), salesInfo.getSid_day_count(), salesInfo.getSid_day_quantity());
            if (total != sub.getAllQuantity()) {
                throw new ApiException(salesInfo.getProduct_sub_name() + "游客票数与总票数不符");
            }
        }
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
