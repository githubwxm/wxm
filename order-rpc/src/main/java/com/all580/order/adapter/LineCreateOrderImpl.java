package com.all580.order.adapter;

import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.LineGroupMapper;
import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.PriceDto;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.LineGroup;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.LineInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.lang.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description:
 * @date 17-5-22 下午3:23
 */
@Component(OrderConstant.CREATE_ADAPTER + "LINE")
public class LineCreateOrderImpl extends TicketCreateOrderImpl {
    @Autowired
    private LineGroupMapper lineGroupMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Override
    public OrderItem insertItem(Order order, ValidateProductSub sub, ProductSalesInfo salesInfo, PriceDto price, Map item) {
        LineInfo lineInfo = salesInfo.getLine();
        Assert.notNull(lineInfo, "没有获取到该线路产品信息");
        // 查询是否有当天的团队
        Integer coreEpId = bookingOrderManager.getCoreEpId(bookingOrderManager.getCoreEpId(salesInfo.getEp_id()));
        Assert.notNull(coreEpId, "获取供应商平台异常");
        String bookingDate = DateFormatUtils.DATE_FORMAT.format(sub.getBooking());
        String number = String.format("%s%s%s", lineInfo.getTeam_prefix(), bookingDate.replace("-", ""), lineInfo.getTeam_suffix());
        LineGroup group = lineGroupMapper.selectByNumber(number, salesInfo.getEp_id(), coreEpId);
        if (group == null) {
            group = new LineGroup();
            BeanUtils.copyProperties(lineInfo, group);
            group.setCore_ep_id(coreEpId);
            group.setCreate_time(new Date());
            group.setEp_id(salesInfo.getEp_id());
            group.setNumber(number);
            group.setBooking_date(bookingDate);
            group.setId(null);
            group.setStatus(OrderConstant.LineGroupStatus.NONE);
            lineGroupMapper.insertSelective(group);
        }
        sub.setGroupId(group.getId());
        return super.insertItem(order, sub, salesInfo, price, item);
    }

    @Override
    public List<Visitor> insertVisitor(List<?> visitorList, OrderItem orderItem, ProductSalesInfo salesInfo, ValidateProductSub sub, Map item) {
        List<Visitor> visitors = new ArrayList<>();
        for (Object o : visitorList) {
            Map v = (Map) o;
            v.put("group_id", sub.getGroupId());
            visitors.add(bookingOrderManager.generateVisitor(v, orderItem.getId()));
        }
        return visitors;
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
        ProductSalesInfo salesInfo = super.validateProductAndGetSales(sub, createOrder, item);
        if (salesInfo.getProduct_type() != ProductConstants.ProductType.ITINERARY){
            throw new ApiException("线路创建异常:" );
        }
        return salesInfo;
    }
}
